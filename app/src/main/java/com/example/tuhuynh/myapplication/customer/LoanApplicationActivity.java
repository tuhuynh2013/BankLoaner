package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileAsync;
import com.example.tuhuynh.myapplication.asynctask.GetUserProfileCallBack;
import com.example.tuhuynh.myapplication.user.UserProfileEditorActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class LoanApplicationActivity extends AppCompatActivity implements GetUserProfileCallBack {

    private static final int REQUEST_CODE = 0x9345;
    TextView tvBankName, tvMonth, tvInterest, tvAmount;
    Button btnApply;
    CheckBox ckbAcceptTerm;
    final Context context = this;

    // Getting the current userProfile
    UserProfile userProfile = SharedPrefManager.getInstance(this).getUser();
    BankInfo bankInfo;
    String month, amount, interest;
    CustomerProfile customerProfile;
    GetUserProfileAsync asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);
        setTitle(getString(R.string.title_app_info));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial screen elements
        initialElement();

        final Intent intent = this.getIntent();
        bankInfo = (BankInfo) intent.getSerializableExtra("bank");
        month = intent.getStringExtra("month");
        amount = intent.getStringExtra("amount");
        interest = intent.getStringExtra("interest");

        // Set value for text view
        tvBankName.setText(bankInfo.getName());
        tvMonth.setText(month);
        tvAmount.setText(amount);
        tvInterest.setText(interest);

        asyncTask = new GetUserProfileAsync(this, this, userProfile);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ckbAcceptTerm.isChecked()) {
                    // customerProfile == null, it means asyncTask not yet running
                    if (customerProfile == null) {
                        asyncTask.execute();
                    } else {
                        submitApplication();
                    }
                } else {
                    ckbAcceptTerm.setError("");
                }
            }
        });

    }

    @Override
    public void responseFromGetUserProfile(Object object) {
        customerProfile = new CustomerProfile();
        customerProfile = (CustomerProfile) object;
        submitApplication();
    }

    /**
     * Initial screen elements
     */
    private void initialElement() {
        tvBankName = findViewById(R.id.tv_bank_name);
        tvMonth = findViewById(R.id.tv_month);
        tvAmount = findViewById(R.id.tv_amount);
        tvInterest = findViewById(R.id.tv_interest);
        btnApply = findViewById(R.id.btn_apply);
        ckbAcceptTerm = findViewById(R.id.ckb_accept_temp);
        ckbAcceptTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.term_dialog);

                    // set the custom dialog components - text, image and button
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    tvTitle.setText(getString(R.string.dialog_title_term));
                    HtmlTextView htmlText = dialog.findViewById(R.id.html_text);
                    htmlText.setHtml("<h1><span style=\"color: green\"><span style=\"font-size: 11pt\"><span style=\"font-family: Tahoma\"><b><span style=\"line-height: 150%\"><a name=\"CVCN\"></a>CHO VAY CÁ NHÂN </span></b></span></span></span></h1>" +
                            "<div style=\"margin-left: 80px\"><span style=\"font-size: 12pt\"><span style=\"color: red\"><b><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'\">Cùng bạn xây dựng các giải pháp tài chính</span></b></span></span></div>" +
                            "<div><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; font-size: 10pt; mso-fareast-font-family: 'Times New Roman'; mso-bidi-font-weight: bold\"><span style=\"font-size: 10pt\">Bạn cần vốn để phục vụ nhu cầu sinh hoạt, " +
                            "tiêu dùng? Bạn có nhu cầu sản xuất kinh doanh nhưng đang thiếu nguồn tài chính? Sản phẩm cho vay cá nhân của Vietcombank sẽ tạo dựng giải pháp cho bạn.</span></span></div>" +
                            "<h1><span style=\\\"color: green\\\"><span style=\\\"font-size: 11pt\\\"><span style=\\\"font-family: Tahoma\\\"><b><span style=\\\"line-height: 150%\\\"><a name=\\\"CVCN\\\"></a>CHO VAY CÁ NHÂN </span></b></span></span></span></h1>\" +\n" +
                            "                            \"<div style=\\\"margin-left: 80px\\\"><span style=\\\"font-size: 12pt\\\"><span style=\\\"color: red\\\"><b><span style=\\\"line-height: 150%; font-family: 'Tahoma','sans-serif'\\\">Cùng bạn xây dựng các giải pháp tài chính</span></b></span></span></div>\" +\n" +
                            "                            \"<div><span style=\\\"line-height: 150%; font-family: 'Tahoma','sans-serif'; font-size: 10pt; mso-fareast-font-family: 'Times New Roman'; mso-bidi-font-weight: bold\\\"><span style=\\\"font-size: 10pt\\\">Bạn cần vốn để phục vụ nhu cầu sinh hoạt, \" +\n" +
                            "                            \"tiêu dùng? Bạn có nhu cầu sản xuất kinh doanh nhưng đang thiếu nguồn tài chính? Sản phẩm cho vay cá nhân của Vietcombank sẽ tạo dựng giải pháp cho bạn.</span></span></div>" +
                            "<ul>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Phù hợp do các sản phẩm cho vay được thiết kế dựa trên nhu cầu của khách hàng.</span></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Thủ tục cho vay nhanh chóng, thuận tiện</span></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Lãi suất cạnh tranh.</span></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Nhiều ưu đãi khi sử dụng các dịch vụ khác đi kèm.</span></li>\n" +
                            "</ul>" +
                            "<h5><span style=\"font-size: 10pt\"><b><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'\">2.<span style=\"font: 7pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp; </span></span></b><b><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'\">Điều kiện sử dụng</span></b></span></h5>" +
                            "<h5><b style=\"mso-bidi-font-weight: normal\"><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; font-size: 10pt; mso-fareast-font-family: Tahoma\"><span style=\"mso-list: Ignore\">3.<span style=\"font: 7pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp; </span></span></span></b><b style=\"mso-bidi-font-weight: normal\"><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; font-size: 10pt; mso-fareast-font-family: 'Times New Roman'\">Hồ sơ đăng ký</span></b></h5>" +
                            "<ul>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">CMND, Sổ hộ khẩu/Giấy chứng nhận tạm trú dài hạn </span></li>\n" +
                            "    <li><a href=\"http://www.vietcombank.com.vn/Personal/Documents/Giay%20de%20nghi%20vay%20von%20ca%20nhan.pdf?22\"><b style=\"mso-bidi-font-weight: normal\"><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: #00b050; font-size: 10pt\">Giấy đề nghị vay vốn&nbsp; cá nhân (theo mẫu)</span></b></a></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Giấy tờ chứng minh mục đích sử dụng vốn</span></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Giấy tờ chứng minh thu nhập</span></li>\n" +
                            "    <li><span style=\"line-height: 150%; font-family: 'Tahoma','sans-serif'; color: black; font-size: 10pt\">Giấy tờ liên quan đến tài sản bảo đảm</span></li>\n" +
                            "</ul>");
                    Button dialogButton = dialog.findViewById(R.id.btn_close);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    /**
     *
     */
    private void submitApplication() {
        // If profile not enough information, move to UserProfileEditorActivity
        if (customerProfile.isMissingInfo()) {
            Intent intent = new Intent(this, UserProfileEditorActivity.class);
            intent.putExtra("caller", "LoanApplication");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            new SubmitApplication().execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                new SubmitApplication().execute();
            }
            Toast.makeText(this, data.getStringExtra("msg"), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SubmitApplication extends AsyncTask<Void, Void, String> {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Displaying the progress bar while userProfile registers on the server
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            // Get current date & time
            Date currentTime = Calendar.getInstance().getTime();
            String strDate = CustomUtil.convertDateToString(currentTime, "default");

            // Reformat amount
            Long longAmount = CustomUtil.convertFormattedStringToLong(amount);
            if (longAmount != null)
                amount = Long.toString(longAmount);

            // Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("month", month);
            params.put("amount", amount);
            params.put("interest", interest);
            params.put("customer_id", userProfile.getId());
            params.put("bank_id", Integer.toString(bankInfo.getId()));
            params.put("date", strDate);
            params.put("status", ApplicationStatus.PROGRESSING);

            // Return the response
            return requestHandler.sendPostRequest(URLs.URL_SUBMIT_APPLICATION, params);
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
                if (!obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.msg_submit_success))) {
                    Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                    intent.putExtra("caller", "SubmitApplication");
                    startActivity(intent);
                    finish();
                } else if (obj.getBoolean("error") && msg.equalsIgnoreCase(getString(R.string.respond_existed_application))) {
                    // Get object from db
                    JSONObject applicationJson = obj.getJSONObject("application_info");

                    Intent intent = new Intent(getApplicationContext(), CustomerAppInfoActivity.class);
                    // Convert String date to Date
                    Date date = CustomUtil.convertStringToDate(applicationJson.getString("date"), "default");
                    // Get bank information
                    BankInfo bankInfo = new BankInfo();
                    bankInfo.setName(applicationJson.getString("bank_name"));

                    ApplicationInfo applicationInfo = new ApplicationInfo();
                    applicationInfo.setId(Integer.parseInt(applicationJson.getString("application_id")));
                    applicationInfo.setMonth(Integer.parseInt(applicationJson.getString("month")));
                    applicationInfo.setAmount(Long.parseLong(applicationJson.getString("amount")));
                    applicationInfo.setInterest(Double.parseDouble(applicationJson.getString("interest")));
                    applicationInfo.setDate(date);
                    applicationInfo.setStatus(applicationJson.getString("status"));
                    applicationInfo.setBankInfo(bankInfo);

                    intent.putExtra("caller", "LoanApplicationActivity");
                    intent.putExtra("application", applicationInfo);

                    startActivity(intent);
                    finish();
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
