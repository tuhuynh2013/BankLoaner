package com.example.tuhuynh.myapplication.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtResetEmail;
    TextView tvMessage;
    Button btnResetPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle(getString(R.string.title_reset_pass));

        // Initial element
        edtResetEmail = findViewById(R.id.edt_reset_email);
        tvMessage = findViewById(R.id.tv_msg);
        btnResetPass = findViewById(R.id.btn_reset_password);

        mAuth = FirebaseAuth.getInstance();

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void resetPassword() {
        String email = edtResetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtResetEmail.setError(getString(R.string.error_empty_email));
            edtResetEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtResetEmail.setError(getString(R.string.error_invalid_email));
            edtResetEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tvMessage.setVisibility(View.VISIBLE);
                            tvMessage.setText(getString(R.string.msg_sent));
                            btnResetPass.setEnabled(false);
                        } else {
                            tvMessage.setVisibility(View.VISIBLE);
                            tvMessage.setText(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }


}
