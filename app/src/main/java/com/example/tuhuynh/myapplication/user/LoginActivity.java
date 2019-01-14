package com.example.tuhuynh.myapplication.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.agent.AgentHomeActivity;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerHomeActivity;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If the user is already logged in, we will start the proper activity base on user's role
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(this).getUser();
            if (user.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
            } else if (user.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                startActivity(new Intent(getApplicationContext(), AgentHomeActivity.class));
            }
            finish();
        }

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
    }

    private void userLogin() {
        // First getting the values
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        // Validating inputs
        if (!CustomUtil.isCorrectUsername(username)) {
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
                        // Storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        // Starting the profile activity
                        if (user.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                            startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
                        } else if (user.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                            startActivity(new Intent(getApplicationContext(), AgentHomeActivity.class));
                        }
                        finish();
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


}
