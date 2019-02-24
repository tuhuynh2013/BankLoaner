package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Request update customer profile to db
 */
public class UpdateCustomerProfileAsync extends AsyncTask<Void, Void, String> {

    private UpdateCustomerProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private CustomerProfile customerProfile;

    public UpdateCustomerProfileAsync(UpdateCustomerProfileCallBack cb, Context context, CustomerProfile customerProfile) {
        this.cb = cb;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.customerProfile = customerProfile;
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
        params.put("id", customerProfile.getId());
        params.put("employment", customerProfile.getEmployment());
        params.put("company", customerProfile.getCompany());
        params.put("salary", Long.toString(customerProfile.getSalary()));
        params.put("bank_account", customerProfile.getBankAccount());
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
            String msg = obj.getString("message");
            cb.responseFromUpdateCustomerProfile(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}