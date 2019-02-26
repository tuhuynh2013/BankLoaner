package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GetAgentsAsync extends AsyncTask<Void, Void, String> {

    private GetAgentsCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;

    public GetAgentsAsync(GetAgentsCallBack cb, Context context) {
        this.cb = cb;
        this.context = context;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
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
        params.put("msg", "true");

        return requestHandler.sendPostRequest(URLs.URL_GET_AGENTS, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");

            // If no error in response
            if (!obj.getBoolean("error") &&
                    msg.equalsIgnoreCase(context.getString(R.string.db_get_agents_success))) {

                // Getting the user from the response
                JSONArray jsonArray = obj.getJSONArray("agents");
                List<AgentProfile> agents = extractAgentList(jsonArray);
                // Sort by agent name
                Collections.sort(agents, new Comparator<AgentProfile>() {
                    @Override
                    public int compare(AgentProfile o1, AgentProfile o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                // Return the result
                cb.responseFromGetAgents(agents, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract list of agents from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of agents
     */
    private List<AgentProfile> extractAgentList(JSONArray jsonArray) throws JSONException {
        List<AgentProfile> agentList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            AgentProfile agent = new AgentProfile();
            JSONObject agentJson = jsonArray.getJSONObject(i);
            agent.setId(agentJson.getString("id"));
            agent.setName(agentJson.getString("name"));
            agent.setSurname(agentJson.getString("surname"));
            agent.setPhone(agentJson.getString("phone"));
            agent.setEmail(agentJson.getString("email"));
            BankInfo bank = new BankInfo();
            bank.setName(agentJson.getString("bank_name"));
            agent.setWorkBank(bank);

            agentList.add(agent);
        }
        return agentList;
    }

}
