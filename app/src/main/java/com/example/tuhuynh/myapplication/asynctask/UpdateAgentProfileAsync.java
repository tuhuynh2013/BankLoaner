package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Request update agent profile to db
 */
public class UpdateAgentProfileAsync extends AsyncTask<Void, Void, String> {

    private UpdateAgentProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private AgentProfile agentProfile;

    public UpdateAgentProfileAsync(UpdateAgentProfileCallBack cb, Context context, AgentProfile agentProfile) {
        this.cb = cb;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.agentProfile = agentProfile;
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
        params.put("user_id", agentProfile.getId());
        params.put("bank_id", Integer.toString(agentProfile.getWorkBank().getId()));
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_UPDATE_AGENT_PROFILE, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
            cb.responseFromUpdateAgentProfile(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}