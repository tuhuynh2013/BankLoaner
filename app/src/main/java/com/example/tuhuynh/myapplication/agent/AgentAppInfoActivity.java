package com.example.tuhuynh.myapplication.agent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.asynctask.UpdateApplicationStatusAsync;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.util.CustomUtil;


public class AgentAppInfoActivity extends AppCompatActivity implements GetUserProfileCallBack {

    TextView tvApplicationID, tvMonth, tvAmount, tvInterest, tvDate, tvStatus;
    TextView tvCustomerName, tvSurname, tvCompany, tvEmployment, tvSalary,
            tvGender, tvPhone, tvAddress, tvIdentity;
    FloatingActionButton fabApproved, fabRejected;

    ApplicationInfo application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_app_info);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial element
        initialElement();

        // Get info from Assigned Application Activity or AgentAppHistory
        Intent intent = this.getIntent();
        application = (ApplicationInfo) intent.getSerializableExtra("application");

        // Set value for application section
        setApplicationInfo();

        // Retrieve customer profile from db
        new GetUserProfileAsync(this, this, application.getCustomer()).execute();

        String caller = intent.getStringExtra("caller");
        if (caller.equalsIgnoreCase("AgentAppHistory")) {
            fabApproved.hide();
            fabRejected.hide();
        } else {
            // Set action for approved button
            fabApproved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
                    application.setStatus(ApplicationStatus.APPROVED);
                    showAlertDialog(getString(R.string.des_approved), getString(R.string.dialog_approved, strAmount));
                }
            });

            // Set action for rejected button
            fabRejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    application.setStatus(ApplicationStatus.REJECTED);
                    showAlertDialog(getString(R.string.des_rejected), getString(R.string.dialog_rejected));
                }
            });
        }

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
        fabApproved = findViewById(R.id.fab_approved);
        fabRejected = findViewById(R.id.fab_rejected);
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
    public void responseFromGetUserProfile(Object object) {
        CustomerProfile customer = (CustomerProfile) object;
        String strSalary = CustomUtil.convertLongToFormattedString(customer.getSalary());

        tvCustomerName.setText(customer.getName());
        tvSurname.setText(customer.getSurname());
        tvCompany.setText(customer.getCompany());
        tvEmployment.setText(customer.getEmployment());
        tvSalary.setText(strSalary);
        tvGender.setText(customer.getGender());
        tvPhone.setText(customer.getPhone());
        tvAddress.setText(customer.getAddress());
        tvIdentity.setText(customer.getIdentity());
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
                new UpdateApplicationStatusAsync(getApplicationContext(), application).execute();
                startActivity(new Intent(getApplicationContext(), AssignedAppsActivity.class));
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
