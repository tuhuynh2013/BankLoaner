package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CustomerAppManagerFragment extends Fragment {

    private View view;
    private List<ApplicationInfo> applications;


    public CustomerAppManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.frag_customer_app_manager, container, false);
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        getCustomerApplication(user);

        return view;
    }

    private void getCustomerApplication(final User user) {

        @SuppressLint("StaticFieldLeak")
        class CustomerApplicationList extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... voids) {
                // Creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("customer_id", Integer.toString(user.getId()));
                // Return the response
                return requestHandler.sendPostRequest(URLs.URL_GET_CUSTOMER_APPLICATIONS, params);
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
                    if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_retrieve_applications_success))) {

                        // Getting the user from the response
                        JSONArray jsonArray = obj.getJSONArray("applications");
                        applications = extractApplicationList(jsonArray);
                        // Sort by date
                        Collections.sort(applications, new Comparator<ApplicationInfo>() {
                            @Override
                            public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        });
                        Collections.reverse(applications);

                        ListView listView = view.findViewById(R.id.lv_application_list);
                        CustomerAppManagerAdapter customerAppManagerAdapter = new CustomerAppManagerAdapter(view.getContext(), R.layout.customer_app_adapter, applications);
                        listView.setAdapter(customerAppManagerAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                ApplicationInfo applicationInfo = (ApplicationInfo) parent.getItemAtPosition(position);
                                Intent intent = new Intent(parent.getContext(), CustomerAppInfoActivity.class);
                                intent.putExtra("caller", "CustomerAppManagerFragment");
                                intent.putExtra("application", applicationInfo);
                                parent.getContext().startActivity(intent);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        CustomerApplicationList customerApplicationList = new CustomerApplicationList();
        customerApplicationList.execute();

    }

    /**
     * Extract list of applications from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of applications
     */
    private List<ApplicationInfo> extractApplicationList(JSONArray jsonArray) throws JSONException {
        List<ApplicationInfo> applicationList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject applicationJson = jsonArray.getJSONObject(i);
            // Get agent profile
            AgentProfile agent = new AgentProfile();
            String strAgentID = applicationJson.getString("agent_id");
            if (!TextUtils.isEmpty(strAgentID)) {
                agent.setId(Integer.parseInt(strAgentID));
            }
            agent.setName(applicationJson.getString("agent_name"));
            agent.setSurname(applicationJson.getString("agent_surname"));
            // Get bank information
            BankInfo bankInfo = new BankInfo();
            bankInfo.setId(applicationJson.getInt("bank_id"));
            bankInfo.setName(applicationJson.getString("bank_name"));
            bankInfo.setShortName(applicationJson.getString("short_name"));
            // Convert date
            Date date = CustomUtil.convertStringToDate(applicationJson.getString("date"), "default");

            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setId(applicationJson.getInt("application_id"));
            applicationInfo.setMonth(applicationJson.getInt("month"));
            applicationInfo.setAmount(applicationJson.getLong("amount"));
            applicationInfo.setInterest(applicationJson.getDouble("interest"));
            applicationInfo.setAgent(agent);
            applicationInfo.setBankInfo(bankInfo);
            applicationInfo.setDate(date);
            applicationInfo.setStatus(applicationJson.getString("status"));
            applicationList.add(applicationInfo);
        }
        return applicationList;
    }


}