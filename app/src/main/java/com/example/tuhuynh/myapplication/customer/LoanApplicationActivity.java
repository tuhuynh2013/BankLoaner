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

import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.user.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.user.ProfileEditorActivity;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LoanApplicationActivity extends AppCompatActivity implements GetUserProfileCallBack {

    private static final int REQUEST_CODE = 0x9345;
    TextView tvBankName, tvMonth, tvInterest, tvAmount;
    Button btnApply;
    CheckBox ckbAcceptTerm;

    // Getting the current user
    User user = SharedPrefManager.getInstance(this).getUser();
    BankInfo bankInfo;
    String month, amount, interest;
    CustomerProfile customerProfile;
    GetUserProfileAsync asyncTask;

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

        asyncTask = new GetUserProfileAsync(this, this, user);

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
    public void responseFromAsync(Object object) {
        customerProfile = new CustomerProfile();
        customerProfile = (CustomerProfile) object;
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
        if (customerProfile.isMissingInfo()) {
            Intent intent = new Intent(this, ProfileEditorActivity.class);
            intent.putExtra("caller", "LoanApplication");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            new SubmitApplication().execute();
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
            String strDate = CustomUtil.convertDateToString(currentTime, "default");

            // Reformat amount
            Long longAmount = CustomUtil.convertFormattedStringToLong(amount);
            if (longAmount != null)
                amount = Long.toString(longAmount);

            // Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("month", month);
            params.put("amount", amount);
            params.put("interest", interest);
            params.put("customer_id", Integer.toString(user.getId()));
            params.put("bank_id", Integer.toString(bankInfo.getId()));
            params.put("date", strDate);
            params.put("status", ApplicationStatus.PROGRESSING);

            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_SUBMIT_APPLICATION, params);
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
                if (!obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.msg_submit_success))) {
                    Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                    intent.putExtra("caller", "SubmitApplication");
                    startActivity(intent);
                    finish();
                } else if (obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.respond_existed_application))) {
                    // Get object from db
                    JSONObject applicationJson = obj.getJSONObject("application_info");

                    Intent intent = new Intent(getApplicationContext(), CustomerAppInfoActivity.class);
                    // Convert String date to Date
                    Date date = CustomUtil.convertStringToDate(applicationJson.getString("date"), "default");
                    // Get bank information
                    BankInfo bankInfo = new BankInfo();
                    bankInfo.setName(applicationJson.getString("bank_name"));

                    ApplicationInfo applicationInfo = new ApplicationInfo();
                    applicationInfo.setId(Integer.parseInt(applicationJson.getString("application_id")));
                    applicationInfo.setMonth(Integer.parseInt(applicationJson.getString("month")));
                    applicationInfo.setAmount(Long.parseLong(applicationJson.getString("amount")));
                    applicationInfo.setInterest(Double.parseDouble(applicationJson.getString("interest")));
                    applicationInfo.setDate(date);
                    applicationInfo.setStatus(applicationJson.getString("status"));
                    applicationInfo.setBankInfo(bankInfo);

                    intent.putExtra("caller", "LoanApplicationActivity");
                    intent.putExtra("application", applicationInfo);

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
