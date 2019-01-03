package com.example.tuhuynh.myapplication.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.tuhuynh.myapplication.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.user.User;

public class LoanApplicationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);

        final Intent intent = this.getIntent();
        final BankInfo bankInfo = (BankInfo) intent.getSerializableExtra("bank");
        final int month = intent.getIntExtra("month", 0);
        final Long amount = intent.getLongExtra("amount", 0);
        final Double interest = intent.getDoubleExtra("interest", 0);
        final User user = (User) intent.getSerializableExtra("user");


    }
}
