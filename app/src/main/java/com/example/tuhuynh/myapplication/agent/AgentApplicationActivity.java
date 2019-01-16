package com.example.tuhuynh.myapplication.agent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.appication.UpdateStatusAsync;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.customer.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.customer.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.CustomUtil;

public class AgentApplicationActivity extends AppCompatActivity implements CustomerProfileCallBack {

    TextView tvApplicationID, tvMonth, tvAmount, tvInterest, tvDate, tvStatus;
    TextView tvCustomerName, tvSurname, tvCompany, tvEmployment, tvSalary,
            tvGender, tvPhone, tvAddress, tvIdentity;
    ImageButton btnApprove, btnRejected;

    ApplicationInfo application;

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
        application = (ApplicationInfo) intent.getSerializableExtra("application");

        // Set value for application section
        setApplicationInfo();

        // Retrieve customer profile from db
        new CustomerProfileAsyncTask(this, this, application.getCustomer()).execute();

        // Set action for approved button
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
                application.setStatus(ApplicationStatus.APPROVED);
                showAlertDialog(getString(R.string.des_approved), getString(R.string.dialog_approved, strAmount));
            }
        });

        // Set action for rejected button
        btnRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.setStatus(ApplicationStatus.REJECTED);
                showAlertDialog(getString(R.string.des_rejected), getString(R.string.dialog_rejected));
            }
        });

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

        // Button section
        btnApprove = findViewById(R.id.btn_approved);
        btnRejected = findViewById(R.id.btn_rejected);
    }

    /**
     *
     */
    private void setApplicationInfo() {
        String strID = Integer.toString(application.getId());
        String strMonth = Integer.toString(application.getMonth());
        String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
        String strInterest = Double.toString(application.getInterest());
        String strDate = CustomUtil.convertDateToString(application.getDate(), "dd-MM-yyyy");

        tvApplicationID.setText(strID);
        tvAmount.setText(strAmount);
        tvMonth.setText(strMonth);
        tvInterest.setText(strInterest);
        tvDate.setText(strDate);
        tvStatus.setText(application.getStatus());

    }

    /**
     * Respond from get customer profile async task
     * and set value for customer info section
     */
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

    /**
     * Create Alert Dialog with custom content
     *
     * @param title     title of dialog
     * @param msgDialog content of dialog
     */
    private void showAlertDialog(String title, String msgDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msgDialog);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new UpdateStatusAsync(getApplicationContext(), application).execute();
                startActivity(new Intent(getApplicationContext(), AssignedApplicationActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
