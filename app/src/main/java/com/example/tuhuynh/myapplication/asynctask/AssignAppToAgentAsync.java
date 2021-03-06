package com.example.tuhuynh.myapplication.asynctask;

import android.os.AsyncTask;

import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class AssignAppToAgentAsync extends AsyncTask<Void, Void, String> {

    private int applicationID;
    private String agentID;
    private String status;

    public AssignAppToAgentAsync(int applicationID, String agentID, String status) {
        this.applicationID = applicationID;
        this.agentID = agentID;
        this.status = status;
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
        params.put("application_id", Integer.toString(applicationID));
        params.put("agent_id", agentID);
        params.put("status", status);

        return requestHandler.sendPostRequest(URLs.URL_ASSIGN_AGENT, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String message = obj.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
