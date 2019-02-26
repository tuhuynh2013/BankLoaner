package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AgentRegisterAsync extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private AgentProfile agentProfile;

    public AgentRegisterAsync(Context context, AgentProfile agentProfile) {
        this.context = context;
        this.agentProfile = agentProfile;
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
        params.put("id", agentProfile.getId());
        params.put("name", agentProfile.getName());
        params.put("email", agentProfile.getEmail());
        params.put("phone", agentProfile.getPhone());
        params.put("role", agentProfile.getRole());
        params.put("bank_id", Integer.toString(agentProfile.getWorkBank().getId()));

        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_AGENT_REGISTER, params);
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
                // Storing the user in shared preferences
                SharedPrefManager.getInstance(context).userLogin(agentProfile);
            } else {
                CustomUtil.displayToast(context, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}