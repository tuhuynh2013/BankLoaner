package com.example.tuhuynh.myapplication.appication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.util.CustomUtil;

public class ApplicationHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_history);
        setTitle("Application History");

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial elements
        TextView tvApplicationID = findViewById(R.id.tv_application_id);
        TextView tvBankName = findViewById(R.id.tv_bank_name);
        TextView tvMonth = findViewById(R.id.tv_month);
        TextView tvAmount = findViewById(R.id.tv_amount);
        TextView tvInterest = findViewById(R.id.tv_interest);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvMessage = findViewById(R.id.tv_message);

        Intent intent = this.getIntent();
        String bankName = intent.getStringExtra("bankName");
        String strAmount = intent.getStringExtra("amount");
        if (!strAmount.isEmpty()) {
            strAmount = CustomUtil.convertLongToFormattedString(Long.parseLong(strAmount));
        }

        tvApplicationID.setText(intent.getStringExtra("applicationID"));
        tvBankName.setText(bankName);
        tvMonth.setText(intent.getStringExtra("month"));
        tvAmount.setText(strAmount);
        tvInterest.setText(intent.getStringExtra("interest"));
        tvDate.setText(intent.getStringExtra("date"));
        tvMessage.setText(getString(R.string.msg_existed_application, bankName));

    }


}
