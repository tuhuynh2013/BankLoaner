package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetUserProfileAsync extends AsyncTask<Void, Void, String> {

    private GetUserProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private User user;

    public GetUserProfileAsync(GetUserProfileCallBack cb, Context context, User user) {
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
        params.put("role", user.getRole());
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GET_USER_PROFILE, params);
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
            if (!obj.getBoolean("error")) {
                if (message.equalsIgnoreCase(context.getString(R.string.msg_retrieve_customer_info_success))) {
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

                    CustomerProfile customerProfile = new CustomerProfile(user.getId(), name, surname, identityID, gender, phone, address, role, employment, company, salary, bankAccount);
                    cb.responseFromAsync(customerProfile);
                } else if (message.equalsIgnoreCase(context.getString(R.string.msg_retrieve_agent_info_success))) {
                    // Getting the agent info from the response
                    JSONObject agentJson = obj.getJSONObject("agentProfile");

                    String name = agentJson.getString("name");
                    String surname = agentJson.getString("surname");
                    String identityID = agentJson.getString("identity_id");
                    String gender = agentJson.getString("gender");
                    String phone = agentJson.getString("phone");
                    String address = agentJson.getString("address");
                    String role = agentJson.getString("role");
                    // Get bank info
                    BankInfo bank = new BankInfo();
                    bank.setId(Integer.parseInt(agentJson.getString("bank_id")));
                    bank.setName(agentJson.getString("bank_name"));
                    bank.setShortName(agentJson.getString("bank_short_name"));

                    AgentProfile agentProfile = new AgentProfile(user.getId(), name, surname, identityID, gender, phone, address, role, bank);
                    cb.responseFromAsync(agentProfile);
                }
            } else {
                Toast.makeText(context.getApplicationContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
