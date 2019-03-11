package com.example.tuhuynh.myapplication.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.tuhuynh.myapplication.admin.AdminHomeActivity;
import com.example.tuhuynh.myapplication.agent.AgentHomeActivity;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.asynctask.AssignFCMTokenAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.GetUserStatusAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserStatusCallBack;
import com.example.tuhuynh.myapplication.asynctask.GoogleRegisterAsync;
import com.example.tuhuynh.myapplication.asynctask.GoogleRegisterCallBack;
import com.example.tuhuynh.myapplication.customer.CustomerHomeActivity;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity implements GetUserProfileCallBack, GoogleRegisterCallBack, GetUserStatusCallBack {

    EditText edtEmail, edtPassword;

    private static final String TAG = "LoginActivity";
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserProfile userProfile;
    private ProgressDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initial element
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        // If user presses on login, calling the method login
        findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultSignIn();
            }
        });

        // If user presses on not registered
        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        // If user presses on Forgot password?
        findViewById(R.id.tv_forgot_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        pDialog = new ProgressDialog(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("946395224241-a6e4tp9hlm3392ekj13n3c2lsf09us60.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.btn_google_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        // If the userProfile is already logged in, we will start the proper activity base on userProfile's role
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            SharedPrefManager.getInstance(this).logout();
            mAuth.signOut();
            // Google sign out
            mGoogleSignInClient.signOut();
        }

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        displayProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acc.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in userProfile's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            userProfile = new UserProfile();
                            userProfile.setId(user.getUid());
                            if (user.getPhotoUrl() != null)
                                userProfile.setProfileImg(user.getPhotoUrl().toString());
                            userProfile.setName(user.getDisplayName());
                            userProfile.setEmail(user.getEmail());
                            userProfile.setAccountType(AccountType.GOOGLE);
                            userProfile.setRole(UserRole.CUSTOMER);
                            registerGoogleAccount();
                            // Add FCM token for user
                            getFCMToken();
                        } else {
                            // If sign in fails, display a message to the userProfile.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            CustomUtil.displayToast(getApplicationContext(), "Login Failed: ");
                        }
                    }

                });
    }

    private void registerGoogleAccount() {
        new GoogleRegisterAsync(this, userProfile).execute();
    }

    @Override
    public void responseFromGoogleRegister(String msg) {
        if (msg.equalsIgnoreCase(getString(R.string.db_login_success)) ||
                msg.equalsIgnoreCase(getString(R.string.db_first_time_google_acc))) {
            setUserSession();
        } else {
            pDialog.dismiss();
        }
        CustomUtil.displayToast(this, msg);
    }

    private void defaultSignIn() {
        // First getting the values
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        // Validating inputs
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.msg_empty_email));
            edtEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!edtEmail.toString().contains("@")) {
                edtEmail.setText(email.concat("@gmail.com"));
                email = edtEmail.getText().toString();
            } else {
                edtEmail.setError(getString(R.string.msg_invalid_email));
                edtEmail.requestFocus();
                return;
            }
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getString(R.string.msg_empty_password));
            edtPassword.requestFocus();
            return;
        }
        // Display progress dialog
        displayProgressDialog();
        new GetUserStatusAsync(this, email).execute();

    }

    @Override
    public void responseFromGetUserStatus(boolean isUserExisted, String status, String msg) {
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        if (isUserExisted) {
            if (status.equalsIgnoreCase(UserStatus.ACTIVE)) {
                // Logging in with email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If the task is successful
                                if (task.isSuccessful()) {
                                    user = mAuth.getCurrentUser();
                                    assert user != null;
                                    userProfile = new UserProfile();
                                    userProfile.setId(user.getUid());
                                    if (user.getPhotoUrl() != null)
                                        userProfile.setProfileImg(user.getPhotoUrl().toString());
                                    userProfile.setEmail(user.getEmail());
                                    userProfile.setAccountType(AccountType.DEFAULT);
                                    setUserSession();
                                    // Add FCM token for user
                                    getFCMToken();
                                } else {
                                    edtPassword.getText().clear();
                                    edtPassword.setError(Objects.requireNonNull(task.getException()).getMessage());
                                    edtPassword.requestFocus();
                                    pDialog.dismiss();
                                }
                            }
                        });
            } else if (status.equalsIgnoreCase(UserStatus.DEACTIVATE)) {
                edtEmail.setError(getString(R.string.msg_user_deactivate));
                edtEmail.requestFocus();
                edtPassword.getText().clear();
                pDialog.dismiss();
            }
        } else {
            edtEmail.setError(msg);
            edtEmail.requestFocus();
            edtPassword.getText().clear();
            pDialog.dismiss();
        }
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                new AssignFCMTokenAsync(userProfile.getId(), instanceIdResult.getToken()).execute();
            }
        });
    }

    private void setUserSession() {
        new GetUserProfileAsync(this, this, userProfile).execute();
    }

    @Override
    public void responseFromGetUserProfile(Object object) {
        UserProfile userProfile = (UserProfile) object;
        // Storing the userProfile in shared preferences
        SharedPrefManager.getInstance(this).userLogin(userProfile);
        // Start the profile activity
        finish();
        CustomUtil.displayToast(this, getString(R.string.db_login_success));
        navigateBaseOnRole(userProfile.getRole());
    }

    /**
     * Navigate user to proper screen base on their role
     */
    private void navigateBaseOnRole(String userRole) {
        if (userRole.equalsIgnoreCase(UserRole.CUSTOMER)) {
            startActivity(new Intent(this, CustomerHomeActivity.class));
        } else if (userRole.equalsIgnoreCase(UserRole.AGENT)) {
            startActivity(new Intent(this, AgentHomeActivity.class));
        } else if (userRole.equalsIgnoreCase(UserRole.ADMIN)) {
            startActivity(new Intent(this, AdminHomeActivity.class));
        }
        finish();
        pDialog.dismiss();
    }

    /**
     * Display Progress bar while Logging in
     */
    private void displayProgressDialog() {
        pDialog.setMessage(getString(R.string.msg_login));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


}
