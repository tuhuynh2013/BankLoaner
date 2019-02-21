package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.UserRole;
import com.example.tuhuynh.myapplication.util.CustomUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetSharingAppsAsync extends AsyncTask<Void, Void, String> {

    private GetAgentAppsCallBack response;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private int agentID;


    public GetSharingAppsAsync(GetAgentAppsCallBack response, Context context, int agentID) {
        this.response = response;
        this.context = context;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.agentID = agentID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Creating request handler object
        RequestHandler requestHandler = new RequestHandler();
        HashMap<String, String> params = new HashMap<>();
        params.put("agent_id", Integer.toString(agentID));

        return requestHandler.sendPostRequest(URLs.URL_GET_SHARING_APPLICATIONS, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
        List<ApplicationInfo> applications = new ArrayList<>();

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String message = obj.getString("message");

            // If no error in response
            if (!obj.getBoolean("error") && message.equalsIgnoreCase(context.getString(R.string.msg_retrieve_sharing_apps_success))) {

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
            }
            // Return the result
            response.responseFromAsync(applications, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private List<ApplicationInfo> extractApplications(JSONArray jsonArray) throws JSONException {
        List<ApplicationInfo> assignedList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject applicationJson = jsonArray.getJSONObject(i);
            // Get customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setId(applicationJson.getInt("customer_id"));
            customer.setPhone(applicationJson.getString("customer_phone"));
            customer.setSalary(applicationJson.getLong("customer_salary"));

            ApplicationInfo applicationInfo = new ApplicationInfo();
            // Convert date
            Date date = CustomUtil.convertStringToDate(applicationJson.getString("sharing_date"), "default");
            applicationInfo.setId(applicationJson.getInt("application_id"));
            applicationInfo.setMonth(applicationJson.getInt("month"));
            applicationInfo.setAmount(applicationJson.getLong("amount"));
            applicationInfo.setInterest(applicationJson.getDouble("interest"));
            applicationInfo.setDate(date);
            applicationInfo.setCustomer(customer);
            assignedList.add(applicationInfo);
        }
        return assignedList;
    }

}
