package com.example.tuhuynh.myapplication.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.asynctask.CustomerRegisterAsync;
import com.example.tuhuynh.myapplication.asynctask.CustomerRegisterCallBack;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements CustomerRegisterCallBack {

    EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    TextView tvLogin;
    Button btnRegister;
    private ProgressDialog progressDialog;

    // Defining firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(getString(R.string.title_register));

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // Initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If userProfile pressed on button register, we will register the userProfile to server
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If userProfile pressed on login, we will open the login screen
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    /**
     * Function uses to validate input and process register
     */
    private void registerUser() {

        final String capitalName;
        String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(name)) {
            edtName.setError(getString(R.string.error_invalid_name));
            edtName.requestFocus();
            return;
        } else {
            capitalName = CustomUtil.capitalFirstLetter(name);
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.error_empty_email));
            edtEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getString(R.string.error_empty_password));
            edtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError(getString(R.string.error_empty_password));
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtPassword.setError(getString(R.string.error_not_match_password));
            edtPassword.requestFocus();
            return;
        }

        // If the email and password are not empty, displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        // Creating a new userProfile
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            UserProfile userProfile = new UserProfile();
                            userProfile.setId(user.getUid());
                            userProfile.setName(capitalName);
                            userProfile.setEmail(user.getEmail());
                            userProfile.setRole(UserRole.CUSTOMER);
                            userProfile.setAccountType(AccountType.DEFAULT);
                            writeCustomerProfileToDB(userProfile);
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                edtPassword.setError(Objects.requireNonNull(task.getException()).getMessage());
                                edtPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                edtEmail.setError(Objects.requireNonNull(task.getException()).getMessage());
                                edtEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                edtEmail.setError(Objects.requireNonNull(task.getException()).getMessage());
                                edtEmail.requestFocus();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void writeCustomerProfileToDB(UserProfile userProfile) {
        new CustomerRegisterAsync(this, this, userProfile).execute();
    }

    @Override
    public void responseFromCustomerRegister(String msg) {
        if (msg.equalsIgnoreCase(getString(R.string.db_customer_register_success))) {
            finish();
            startActivity(new Intent(this, UserProfileEditorActivity.class));
        } else {
            CustomUtil.displayToast(this, msg);
        }
    }


}
