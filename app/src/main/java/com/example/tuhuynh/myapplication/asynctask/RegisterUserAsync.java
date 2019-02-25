package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customer.CustomerProfile;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.user.UserRole;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RegisterUserAsync extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private UserProfile userProfile;

    public RegisterUserAsync(Context context, UserProfile userProfile) {
        this.context = context;
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

        // Creating request parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("id", userProfile.getId());
        params.put("name", userProfile.getName());
        params.put("email", userProfile.getEmail());
        params.put("role", userProfile.getRole());

        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String message = obj.getString("message");

            // If no error in response
            if (!obj.getBoolean("error")) {
                // Create object base on user role
                if (userProfile.getRole().equalsIgnoreCase(UserRole.CUSTOMER)) {
                    CustomerProfile customer = new CustomerProfile();
                    customer.setId(userProfile.getId());
                    customer.setName(userProfile.getName());
                    customer.setEmail(userProfile.getEmail());
                    customer.setRole(userProfile.getRole());
                    customer.setAccountType(userProfile.getAccountType());
                    // Storing the user in shared preferences
                    SharedPrefManager.getInstance(context).userLogin(customer);
                } else if (userProfile.getRole().equalsIgnoreCase(UserRole.AGENT)) {
                    AgentProfile agent = new AgentProfile();
                    agent.setId(userProfile.getId());
                    agent.setName(userProfile.getName());
                    agent.setEmail(userProfile.getEmail());
                    agent.setRole(userProfile.getRole());
                    agent.setAccountType(userProfile.getAccountType());
                    // Storing the user in shared preferences
                    SharedPrefManager.getInstance(context).userLogin(agent);
                }
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
