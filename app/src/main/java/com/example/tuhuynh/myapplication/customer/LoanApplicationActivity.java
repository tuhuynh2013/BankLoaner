package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.util.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LoanApplicationActivity extends AppCompatActivity implements CustomerProfileCallBack {

    private static final int REQUEST_CODE = 0x9345;
    TextView tvBankName, tvMonth, tvInterest, tvAmount;
    Button btnApply;
    CheckBox ckbAcceptTerm;

    // Getting the current user
    User user = SharedPrefManager.getInstance(this).getUser();
    BankInfo bankInfo;
    String month, amount, interest;
    CustomerProfile customerProfile;
    CustomerProfileAsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial screen elements
        initialElement();

        final Intent intent = this.getIntent();
        bankInfo = (BankInfo) intent.getSerializableExtra("bank");
        month = intent.getStringExtra("month");
        amount = intent.getStringExtra("amount");
        interest = intent.getStringExtra("interest");

        // Set value for text view
        tvBankName.setText(bankInfo.getName());
        tvMonth.setText(month);
        tvAmount.setText(amount);
        tvInterest.setText(interest);

        asyncTask = new CustomerProfileAsyncTask(this, this, user);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ckbAcceptTerm.isChecked()) {
                    // customerProfile == null, it means asyncTask not yet running
                    if (customerProfile == null) {
                        asyncTask.execute();
                    } else {
                        submitApplication();
                    }
                } else {
                    ckbAcceptTerm.setError("");
                }
            }
        });

    }

    @Override
    public void callBack(CustomerProfile output) {
        customerProfile = new CustomerProfile();
        customerProfile = output;
        submitApplication();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                new SubmitApplication().execute();
            }
            Toast.makeText(this, data.getStringExtra("msg"), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initial screen elements
     */
    private void initialElement() {
        tvBankName = findViewById(R.id.tv_bank_name);
        tvMonth = findViewById(R.id.tv_month);
        tvAmount = findViewById(R.id.tv_amount);
        tvInterest = findViewById(R.id.tv_interest);
        btnApply = findViewById(R.id.btn_apply);
        ckbAcceptTerm = findViewById(R.id.ckb_accept_temp);
    }

    /**
     *
     */
    private void submitApplication() {
        // If profile not enough information, move to ProfileEditorActivity
        if (customerProfile.isEmpty()) {
            Intent intent = new Intent(this, ProfileEditorActivity.class);
            intent.putExtra("requestCode", REQUEST_CODE);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            new SubmitApplication().execute();
            System.out.print("success");
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SubmitApplication extends AsyncTask<Void, Void, String> {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Displaying the progress bar while user registers on the server
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            // Get current date & time
            Date currentTime = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String strDate = formatter.format(currentTime);

            // Reformat amount
            Long longAmount = CustomUtil.convertFormattedStringToLong(amount);
            if (longAmount != null)
                amount = Long.toString(longAmount);

            // Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("month", month);
            params.put("amount", amount);
            params.put("interest", interest);
            params.put("user_id", Integer.toString(user.getId()));
            params.put("bank_id", Integer.toString(bankInfo.getId()));
            params.put("date", strDate);

            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_SUBMITAPPLICATION, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Hiding the progressbar after completion
            progressBar.setVisibility(View.GONE);

            try {
                // Converting response to json object
                JSONObject obj = new JSONObject(s);
                String msg = obj.getString("message");

                // If no error in response
                if (!obj.getBoolean("error")) {
                    Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                    intent.putExtra("caller", "SubmitApplication");
                    startActivity(intent);
                    finish();
                } else if (obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.respond_existed_application))) {
                    // Get object from db
                    JSONObject applicationInfo = obj.getJSONObject("application_info");

                    Intent intent = new Intent(getApplicationContext(), ApplicationHistoryActivity.class);
                    intent.putExtra("applicationID", applicationInfo.getString("application_id"));
                    intent.putExtra("month", applicationInfo.getString("month"));
                    intent.putExtra("amount", applicationInfo.getString("amount"));
                    intent.putExtra("interest", applicationInfo.getString("interest"));
                    intent.putExtra("date", applicationInfo.getString("date"));
                    intent.putExtra("bankName", applicationInfo.getString("bank_name"));
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
