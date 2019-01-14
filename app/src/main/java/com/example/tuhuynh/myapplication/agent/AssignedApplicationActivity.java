package com.example.tuhuynh.myapplication.agent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AssignedApplicationActivity extends AppCompatActivity {

    User user;
    List<ApplicationInfo> applications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_application);
        setTitle(getString(R.string.title_assigned_application));

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            user = SharedPrefManager.getInstance(this).getUser();
        }

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new AssignedAppAsyncTask().execute();

    }

    /**
     * Uses to get list of banks from database
     */
    @SuppressLint("StaticFieldLeak")
    class AssignedAppAsyncTask extends AsyncTask<Void, Void, String> {

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
            params.put("agent_id", Integer.toString(user.getId()));

            return requestHandler.sendPostRequest(URLs.URL_GET_ASSIGNED_APPLICATIONS, params);
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
                if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_retrieve_applications_success))) {

                    // Getting the user from the response
                    JSONArray jsonArray = obj.getJSONArray("applications");
                    applications = extractApplications(jsonArray);
                    // Sort by date
                    Collections.sort(applications, new Comparator<ApplicationInfo>() {
                        @Override
                        public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });
                    Collections.reverse(applications);

                    ListView listView = findViewById(R.id.lv_assigned_applications);
                    AssignedApplicationAdapter assignedAdapter = new AssignedApplicationAdapter(getApplicationContext(), R.layout.assigned_application_adapter, applications);
                    listView.setAdapter(assignedAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            ApplicationInfo application = (ApplicationInfo) parent.getItemAtPosition(position);
//                            Intent intent = new Intent(parent.getContext(), BankInformationActivity.class);
//                            intent.putExtra("bank", bank);
//                            parent.getContext().startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private List<ApplicationInfo> extractApplications(JSONArray jsonArray) throws JSONException {
        List<ApplicationInfo> assignedList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject applicationJson = jsonArray.getJSONObject(i);
            // Get customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setId(applicationJson.getInt("customer_id"));
            customer.setName(applicationJson.getString("customer_name"));
            customer.setSurname(applicationJson.getString("customer_surname"));
            // Get bank information
            BankInfo bankInfo = new BankInfo();
            bankInfo.setId(applicationJson.getInt("bank_id"));
            bankInfo.setName(applicationJson.getString("bank_name"));
            // Convert date
            Date date = CustomUtil.convertStringToDate(applicationJson.getString("date"), "default");

            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setId(applicationJson.getInt("application_id"));
            applicationInfo.setMonth(applicationJson.getInt("month"));
            applicationInfo.setAmount(applicationJson.getLong("amount"));
            applicationInfo.setInterest(applicationJson.getDouble("interest"));
            applicationInfo.setCustomer(customer);
            applicationInfo.setBankInfo(bankInfo);
            applicationInfo.setDate(date);
            applicationInfo.setStatus(applicationJson.getString("status"));
            assignedList.add(applicationInfo);
        }
        return assignedList;
    }


}
