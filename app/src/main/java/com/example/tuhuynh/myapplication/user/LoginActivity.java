package com.example.tuhuynh.myapplication.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.agent.AgentHomeActivity;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerHomeActivity;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initial element
        etUsername = findViewById(R.id.edt_username);
        etPassword = findViewById(R.id.edt_password);

        // If user presses on login, calling the method login
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
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


        pDialog = new ProgressDialog(LoginActivity.this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("946395224241-a6e4tp9hlm3392ekj13n3c2lsf09us60.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // If the user is already logged in, we will start the proper activity base on user's role
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            SharedPrefManager.getInstance(this).logout();
            mAuth.signOut();
            // Google sign out
            mGoogleSignInClient.signOut();
        }

    }

    /**
     * Display Progress bar while Logging in
     */

    private void displayProgressDialog() {
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        googleLogin(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        displayProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            googleLogin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login Failed: ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }

                });
    }

    private void googleLogin(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            final String email = user.getEmail();
            final String name = user.getDisplayName();
            // If it passes all the validations
            @SuppressLint("StaticFieldLeak")
            class RegisterUser extends AsyncTask<Void, Void, String> {

                private ProgressBar progressBar;

                @Override
                protected String doInBackground(Void... voids) {
                    // Creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    // Creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("email", email);
                    params.put("role", UserRole.CUSTOMER);

                    // Return the response
                    return requestHandler.sendPostRequest(URLs.URL_GOOGLEREGISTER, params);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    // Displaying the progress bar while user registers on the server
                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    // Hiding the progressbar after completion
                    progressBar.setVisibility(View.GONE);

                    try {
                        // Converting response to json object
                        JSONObject obj = new JSONObject(s);
                        String message = obj.getString("message");

                        // If no error in response
                        if (!obj.getBoolean("error")) {
                            // Getting the user from the response
                            JSONObject userJson = obj.getJSONObject("user");
                            int userID = userJson.getInt("id");
                            String name = userJson.getString("name");
                            String email = userJson.getString("email");
                            String role = userJson.getString("role");

                            // Create object base on user role
                            if (role.equalsIgnoreCase(UserRole.CUSTOMER)) {
                                CustomerProfile customer = new CustomerProfile();
                                customer.setId(userID);
                                customer.setUsername(email);
                                customer.setName(name);
                                customer.setEmail(email);
                                customer.setRole(role);
                                customer.setAccountType(AccountType.GOOGLE);
                                // Storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(customer);
                            }
                            // Starting the profile activity
                            startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Executing the async task
            RegisterUser ru = new RegisterUser();
            ru.execute();
        }
    }

    private void hideProgressDialog() {
        pDialog.dismiss();
    }

    private void userLogin() {
        // First getting the values
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        // Validating inputs
        if (CustomUtil.isIncorrectUsername(username)) {
            etUsername.setError(getString(R.string.error_invalid_username));
            etUsername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_empty_username));
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return;
        }

        // If everything is fine
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... voids) {
                // Creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                // Creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    // Converting response to json object
                    JSONObject obj = new JSONObject(s);
                    String message = obj.getString("message");

                    // If no error in response
                    if (!obj.getBoolean("error") && !message.equalsIgnoreCase(getString(R.string.error_username_password))) {
                        // Getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");
                        // Creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("name"),
                                userJson.getString("email"),
                                userJson.getString("role")
                        );
                        user.setAccountType(AccountType.DEFAULT);
                        // Storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        navigateBaseOnRole(user.getRole());
                    } else {
                        etPassword.getText().clear();
                        etPassword.setError(message);
                        etPassword.requestFocus();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void navigateBaseOnRole(String userRole) {
        if (userRole.equalsIgnoreCase(UserRole.CUSTOMER)) {
            startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
        } else if (userRole.equalsIgnoreCase(UserRole.AGENT)) {
            startActivity(new Intent(getApplicationContext(), AgentHomeActivity.class));
        }
        finish();
    }

}
