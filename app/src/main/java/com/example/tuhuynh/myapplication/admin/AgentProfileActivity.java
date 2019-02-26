package com.example.tuhuynh.myapplication.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserStatusAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserStatusCallBack;
import com.example.tuhuynh.myapplication.user.UserStatus;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AgentProfileActivity extends AppCompatActivity implements UpdateUserStatusCallBack {

    TextView tvStatus;
    Button btnActive, btnDeactivate;
    AgentProfile agentProfile;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Get info from Assigned Application Activity or AgentAppHistory
        Intent intent = this.getIntent();
        agentProfile = (AgentProfile) intent.getSerializableExtra("agentProfile");
        setValueForViews();

        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agentProfile.setStatus(UserStatus.ACTIVE);
                showAlertDialog(getString(R.string.btn_active), getString(R.string.dialog_active, agentProfile.getName()));
            }
        });

        btnDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agentProfile.setStatus(UserStatus.DEACTIVATE);
                showAlertDialog(getString(R.string.btn_deactivate), getString(R.string.dialog_deactivate, agentProfile.getName()));
            }
        });

    }

    private void setValueForViews() {
        // Initial views
        TextView tvName, tvSurname, tvEmail, tvIdentity, tvGender, tvPhone, tvAddress, tvBank;
        tvName = findViewById(R.id.tv_name);
        tvSurname = findViewById(R.id.tv_surname);
        tvEmail = findViewById(R.id.tv_email);
        tvIdentity = findViewById(R.id.tv_identity);
        tvGender = findViewById(R.id.tv_gender);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvBank = findViewById(R.id.tv_bank);
        tvStatus = findViewById(R.id.tv_status);
        btnActive = findViewById(R.id.btn_active);
        btnDeactivate = findViewById(R.id.btn_deactivate);

        tvName.setText(agentProfile.getName());
        tvSurname.setText(agentProfile.getSurname());
        tvEmail.setText(agentProfile.getEmail());
        tvIdentity.setText(agentProfile.getIdentity());
        tvGender.setText(agentProfile.getGender());
        tvPhone.setText(agentProfile.getPhone());
        tvAddress.setText(agentProfile.getAddress());
        setStatusColor();
        tvBank.setText(agentProfile.getWorkBank().getName());
        setButton();
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
                updateUserStatus();
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
     */
    private void updateUserStatus() {
        new UpdateUserStatusAsync(this, agentProfile).execute();
    }

    @Override
    public void responseFromUpdateUserStatus(String msg) {
        if (msg.equalsIgnoreCase(getString(R.string.db_user_status_updated))) {
            setStatusColor();
            setButton();
        } else {
            CustomUtil.displayToast(this, msg);
        }
    }

    /**
     *
     */
    private void setStatusColor() {
        tvStatus.setText(agentProfile.getStatus());
        if (agentProfile.getStatus().equalsIgnoreCase(UserStatus.DEACTIVATE)) {
            tvStatus.setTextColor(Color.parseColor("#CC0000"));
        } else {
            tvStatus.setTextColor(Color.parseColor("#009900"));
        }
    }

    /**
     *
     */
    private void setButton() {
        if (agentProfile.getStatus().equalsIgnoreCase(UserStatus.ACTIVE)) {
            btnActive.setEnabled(false);
            btnDeactivate.setEnabled(true);
        } else {
            btnActive.setEnabled(true);
            btnDeactivate.setEnabled(false);
        }
    }


}
