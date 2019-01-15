package com.example.tuhuynh.myapplication.user;

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

import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.customer.CustomerProfileAsyncTask;
import com.example.tuhuynh.myapplication.customer.CustomerProfileCallBack;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileEditorActivity extends AppCompatActivity implements CustomerProfileCallBack {

    private TextView tvUsername;
    private TextView tvEmail;
    private EditText edtName;
    private EditText edtSurname;
    private EditText edtIdentity;
    private EditText edtPhone;
    private EditText edtAddress;
    private EditText edtEmployment;
    private EditText edtCompany;
    private EditText edtSalary;
    private EditText edtBankAccount;
    private RadioGroup rdgGender;
    private RadioButton rdMale, rdFemale;
    private Button btnSave;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            // Getting the current user
            user = SharedPrefManager.getInstance(this).getUser();
        }

        // Turn off Up navigation, when called from LoanApplication
        final Intent intent = this.getIntent();
        int requestCode = intent.getIntExtra("requestCode", 0);
        if (requestCode == 0x9345) {
            // Create Up button
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Initial for profile editor screen
        initialProfileEditorScreen();

        // Retrieve customer profile from db
        new CustomerProfileAsyncTask(this, this, (CustomerProfile) user).execute();

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
        edtName = findViewById(R.id.edt_name);
        edtSurname = findViewById(R.id.edt_surname);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        edtIdentity = findViewById(R.id.edt_identity);
        rdgGender = findViewById(R.id.rdg_gender);
        rdMale = findViewById(R.id.rd_male);
        rdFemale = findViewById(R.id.rd_female);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        TextView tvEmployment = findViewById(R.id.tv_employment);
        edtEmployment = findViewById(R.id.edt_employment);
        TextView tvCompany = findViewById(R.id.tv_company);
        edtCompany = findViewById(R.id.edt_company);
        TextView tvSalary = findViewById(R.id.tv_salary);
        edtSalary = findViewById(R.id.edt_salary);
        TextView tvBankAccount = findViewById(R.id.tv_bank_account);
        edtBankAccount = findViewById(R.id.edt_bank_account);
        TextView tvBank = findViewById(R.id.tv_bank);
        EditText edtBank = findViewById(R.id.edt_bank);
        btnSave = findViewById(R.id.btn_save);

        if (user.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            tvBank.setVisibility(View.INVISIBLE);
            edtBank.setVisibility(View.INVISIBLE);
        } else if (user.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            tvEmployment.setVisibility(View.INVISIBLE);
            edtEmployment.setVisibility(View.INVISIBLE);
            tvCompany.setVisibility(View.INVISIBLE);
            edtCompany.setVisibility(View.INVISIBLE);
            tvSalary.setVisibility(View.INVISIBLE);
            edtSalary.setVisibility(View.INVISIBLE);
            tvBankAccount.setVisibility(View.INVISIBLE);
            edtBankAccount.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Uses to get user profile from db
     * and set content for profile editor screen
     */
    @Override
    public void callBack(CustomerProfile output) {
        // Setting the values to the textviews
        edtName.setText(output.getName());
        String surname = output.getSurname();
        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        String identity = output.getIdentity();
        String gender = output.getGender();
        String phone = output.getPhone();
        String address = output.getAddress();
        String employment = output.getEmployment();
        String company = output.getCompany();
        Long salary = output.getSalary();
        String bankAccount = output.getBankAccount();

        // Section to check if values are available to display on screen
        if (CustomUtil.hasMeaning(surname)) {
            edtSurname.setText(surname);
        }

        if (CustomUtil.hasMeaning(identity)) {
            edtIdentity.setText(identity);
        }

        if (CustomUtil.hasMeaning(gender)) {
            if (gender.equalsIgnoreCase(rdMale.getText().toString())) {
                rdMale.setChecked(true);
            } else if (gender.equalsIgnoreCase(rdFemale.getText().toString())) {
                rdFemale.setChecked(true);
            } else {
                rdgGender.check(rdMale.getId());
            }
        }

        if (CustomUtil.hasMeaning(phone)) {
            edtPhone.setText(phone);
        }

        if (CustomUtil.hasMeaning(address)) {
            edtAddress.setText(address);
        }

        if (CustomUtil.hasMeaning(employment)) {
            edtEmployment.setText(employment);
        }

        if (CustomUtil.hasMeaning(company)) {
            edtCompany.setText(company);
        }

        if (salary != null) {
            String strSalary = Long.toString(salary);
            edtSalary.setText(strSalary);
        }

        if (CustomUtil.hasMeaning(bankAccount)) {
            edtBankAccount.setText(bankAccount);
        }

    }


    /**
     * Validate in put value and execute update user profile function
     */
    private void updateUserProfile() {
        String name = edtName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String identity = edtIdentity.getText().toString().trim();
        // Get checked value of gender
        RadioButton rd = findViewById(rdgGender.getCheckedRadioButtonId());
        String gender = rd.getText().toString();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Check name
        if (TextUtils.isEmpty(name)) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(name)) {
            edtName.setError(getString(R.string.error_invalid_name));
            edtName.requestFocus();
            return;
        } else {
            name = CustomUtil.capitalFirstLetter(name);
        }

        // Check surname
        if (TextUtils.isEmpty(surname)) {
            edtSurname.setError(getString(R.string.error_empty_surname));
            edtSurname.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(surname)) {
            edtSurname.setError(getString(R.string.error_invalid_surname));
            edtSurname.requestFocus();
            return;
        } else {
            surname = CustomUtil.capitalFirstLetter(surname);
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

        // Execute update user profile
        User updateUser = new User(name, surname, identity, gender, phone, address);
        new UpdateUserProfile(updateUser).execute();

        if (user.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            updateCustomerProfile();
        } else if (user.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            // Todo: update agent profile
        }

    }

    /**
     * Request update user profile to db
     */
    @SuppressLint("StaticFieldLeak")
    class UpdateUserProfile extends AsyncTask<Void, Void, String> {

        User updateUser;

        private UpdateUserProfile(User updateUser) {
            this.updateUser = updateUser;
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
            params.put("id", Integer.toString(user.getId()));
            params.put("name", updateUser.getName());
            params.put("username", user.getUsername());
            params.put("surname", updateUser.getSurname());
            params.put("identity", updateUser.getIdentity());
            params.put("gender", updateUser.getGender());
            params.put("phone", updateUser.getPhone());
            params.put("address", updateUser.getAddress());
            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_UPDATE_USER_PROFILE, params);
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
                if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_update_user_successfully))) {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_successfully, Toast.LENGTH_LONG).show();
                    // ToDo
                    returnIntent(Activity.RESULT_OK, getString(R.string.msg_update_successfully));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_not_successfully, Toast.LENGTH_LONG).show();
                    // Todo
                    returnIntent(Activity.RESULT_CANCELED, getString(R.string.msg_update_not_successfully));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Validate in put value and execute update customer profile function
     */
    private void updateCustomerProfile() {

        String employment = edtEmployment.getText().toString().trim();
        String company = edtCompany.getText().toString().trim();
        String strSalary = edtSalary.getText().toString().trim();
        Long salary = Long.parseLong(strSalary);
        String bankAccount = edtBankAccount.getText().toString().trim();

        // Check employment
        if (TextUtils.isEmpty(employment)) {
            edtEmployment.setError(getString(R.string.error_empty_workplace));
            edtEmployment.requestFocus();
            return;
        }

        // Check company
        if (TextUtils.isEmpty(company)) {
            edtCompany.setError(getString(R.string.error_empty_designation));
            edtCompany.requestFocus();
            return;
        } else {
            company = CustomUtil.capitalFirstLetter(company);
        }

        // Check salary
        if (TextUtils.isEmpty(strSalary)) {
            edtSalary.setError(getString(R.string.error_empty_designation));
            edtSalary.requestFocus();
            return;
        }

        // Check bank account
        if (TextUtils.isEmpty(bankAccount)) {
            edtBankAccount.setError(getString(R.string.error_empty_designation));
            edtBankAccount.requestFocus();
            return;
        }

        CustomerProfile customerProfile = new CustomerProfile(employment, company, salary, bankAccount);
        new UpdateCustomerProfile(customerProfile).execute();

    }

    /**
     * Request update customer profile to db
     */
    @SuppressLint("StaticFieldLeak")
    class UpdateCustomerProfile extends AsyncTask<Void, Void, String> {

        CustomerProfile updateCustomer;

        private UpdateCustomerProfile(CustomerProfile customerProfile) {
            this.updateCustomer = customerProfile;
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
            params.put("id", Integer.toString(user.getId()));
            params.put("username", user.getUsername());
            params.put("employment", updateCustomer.getEmployment());
            params.put("company", updateCustomer.getCompany());
            params.put("salary", Long.toString(updateCustomer.getSalary()));
            params.put("bank_account", updateCustomer.getBankAccount());
            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_UPDATE_CUSTOMER_PROFILE, params);
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
                if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_update_customer_successfully))) {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_successfully, Toast.LENGTH_LONG).show();
                    returnIntent(Activity.RESULT_OK, getString(R.string.msg_update_successfully));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msg_update_not_successfully, Toast.LENGTH_LONG).show();
                    returnIntent(Activity.RESULT_CANCELED, getString(R.string.msg_update_not_successfully));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
