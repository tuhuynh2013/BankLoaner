package com.example.tuhuynh.myapplication.agent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.customer.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.customer.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.CustomUtil;

public class AgentApplicationActivity extends AppCompatActivity implements CustomerProfileCallBack {

    TextView tvApplicationID, tvMonth, tvAmount, tvInterest, tvDate, tvStatus;
    TextView tvCustomerName, tvSurname, tvCompany, tvEmployment, tvSalary,
            tvGender, tvPhone, tvAddress, tvIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_appication);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial element
        initialElement();

        // Get info from Assigned Application Activity
        Intent intent = this.getIntent();
        ApplicationInfo applicationInfo = (ApplicationInfo) intent.getSerializableExtra("application");

        setApplicationInfo(applicationInfo);

        // Retrieve customer profile from db
        new CustomerProfileAsyncTask(this, this, applicationInfo.getCustomer()).execute();

    }

    /**
     *
     */
    private void initialElement() {
        // Application section
        tvApplicationID = findViewById(R.id.tv_application_id);
        tvMonth = findViewById(R.id.tv_month);
        tvAmount = findViewById(R.id.tv_amount);
        tvInterest = findViewById(R.id.tv_interest);
        tvDate = findViewById(R.id.tv_date);
        tvStatus = findViewById(R.id.tv_status);

        // Customer section
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvSurname = findViewById(R.id.tv_surname);
        tvCompany = findViewById(R.id.tv_company);
        tvEmployment = findViewById(R.id.tv_employment);
        tvSalary = findViewById(R.id.tv_salary);
        tvGender = findViewById(R.id.tv_gender);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvIdentity = findViewById(R.id.tv_identity);
    }

    /**
     *
     */
    private void setApplicationInfo(ApplicationInfo applicationInfo) {
        String strID = Integer.toString(applicationInfo.getId());
        String strMonth = Integer.toString(applicationInfo.getMonth());
        String strAmount = CustomUtil.convertLongToFormattedString(applicationInfo.getAmount());
        String strInterest = Double.toString(applicationInfo.getInterest());
        String strDate = CustomUtil.convertDateToString(applicationInfo.getDate(), "dd-MM-yyyy");

        tvApplicationID.setText(strID);
        tvAmount.setText(strAmount);
        tvMonth.setText(strMonth);
        tvInterest.setText(strInterest);
        tvDate.setText(strDate);
        tvStatus.setText(applicationInfo.getStatus());

    }


    @Override
    public void callBack(CustomerProfile output) {
        String strSalary = CustomUtil.convertLongToFormattedString(output.getSalary());

        tvCustomerName.setText(output.getName());
        tvSurname.setText(output.getSurname());
        tvCompany.setText(output.getCompany());
        tvEmployment.setText(output.getEmployment());
        tvSalary.setText(strSalary);
        tvGender.setText(output.getGender());
        tvPhone.setText(output.getPhone());
        tvAddress.setText(output.getAddress());
        tvIdentity.setText(output.getIdentity());
    }


}
