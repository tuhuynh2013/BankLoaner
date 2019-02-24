package com.example.tuhuynh.myapplication.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.UpdateAgentProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateAgentProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.UpdateCustomerProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateCustomerProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserProfileCallBack;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.asynctask.GetBanksAsync;
import com.example.tuhuynh.myapplication.asynctask.GetBanksCallBack;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;


public class UserProfileEditorActivity extends AppCompatActivity implements GetUserProfileCallBack, GetBanksCallBack,
        UpdateUserProfileCallBack, UpdateCustomerProfileCallBack, UpdateAgentProfileCallBack {

    private TextView tvEmail;
    private EditText edtName, edtSurname, edtIdentity, edtPhone, edtAddress, edtEmployment,
            edtCompany, edtSalary, edtBankAccount;
    private RadioGroup rdgGender;
    private RadioButton rdMale, rdFemale;
    private Spinner spinBankName;
    private Button btnSave;

    UserProfile userProfile;
    List<BankInfo> banks;
    List<String> bankNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);
        setTitle(getString(R.string.title_update_profile));

        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            // Getting the current userProfile
            userProfile = SharedPrefManager.getInstance(this).getUser();
        }

        // Turn off Up navigation, when called from LoanApplication
        final Intent intent = this.getIntent();
        String caller = intent.getStringExtra("caller");
        if (caller != null) {
            if (caller.equalsIgnoreCase("LoanApplication")) {
                // Create Up button
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
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
        edtName = findViewById(R.id.edt_name);
        edtSurname = findViewById(R.id.edt_surname);
        edtIdentity = findViewById(R.id.edt_identity);
        tvEmail = findViewById(R.id.tv_email);
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
        spinBankName = findViewById(R.id.spin_bank_name);
        btnSave = findViewById(R.id.btn_save);

        if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            tvBank.setVisibility(View.GONE);
            spinBankName.setVisibility(View.GONE);
        } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            tvEmployment.setVisibility(View.GONE);
            edtEmployment.setVisibility(View.GONE);
            tvCompany.setVisibility(View.GONE);
            edtCompany.setVisibility(View.GONE);
            tvSalary.setVisibility(View.GONE);
            edtSalary.setVisibility(View.GONE);
            tvBankAccount.setVisibility(View.GONE);
            edtBankAccount.setVisibility(View.GONE);

            // Get bank info from db
            new GetBanksAsync(this, this).execute();

        }
        // Retrieve userProfile profile from db
        new GetUserProfileAsync(this, this, userProfile).execute();
    }

    /**
     * Uses to get userProfile profile from db
     * and set content for profile editor screen
     */
    @Override
    public void responseFromGetUserProfile(Object object) {
        // Setting the values to the textviews
        UserProfile userProfile = (UserProfile) object;

        String surname = userProfile.getSurname();
        String identity = userProfile.getIdentity();
        String gender = userProfile.getGender();
        String phone = userProfile.getPhone();
        String address = userProfile.getAddress();

        // Section to check if values are available to display on screen
        edtName.setText(userProfile.getName());
        tvEmail.setText(this.userProfile.getEmail());

        if (!TextUtils.isEmpty(surname)) {
            edtSurname.setText(surname);
        }

        if (!TextUtils.isEmpty(identity)) {
            edtIdentity.setText(identity);
        }

        if (!TextUtils.isEmpty(gender)) {
            if (gender.equalsIgnoreCase(rdMale.getText().toString())) {
                rdMale.setChecked(true);
            } else if (gender.equalsIgnoreCase(rdFemale.getText().toString())) {
                rdFemale.setChecked(true);
            } else {
                rdgGender.check(rdMale.getId());
            }
        }

        if (!TextUtils.isEmpty(phone)) {
            edtPhone.setText(phone);
        }

        if (!TextUtils.isEmpty(address)) {
            edtAddress.setText(address);
        }

        // Customer section
        if (this.userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            CustomerProfile customer = (CustomerProfile) object;
            String employment = customer.getEmployment();
            String company = customer.getCompany();
            Long salary = customer.getSalary();
            String bankAccount = customer.getBankAccount();

            if (!TextUtils.isEmpty(employment)) {
                edtEmployment.setText(employment);
            }

            if (!TextUtils.isEmpty(company)) {
                edtCompany.setText(company);
            }

            if (salary != null) {
                String strSalary = Long.toString(salary);
                edtSalary.setText(strSalary);
            }

            if (!TextUtils.isEmpty(bankAccount)) {
                edtBankAccount.setText(bankAccount);
            }
        }
        // Agent section
        else if (this.userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            AgentProfile agent = (AgentProfile) object;
            String bankName = agent.getWorkBank().getName();
            // Set value for spinner
            if (!TextUtils.isEmpty(bankName)) {
                for (int i = 0; i < spinBankName.getAdapter().getCount(); i++) {
                    if (spinBankName.getAdapter().getItem(i).toString().contains(bankName)) {
                        spinBankName.setSelection(i);
                    }
                }
            }
        }

    }

    /**
     * Validate in put value and execute update userProfile profile function
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

        // Check identity
        if (TextUtils.isEmpty(identity)) {
            edtIdentity.setError(getString(R.string.error_empty_identity));
            edtIdentity.requestFocus();
            return;
        } else if (CustomUtil.isCorrectIdentity(identity)) {
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

        // Execute update userProfile profile
        UserProfile updateUserProfile = new UserProfile(userProfile.getId(), name, surname, identity, gender, phone, address);
        new UpdateUserProfileAsync(this, this, updateUserProfile).execute();

    }

    @Override
    public void responseFromUpdateUserProfile(String msg) {
        // If update UserProfile success, execute update CustomerProfile or AgentProfile
        if (msg.equalsIgnoreCase(getString(R.string.msg_update_user_successfully))) {
            if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                updateCustomerProfile();
            } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                updateAgentProfile();
            }
        } else {
            CustomUtil.displayToast(getApplicationContext(), msg);
        }
    }

    /**
     * Validate input value and execute update customer profile function
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
        new UpdateCustomerProfileAsync(this, this, customerProfile).execute();

    }

    @Override
    public void responseFromUpdateCustomerProfile(String msg) {
        // If update CustomerProfile success, return intent result
        if (msg.equalsIgnoreCase(getString(R.string.msg_update_customer_successfully))) {
            returnIntent(Activity.RESULT_OK, msg);
        } else {
            returnIntent(Activity.RESULT_CANCELED, msg);
        }
        CustomUtil.displayToast(getApplicationContext(), msg);
    }

    /**
     * Execute update customer profile function
     */
    private void updateAgentProfile() {
        // Get bank info from userProfile selected
        BankInfo bank = new BankInfo();
        for (BankInfo bankInfo : banks) {
            if (bankInfo.getName().equalsIgnoreCase(spinBankName.getSelectedItem().toString())) {
                bank = bankInfo;
                break;
            }
        }
        new UpdateAgentProfileAsync(this, this, new AgentProfile(bank)).execute();
    }

    @Override
    public void responseFromUpdateAgentProfile(String msg) {
        // If update Agent success, return intent result
        if (msg.equalsIgnoreCase(getString(R.string.msg_update_agent_successfully))) {
            returnIntent(Activity.RESULT_OK, msg);
        } else {
            returnIntent(Activity.RESULT_CANCELED, msg);
        }
        CustomUtil.displayToast(getApplicationContext(), msg);
    }

    /**
     * Get list of banks
     */
    @Override
    public void responseFromGetBanks(List<BankInfo> banks) {
        this.banks = banks;
        for (BankInfo bankInfo : banks) {
            bankNames.add(bankInfo.getName());
        }
        // Initial spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, bankNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinBankName.setAdapter(adapter);
        spinBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
