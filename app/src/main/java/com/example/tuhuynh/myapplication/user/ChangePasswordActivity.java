package com.example.tuhuynh.myapplication.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class ChangePasswordActivity extends AppCompatActivity {

    TextView tvEmail, tvFireBaseException;
    EditText edtOldPass, edtNewPass, edtConfirmPass;
    String oldPass, newPass;
    FirebaseUser user;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle(getString(R.string.title_change_pass));

        // Initializing firebase authentication object
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn() || mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        user = mAuth.getCurrentUser();
        pDialog = new ProgressDialog(this);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial elements
        tvEmail = findViewById(R.id.tv_email);
        tvFireBaseException = findViewById(R.id.tv_firebase_exception);
        edtOldPass = findViewById(R.id.edt_old_pass);
        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_new_password);

        tvEmail.setText(user.getEmail());

        findViewById(R.id.btn_change_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    /**
     *
     */
    private void changePassword() {

        oldPass = edtOldPass.getText().toString();
        newPass = edtNewPass.getText().toString();
        final String confirmPass = edtConfirmPass.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            edtOldPass.setError(getString(R.string.error_empty_password));
            edtOldPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            edtNewPass.setError(getString(R.string.error_empty_password));
            edtNewPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPass)) {
            edtConfirmPass.setError(getString(R.string.error_empty_password));
            edtConfirmPass.requestFocus();
            return;
        }

        if (oldPass.equals(newPass)) {
            edtOldPass.getText().clear();
            edtOldPass.setError(getString(R.string.error_old_equal_new));
            edtOldPass.requestFocus();
            edtNewPass.getText().clear();
            edtConfirmPass.getText().clear();
            return;
        }

        if (!confirmPass.equals(newPass)) {
            edtNewPass.setText("");
            edtConfirmPass.setText("");
            edtConfirmPass.setError(getString(R.string.error_not_match_password));
            edtConfirmPass.requestFocus();
            return;
        }

        displayProgressDialog();
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPass);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updatePassword();
                        } else {
                            displayFireBaseException(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    private void updatePassword() {
        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pDialog.dismiss();
                    CustomUtil.displayToast(getApplicationContext(), getString(R.string.msg_pass_changed));
                    // Closing this activity
                    finish();
                    // Starting login activity
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                } else {
                    displayFireBaseException(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    private void displayFireBaseException(String msg) {
        pDialog.dismiss();
        edtOldPass.getText().clear();
        edtOldPass.requestFocus();
        edtNewPass.getText().clear();
        edtConfirmPass.getText().clear();
        tvFireBaseException.setVisibility(View.VISIBLE);
        tvFireBaseException.setText(msg);
    }

    /**
     * Display Progress bar while Logging in
     */
    private void displayProgressDialog() {
        pDialog.setMessage(getString(R.string.msg_change_pass));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


}
