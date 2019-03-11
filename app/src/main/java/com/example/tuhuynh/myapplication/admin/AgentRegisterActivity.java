package com.example.tuhuynh.myapplication.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.AgentRegisterAsync;
import com.example.tuhuynh.myapplication.asynctask.GetBanksAsync;
import com.example.tuhuynh.myapplication.asynctask.GetBanksCallBack;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.user.AccountType;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.user.UserRole;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AgentRegisterActivity extends AppCompatActivity implements GetBanksCallBack {

    EditText edtName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Button btnRegister;
    private Spinner spinBankName;
    private ProgressDialog progressDialog;
    // Defining firebase auth object
    private FirebaseAuth mAuth;
    BankInfo selectedBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_register);
        setTitle(getString(R.string.title_agent_register));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial views of this activity
        initialViews();

        // Initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If userProfile pressed on button register, we will register the userProfile to server
                registerAgent();
            }
        });

    }

    private void initialViews() {
        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        spinBankName = findViewById(R.id.spin_bank_name);
        btnRegister = findViewById(R.id.btn_register);
        // Get bank info from db
        new GetBanksAsync(this, this).execute();
    }

    @Override
    public void responseFromGetBanks(final List<BankInfo> banks) {
        List<String> bankArrayList = new ArrayList<>();

        for (BankInfo bankInfo : banks) {
            bankArrayList.add(bankInfo.getName());
        }
        // Initial spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, bankArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinBankName.setAdapter(adapter);
        spinBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBank = banks.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Function uses to validate input and process register
     */
    private void registerAgent() {

        final String capitalName;
        String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError(getString(R.string.msg_empty_name));
            edtName.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(name)) {
            edtName.setError(getString(R.string.msg_invalid_name));
            edtName.requestFocus();
            return;
        } else {
            capitalName = CustomUtil.capitalFirstLetter(name);
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.msg_empty_email));
            edtEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.msg_invalid_email));
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError(getString(R.string.msg_empty_phone));
            edtPhone.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectPhone(phone)) {
            edtPhone.setError(getString(R.string.msg_invalid_phone));
            edtPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getString(R.string.msg_empty_password));
            edtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError(getString(R.string.msg_empty_password));
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtPassword.setError(getString(R.string.error_not_match_password));
            edtPassword.requestFocus();
            return;
        }

        if (selectedBank == null) {
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
                            userProfile.setPhone(phone);
                            userProfile.setRole(UserRole.AGENT);
                            userProfile.setAccountType(AccountType.DEFAULT);
                            AgentProfile agentProfile = new AgentProfile(userProfile, selectedBank);
                            new AgentRegisterAsync(agentProfile).execute();
                            finish();
                            startActivity(new Intent(getApplicationContext(), AgentManagementActivity.class));
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


}
