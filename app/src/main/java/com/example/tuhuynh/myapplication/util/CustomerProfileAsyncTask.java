package com.example.tuhuynh.myapplication.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CustomerProfileAsyncTask extends AsyncTask<Void, Void, String> {

    private CustomerProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private User user;

    public CustomerProfileAsyncTask(CustomerProfileCallBack cb, Context context, User user) {
        this.cb = cb;
        this.context = context;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.user = user;
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
        params.put("id", Integer.toString(user.getId()));
        params.put("username", user.getUsername());
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_USERPROFILE, params);
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
            if (!obj.getBoolean("error") && message.equalsIgnoreCase(context.getString(R.string.msg_retrieve_customer_info_success))) {

                // Getting the customer info from the response
                JSONObject customerJson = obj.getJSONObject("customerProfile");

                String surname = customerJson.getString("surname");
                String identityID = customerJson.getString("identity_id");
                String gender = customerJson.getString("gender");
                String phone = customerJson.getString("phone");
                String address = customerJson.getString("address");
                String workplace = customerJson.getString("workplace");
                String designation = customerJson.getString("designation");

                CustomerProfile customerProfile = new CustomerProfile(surname, identityID, gender, phone, address, workplace, designation);
                cb.callBack(customerProfile);

            } else {
                Toast.makeText(context.getApplicationContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
