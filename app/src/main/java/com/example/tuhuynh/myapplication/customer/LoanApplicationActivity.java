package com.example.tuhuynh.myapplication.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.util.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

public class LoanApplicationActivity extends AppCompatActivity implements CustomerProfileCallBack {

    private static final int REQUEST_CODE = 0x9345;
    TextView tvBankName, tvMonth, tvInterest, tvAmount;
    Button btnApply;
    CheckBox ckbAcceptTerm;

    // Getting the current user
    User user = SharedPrefManager.getInstance(this).getUser();
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
        final BankInfo bankInfo = (BankInfo) intent.getSerializableExtra("bank");
        final String month = intent.getStringExtra("month");
        final String amount = intent.getStringExtra("amount");
        final String interest = intent.getStringExtra("interest");

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
                        runCommit();
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
        runCommit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

            } else {

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

    private void runCommit() {
        // If profile not enough information, move to ProfileEditorActivity
        if (customerProfile.isEmpty()) {
            Intent intent = new Intent(this, ProfileEditorActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            // Todo: commit application
            System.out.print("success");
        }
    }

}
