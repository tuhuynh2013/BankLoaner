package com.example.tuhuynh.myapplication.customer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.util.CustomUtil;


public class CustomerAppInfoActivity extends AppCompatActivity {

    TextView tvApplicationID, tvBankName, tvMonth, tvAmount, tvInterest, tvDate, tvStatus, tvTitleAgent, tvAgent, tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_app_info);
        setTitle(getString(R.string.title_application_history));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get intent from LoanApplicationActivity
        Intent intent = this.getIntent();
        String caller = intent.getStringExtra("caller");
        ApplicationInfo application = (ApplicationInfo) intent.getSerializableExtra("application");

        initialElement();

        String strID = Integer.toString(application.getId());
        String bankName = application.getBankInfo().getName();
        String strMonth = Integer.toString(application.getMonth());
        String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
        String strInterest = Double.toString(application.getInterest());
        String strDate = CustomUtil.convertDateToString(application.getDate(), "default");
        String status = application.getStatus();

        tvApplicationID.setText(strID);
        tvBankName.setText(bankName);
        tvMonth.setText(strMonth);
        tvAmount.setText(strAmount);
        tvInterest.setText(strInterest);
        tvDate.setText(strDate);
        tvStatus.setText(status);
        tvMessage.setText(getString(R.string.msg_existed_application, bankName));

        if (caller.equalsIgnoreCase("LoanApplicationActivity")) {
            initialElement();
            tvTitleAgent.setVisibility(View.INVISIBLE);
            tvAgent.setVisibility(View.INVISIBLE);
        } else if (caller.equalsIgnoreCase("CustomerAppManagerFragment")) {
            initialElement();
            tvMessage.setVisibility(View.INVISIBLE);

            String agentName = CustomUtil.setFullName(application.getAgent().getName(), application.getAgent().getSurname());
            if (agentName.isEmpty()) {
                tvTitleAgent.setVisibility(View.INVISIBLE);
                tvAgent.setVisibility(View.INVISIBLE);
            } else {
                tvAgent.setText(agentName);
            }
        }

    }

    private void initialElement() {
        // Initial elements
        tvApplicationID = findViewById(R.id.tv_application_id);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvMonth = findViewById(R.id.tv_month);
        tvAmount = findViewById(R.id.tv_amount);
        tvInterest = findViewById(R.id.tv_interest);
        tvDate = findViewById(R.id.tv_date);
        tvStatus = findViewById(R.id.tv_status);
        tvTitleAgent = findViewById(R.id.tv_title_agent);
        tvAgent = findViewById(R.id.tv_agent);
        tvMessage = findViewById(R.id.tv_message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
