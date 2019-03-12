package com.example.tuhuynh.myapplication.agent;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.asynctask.GetUserFCMTokensAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserFCMTokensCallBack;
import com.example.tuhuynh.myapplication.asynctask.SendNotificationAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateApplicationStatusAsync;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.firebase.NotificationInfo;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.List;


public class AgentAppInfoActivity extends AppCompatActivity implements GetUserProfileCallBack, GetUserFCMTokensCallBack {

    private TextView tvApplicationID, tvMonth, tvAmount, tvInterest, tvDate, tvStatus;
    private TextView tvCustomerName, tvSurname, tvCompany, tvEmployment, tvSalary,
            tvGender, tvPhone, tvAddress, tvIdentity;
    private FloatingActionButton fabApproved, fabRejected;

    private ApplicationInfo application;
    private CustomerProfile customerProfile;
    private UserProfile agentProfile;
    private NotificationInfo notificationInfo;

    private final int REQUEST_CODE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_app_info);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        isLoggedIn();

        // Initial element
        initialElement();

        // Get info from Assigned Application Activity or AgentAppHistory
        Intent intent = this.getIntent();
        application = (ApplicationInfo) intent.getSerializableExtra("application");

        // Set value for application section
        setApplicationInfo();

        // Retrieve customer profile from db
        new GetUserProfileAsync(this, application.getCustomer()).execute();

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
     * Check user is logged in and initial firebase component
     **/
    private void isLoggedIn() {
        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            agentProfile = SharedPrefManager.getInstance(this).getUser();
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

        customerProfile = customer;
    }

    /**
     * Create Alert Dialog with custom content
     *
     * @param title     title of dialog
     * @param msgDialog content of dialog
     */
    private void showAlertDialog(String title, String msgDialog) {
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        input.setHint(getString(R.string.hint_agent_comment));
        input.setSingleLine(false);
        input.setLines(4);
        input.setMaxLines(7);
        input.setGravity(Gravity.START | Gravity.TOP);
        input.setHorizontalScrollBarEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msgDialog);
        builder.setCancelable(false);
        builder.setView(input);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new UpdateApplicationStatusAsync(getApplicationContext(), application).execute();
                String title = getString(R.string.ntf_tile_app_status);
                if (application.getStatus().equalsIgnoreCase(ApplicationStatus.APPROVED)) {
                    // Send notification
                    String ntfMessage = getString(R.string.msg_app_approved, application.getBankInfo().getName());
                    sendNotifications(title, ntfMessage);
                    // Send an email
                    String emailMessage = getString(R.string.email_approved, getProfessionalName(), agentProfile.getName(), application.getBankInfo().getName(), input.getText().toString());
                    sendEmailToCustomer(title, emailMessage);
                } else {
                    // Send notification
                    String msg = getString(R.string.msg_app_rejected, application.getBankInfo().getName());
                    sendNotifications(title, msg);
                    // Send an email
                    String emailMessage = getString(R.string.email_rejected, getProfessionalName(), agentProfile.getName(), application.getBankInfo().getName(), input.getText().toString());
                    sendEmailToCustomer(title, emailMessage);
                }
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

    /**
     *
     **/
    private void sendEmailToCustomer(String title, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", customerProfile.getEmail(), null));
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        try {
            startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AgentAppInfoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generate customer professional name
     **/
    private String getProfessionalName() {
        String customerName;
        if (customerProfile.getGender().equalsIgnoreCase("male")) {
            customerName = "Mr.";
        } else {
            customerName = "Mrs.";
        }
        return customerName.concat(customerProfile.getName());
    }

    /**
     *
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            startActivity(new Intent(getApplicationContext(), AssignedAppsActivity.class));
            finish();
        }
    }

    /**
     * Send notifications to customer. Support FCM for multi-devices
     **/
    private void sendNotifications(String title, String msg) {
        notificationInfo = new NotificationInfo();
        notificationInfo.setTile(title);
        notificationInfo.setMessage(msg);
        new GetUserFCMTokensAsync(this, this, application.getCustomer().getId()).execute();
    }

    @Override
    public void responseFromGetUserFCMTokens(final List<String> tokens) {
        int count = 0;
        for (final String token : tokens) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationInfo.setFcmToken(token);
                    new SendNotificationAsync(notificationInfo).execute();
                }
            }, count);
            count += 5000;
        }
    }


}
