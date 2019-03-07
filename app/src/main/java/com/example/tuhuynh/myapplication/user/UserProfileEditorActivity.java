package com.example.tuhuynh.myapplication.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.UpdateCustomerProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateCustomerProfileCallBack;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.UpdateUserProfileCallBack;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class UserProfileEditorActivity extends AppCompatActivity implements GetUserProfileCallBack,
        UpdateUserProfileCallBack, UpdateCustomerProfileCallBack {

    private ImageView imgProfile;
    private TextView tvEmail, tvBankName;
    private EditText edtName, edtSurname, edtIdentity, edtPhone, edtAddress, edtEmployment,
            edtCompany, edtSalary, edtBankAccount;
    private RadioGroup rdgGender;
    private RadioButton rdMale, rdFemale;
    private Button btnSave;

    private String caller;
    private UserProfile userProfile;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseUser firebaseUser;

    private String profileImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);
        setTitle(getString(R.string.title_update_profile));

        isLoggedInAndInitialFireBase();

        // Turn off Up navigation, when called from LoanApplication
        final Intent intent = this.getIntent();
        caller = intent.getStringExtra("caller");
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

        // Retrieve userProfile profile from db
        new GetUserProfileAsync(this, this, userProfile).execute();

        // Initial for profile editor screen
        initialViews();

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

    }

    /**
     * Check user is logged in and initial firebase component
     **/
    private void isLoggedInAndInitialFireBase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            userProfile = SharedPrefManager.getInstance(this).getUser();
            firebaseUser = mAuth.getCurrentUser();
        }
    }

    /**
     * Initial elements and set its own value
     */
    private void initialViews() {

        // Initial element
        imgProfile = findViewById(R.id.img_profile);
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
        tvBankName = findViewById(R.id.tv_bank_name);
        btnSave = findViewById(R.id.btn_save);

        if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            tvBank.setVisibility(View.GONE);
            tvBankName.setVisibility(View.GONE);
        } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            tvEmployment.setVisibility(View.GONE);
            edtEmployment.setVisibility(View.GONE);
            tvCompany.setVisibility(View.GONE);
            edtCompany.setVisibility(View.GONE);
            tvSalary.setVisibility(View.GONE);
            edtSalary.setVisibility(View.GONE);
            tvBankAccount.setVisibility(View.GONE);
            edtBankAccount.setVisibility(View.GONE);
        }

    }

    /**
     *
     **/
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     *
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        if (CustomUtil.hasMeaning(userProfile.getProfileImg())) {
            Glide.with(getApplicationContext()).load(userProfile.getProfileImg()).into(imgProfile);
        }


        // Customer section
        if (this.userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
            CustomerProfile customer = (CustomerProfile) object;
            String employment = customer.getEmployment();
            String company = customer.getCompany();
            Long salary = customer.getSalary();
            String bankAccount = customer.getBankAccount();

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
        // Agent section
        else if (this.userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
            AgentProfile agent = (AgentProfile) object;
            String bankName = agent.getWorkBank().getName();
            // Set value for spinner
            if (CustomUtil.hasMeaning(bankName)) {
                tvBankName.setText(bankName);
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
            edtName.setError(getString(R.string.msg_empty_name));
            edtName.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(name)) {
            edtName.setError(getString(R.string.msg_invalid_name));
            edtName.requestFocus();
            return;
        } else {
            name = CustomUtil.capitalFirstLetter(name);
        }

        // Check surname
        if (TextUtils.isEmpty(surname)) {
            edtSurname.setError(getString(R.string.msg_empty_surname));
            edtSurname.requestFocus();
            return;
        } else if (CustomUtil.isIncorrectName(surname)) {
            edtSurname.setError(getString(R.string.msg_invalid_surname));
            edtSurname.requestFocus();
            return;
        } else {
            surname = CustomUtil.capitalFirstLetter(surname);
        }

        // Check identity
        if (TextUtils.isEmpty(identity)) {
            edtIdentity.setError(getString(R.string.msg_empty_identity));
            edtIdentity.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectIdentity(identity)) {
            edtIdentity.setError(getString(R.string.msg_invalid_identity));
            edtIdentity.requestFocus();
            return;
        }

        // Check phone number
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError(getString(R.string.msg_empty_phone));
            edtPhone.requestFocus();
            return;
        } else if (!CustomUtil.isCorrectPhone(phone)) {
            edtPhone.setError(getString(R.string.msg_invalid_phone));
            edtPhone.requestFocus();
            return;
        }

        // Check address
        if (TextUtils.isEmpty(address)) {
            edtAddress.setError(getString(R.string.msg_empty_address));
            edtAddress.requestFocus();
            return;
        }

        // Execute update userProfile profile
        UserProfile updateUserProfile = new UserProfile(userProfile.getId(), name, surname,
                userProfile.getEmail(), identity, gender, phone, address, userProfile.getRole(),
                userProfile.getAccountType());
        new UpdateUserProfileAsync(this, this, updateUserProfile).execute();

    }

    @Override
    public void responseFromUpdateUserProfile(String msg) {
        // If update UserProfile success, execute update upload profile picture
        if (msg.equalsIgnoreCase(getString(R.string.db_update_user_successfully))) {
            if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                uploadImage();
            } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                // If update Agent success, return intent result
                if (TextUtils.isEmpty(caller)) {
                    finish();
                    startActivity(new Intent(this, UserProfileActivity.class));
                } else {
                    returnIntent(Activity.RESULT_OK, msg);
                }
                CustomUtil.displayToast(getApplicationContext(), msg);
            }
        } else {
            edtIdentity.getText().clear();
            edtIdentity.setError(msg);
            edtIdentity.requestFocus();
        }
        CustomUtil.displayToast(getApplicationContext(), msg);
    }

    /**
     *
     **/
    private void uploadImage() {

        if (filePath != null) {
            //Firebase
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("user/" + userProfile.getId() + "/images/profile picture");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImgUrl = uri.toString();
                                    saveUserInfoToFireBase();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void saveUserInfoToFireBase() {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().
                setPhotoUri(Uri.parse(profileImgUrl)).build();

        firebaseUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateCustomerProfile();
                }
            }
        });

        //Change user profile image
        userProfile.setProfileImg(profileImgUrl);
        SharedPrefManager.getInstance(this).userLogin(userProfile);

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
            edtEmployment.setError(getString(R.string.msg_empty_workplace));
            edtEmployment.requestFocus();
            return;
        }

        // Check company
        if (TextUtils.isEmpty(company)) {
            edtCompany.setError(getString(R.string.msg_empty_designation));
            edtCompany.requestFocus();
            return;
        } else {
            company = CustomUtil.capitalFirstLetter(company);
        }

        // Check salary
        if (TextUtils.isEmpty(strSalary)) {
            edtSalary.setError(getString(R.string.msg_empty_designation));
            edtSalary.requestFocus();
            return;
        }

        // Check bank account
        if (TextUtils.isEmpty(bankAccount)) {
            edtBankAccount.setError(getString(R.string.msg_empty_designation));
            edtBankAccount.requestFocus();
            return;
        }

        CustomerProfile customerProfile = new CustomerProfile(userProfile, employment, company, salary, bankAccount);
        new UpdateCustomerProfileAsync(this, this, customerProfile).execute();

    }

    @Override
    public void responseFromUpdateCustomerProfile(String msg) {
        // If update CustomerProfile success, return intent result
        if (msg.equalsIgnoreCase(getString(R.string.db_update_customer_successfully))) {
            if (TextUtils.isEmpty(caller)) {
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                CustomUtil.displayToast(getApplicationContext(), getString(R.string.ui_customer_profile_updated));
            } else {
                returnIntent(Activity.RESULT_OK, msg);
            }
        } else {
            returnIntent(Activity.RESULT_CANCELED, msg);
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
