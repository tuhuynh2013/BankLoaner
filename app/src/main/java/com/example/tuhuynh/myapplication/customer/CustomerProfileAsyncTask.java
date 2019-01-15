package com.example.tuhuynh.myapplication.customer;

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
    private CustomerProfile customer;

    public CustomerProfileAsyncTask(CustomerProfileCallBack cb, Context context, CustomerProfile customer) {
        this.cb = cb;
        this.context = context;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.customer = customer;
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
        params.put("id", Integer.toString(customer.getId()));
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GET_CUSTOMER_PROFILE, params);
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

                String name = customerJson.getString("name");
                String surname = customerJson.getString("surname");
                String identityID = customerJson.getString("identity_id");
                String gender = customerJson.getString("gender");
                String phone = customerJson.getString("phone");
                String address = customerJson.getString("address");
                String role = customerJson.getString("role");
                String employment = customerJson.getString("employment");
                String company = customerJson.getString("company");
                Long salary = customerJson.getLong("salary");
                String bankAccount = customerJson.getString("bank_account");

                CustomerProfile customerProfile = new CustomerProfile(name, surname, identityID, gender, phone, address, role, employment, company, salary, bankAccount);
                cb.callBack(customerProfile);

            } else {
                Toast.makeText(context.getApplicationContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
