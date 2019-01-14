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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView tvUsername;
    EditText edtOldPass, edtNewPass, edtConfirmPass;
    User user = SharedPrefManager.getInstance(this).getUser();
    String oldPass, newPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial elements
        tvUsername = findViewById(R.id.tv_username);
        edtOldPass = findViewById(R.id.edt_old_pass);
        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_new_password);

        tvUsername.setText(user.getUsername());

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
        String confirmPass = edtConfirmPass.getText().toString();

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

        if (!confirmPass.equals(newPass)) {
            edtNewPass.setText("");
            edtConfirmPass.setText("");
            edtConfirmPass.setError(getString(R.string.error_not_match_password));
            edtConfirmPass.requestFocus();
            return;
        }

        new ChangePasswordAsyncTask().execute();

    }

    /**
     *
     */
    @SuppressLint("StaticFieldLeak")
    class ChangePasswordAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressBar progressBar;

        @Override
        protected String doInBackground(Void... voids) {
            // Creating request handler object
            RequestHandler requestHandler = new RequestHandler();
            // Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("id", Integer.toString(user.getId()));
            params.put("username", user.getUsername());
            params.put("old_pass", oldPass);
            params.put("new_pass", newPass);
            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_CHANGE_PASSWORD, params);
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
                String msg = obj.getString("message");

                // If no error in response
                if (!obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.msg_change_pass_success))) {
                    // Starting the ProfileActivity
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                    finish();
                } else if (obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.msg_wrong_old_pass))) {
                    edtOldPass.setError(getString(R.string.msg_wrong_old_pass));
                    edtOldPass.requestFocus();
                    edtOldPass.setText("");
                    edtNewPass.setText("");
                    edtConfirmPass.setText("");
                }

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}
