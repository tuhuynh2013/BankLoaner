package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.user.UserRole;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GetUserProfileAsync extends AsyncTask<Void, Void, String> {

    private final String TAG = GetUserProfileAsync.class.getSimpleName();
    private GetUserProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private UserProfile userProfile;

    public GetUserProfileAsync(GetUserProfileCallBack cb, UserProfile userProfile) {
        this.cb = cb;
        this.userProfile = userProfile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Creating request handler object
        RequestHandler requestHandler = new RequestHandler();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", userProfile.getId());
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GET_USER_PROFILE, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");

            // If no error in response
            if (!obj.getBoolean("error")) {
                JSONObject userJson = obj.getJSONObject("userProfile");
                userProfile.setEmail(userJson.getString("email"));
                userProfile.setName(userJson.getString("name"));
                userProfile.setSurname(userJson.getString("surname"));
                userProfile.setIdentity(userJson.getString("identity"));
                userProfile.setGender(userJson.getString("gender"));
                userProfile.setPhone(userJson.getString("phone"));
                userProfile.setAddress(userJson.getString("address"));
                userProfile.setRole(userJson.getString("role"));

                if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                    // Getting the customer info from the response
                    JSONObject customerJson = obj.getJSONObject("customerProfile");
                    String employment = customerJson.getString("employment");
                    String company = customerJson.getString("company");
                    Long salary = customerJson.getLong("salary");
                    String bankAccount = customerJson.getString("bank_account");

                    CustomerProfile customerProfile = new CustomerProfile(userProfile, employment, company, salary, bankAccount);
                    cb.responseFromGetUserProfile(customerProfile);
                } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                    // Getting the agent info from the response
                    JSONObject agentJson = obj.getJSONObject("agentProfile");
                    // Get bank info
                    BankInfo bank = new BankInfo();
                    bank.setId(Integer.parseInt(agentJson.getString("bank_id")));
                    bank.setName(agentJson.getString("bank_name"));
                    bank.setShortName(agentJson.getString("bank_short_name"));

                    AgentProfile agentProfile = new AgentProfile(userProfile, bank);
                    cb.responseFromGetUserProfile(agentProfile);
                } else if (userProfile.getRole().equalsIgnoreCase(UserRole.ADMIN)) {
                    cb.responseFromGetUserProfile(userProfile);
                }
                Log.v(TAG, msg);
            } else {
                Log.e(TAG, msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
