package com.example.tuhuynh.myapplication.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.customer.CustomerHomeActivity;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtUsername, edtEmail, edtIdentity, edtPassword, edtConfirmPassword;
    TextView tvLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edt_username);
        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtIdentity = findViewById(R.id.edt_identity);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If user pressed on button register, we will register the user to server
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If user pressed on login, we will open the login screen
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    /**
     * Function uses to validate input and process register
     */
    private void registerUser() {
        final String username = edtUsername.getText().toString().trim();
        final String capitalName;
        String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String identity = edtIdentity.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // First we will do the validations
        if (TextUtils.isEmpty(username)) {
            edtUsername.setError(getString(R.string.error_empty_username));
            edtUsername.requestFocus();
            return;
        } else {
            if (CustomUtil.isIncorrectUsername(username)) {
                edtUsername.setError(getString(R.string.error_invalid_username));
                edtUsername.requestFocus();
                return;
            }
        }

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
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            edtEmail.requestFocus();
            return;
        }

        // Check identity number
        if (TextUtils.isEmpty(identity)) {
            edtIdentity.setError(getString(R.string.error_empty_identity));
            edtIdentity.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectIdentity(identity)) {
            edtIdentity.setError(getString(R.string.error_invalid_identity));
            edtIdentity.requestFocus();
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
                params.put("username", username);
                params.put("name", capitalName);
                params.put("email", email);
                params.put("identity", identity);
                params.put("password", password);
                params.put("role", UserRole.CUSTOMER);

                // Return the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
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
                        String userName = userJson.getString("username");
                        String name = userJson.getString("name");
                        String email = userJson.getString("email");
                        String role = userJson.getString("role");

                        // Create object base on user role
                        if (role.equalsIgnoreCase(UserRole.CUSTOMER)) {
                            CustomerProfile customer = new CustomerProfile();
                            customer.setId(userID);
                            customer.setUsername(username);
                            customer.setName(name);
                            customer.setEmail(email);
                            customer.setRole(role);
                            // Storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(customer);
                        } else if (role.equalsIgnoreCase(UserRole.AGENT)) {
                            AgentProfile agent = new AgentProfile();
                            agent.setId(userID);
                            agent.setUsername(username);
                            agent.setName(name);
                            agent.setEmail(email);
                            agent.setRole(role);
                            // Storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(agent);
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
