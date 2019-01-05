package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.util.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileEditorActivity extends AppCompatActivity implements CustomerProfileCallBack {

    private TextView tvUsername, tvEmail;
    private EditText edtName, edtSurname, edtIdentity, edtPhone, edtAddress, edtWorkplace, edtDesignation;
    private RadioGroup rdgGender;
    private RadioButton rdMale, rdFemale;
    private Button btnSave;

    // Getting the current user
    User user = SharedPrefManager.getInstance(this).getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Todo: turn off when come from LoanApplication
        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial for profile editor screen
        initialProfileEditorScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

    }

    /**
     * Initial elements and set its own value
     */
    private void initialProfileEditorScreen() {
        // Initial element
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        edtName = findViewById(R.id.edt_name);
        edtSurname = findViewById(R.id.edt_surname);
        edtIdentity = findViewById(R.id.edt_identity);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        edtWorkplace = findViewById(R.id.edt_workplace);
        edtDesignation = findViewById(R.id.edt_designation);
        rdgGender = findViewById(R.id.rdg_gender);
        rdMale = findViewById(R.id.rd_male);
        rdFemale = findViewById(R.id.rd_female);
        btnSave = findViewById(R.id.btn_save);

        // Retrieve customer profile from db
        new CustomerProfileAsyncTask(this, this, user).execute();

    }

    /**
     * Uses to get user profile from db
     */
    @Override
    public void callBack(CustomerProfile output) {
        // Setting the values to the textviews
        edtName.setText(user.getName());
        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());

        String surname = output.getSurname();
        String identityID = output.getIdentity();
        String gender = output.getGender();
        String phone = output.getPhone();
        String address = output.getAddress();
        String workplace = output.getWorkplace();
        String designation = output.getDesignation();

        // Section to check if values are available to display on screen
        if (!surname.isEmpty()) {
            edtSurname.setText(surname);
        }

        if (!identityID.isEmpty()) {
            edtIdentity.setText(identityID);
        }

        if (!gender.isEmpty()) {
            if (gender.equalsIgnoreCase(rdMale.getText().toString())) {
                rdMale.setChecked(true);
            } else if (gender.equalsIgnoreCase(rdFemale.getText().toString())) {
                rdFemale.setChecked(true);
            } else {
                rdgGender.check(rdMale.getId());
            }
        }

        if (!phone.isEmpty()) {
            edtPhone.setText(phone);
        }

        if (!address.isEmpty()) {
            edtAddress.setText(address);
        }

        if (!workplace.isEmpty()) {
            edtWorkplace.setText(workplace);
        }

        if (!designation.isEmpty()) {
            edtDesignation.setText(designation);
        }

    }

    /**
     *
     */
    @SuppressLint("StaticFieldLeak")
    class UpdateUserProfile extends AsyncTask<Void, Void, String> {

        User updateUser;
        CustomerProfile customerProfile;

        private UpdateUserProfile(User updateUser, CustomerProfile customerProfile) {
            this.updateUser = updateUser;
            this.customerProfile = customerProfile;
        }

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
            HashMap<String, String> params = new HashMap<>();
            params.put("id", Integer.toString(updateUser.getId()));
            params.put("username", updateUser.getUsername());
            params.put("surname", customerProfile.getSurname());
            params.put("identity", customerProfile.getIdentity());
            params.put("gender", customerProfile.getGender());
            params.put("phone", customerProfile.getPhone());
            params.put("address", customerProfile.getAddress());
            params.put("workplace", customerProfile.getWorkplace());
            params.put("designation", customerProfile.getDesignation());
            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_UPDATEUSERPROFILE, params);
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
                if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_update_successfully))) {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_successfully, Toast.LENGTH_LONG).show();
                    returnIntent(Activity.RESULT_OK, getString(R.string.msg_update_successfully));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_error, Toast.LENGTH_LONG).show();
                    returnIntent(Activity.RESULT_CANCELED, getString(R.string.msg_update_error));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     *
     */
    private void updateUserProfile() {

        String name = edtName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String identity = edtIdentity.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String workplace = edtWorkplace.getText().toString().trim();
        String designation = edtDesignation.getText().toString().trim();
        // Get checked value of gender
        RadioButton rd = findViewById(rdgGender.getCheckedRadioButtonId());
        String gender = rd.getText().toString();

        // Check name
        if (TextUtils.isEmpty(name)) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectName(name)) {
            edtName.setError(getString(R.string.error_invalid_name));
            edtName.requestFocus();
            return;
        }

        // Check surname
        if (TextUtils.isEmpty(surname)) {
            edtSurname.setError(getString(R.string.error_empty_surname));
            edtSurname.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectName(surname)) {
            edtSurname.setError(getString(R.string.error_invalid_surname));
            edtSurname.requestFocus();
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

        // Check phone number
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError(getString(R.string.error_empty_phone));
            edtPhone.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectPhone(phone)) {
            edtPhone.setError(getString(R.string.error_invalid_phone));
            edtPhone.requestFocus();
            return;
        }

        // Check address
        if (TextUtils.isEmpty(address)) {
            edtAddress.setError(getString(R.string.error_empty_address));
            edtAddress.requestFocus();
            return;
        }

        // Check workplace
        if (TextUtils.isEmpty(workplace)) {
            edtWorkplace.setError(getString(R.string.error_empty_workplace));
            edtWorkplace.requestFocus();
            return;
        }

        // Check designation
        if (TextUtils.isEmpty(designation)) {
            edtDesignation.setError(getString(R.string.error_empty_designation));
            edtDesignation.requestFocus();
            return;
        }
        CustomerProfile customerProfile = new CustomerProfile(surname, identity, gender, phone, address, workplace, designation);

        UpdateUserProfile updateUserProfile = new UpdateUserProfile(user, customerProfile);
        updateUserProfile.execute();

    }

    /**
     * Return intent to previous activity with message
     *
     * @param resultCode result of action is OK or Canceled
     * @param msg        message
     */
    private void returnIntent(int resultCode, String msg) {
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnIntent(Activity.RESULT_CANCELED, getString(R.string.msg_update_not_successfully));
        super.onBackPressed();
    }

    //Todo: check change and show confirm message to discard


}
