package com.example.tuhuynh.myapplication.appication;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ApplicationManagerFragment extends Fragment {

    private View view;
    private List<ApplicationInfo> applications;


    public ApplicationManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.frag_application_list, container, false);
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
                params.put("user_id", Integer.toString(user.getId()));
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

                        ListView listView = view.findViewById(R.id.lv_application_list);
                        ApplicationArrayAdapter applicationArrayAdapter = new ApplicationArrayAdapter(view.getContext(), R.layout.application_adapter, applications);
                        listView.setAdapter(applicationArrayAdapter);

//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                                BankInfo bank = (BankInfo) parent.getItemAtPosition(position);
//                                Toast.makeText(v.getContext(), bank.getName(), Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(parent.getContext(), BankInformationActivity.class);
//                                intent.putExtra("bank", bank);
//                                parent.getContext().startActivity(intent);
//                            }
//                        });

                    } else {
                        Toast.makeText(view.getContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
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
     * Extract list of banks from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of banks
     */
    private List<ApplicationInfo> extractApplicationList(JSONArray jsonArray) throws JSONException {
        List<ApplicationInfo> applicationList = new ArrayList<>();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy");

        for (int i = 0; i < jsonArray.length(); i++) {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            JSONObject applicationJson = jsonArray.getJSONObject(i);
            applicationInfo.setId(Integer.parseInt(applicationJson.getString("id")));
            applicationInfo.setMonth(Integer.parseInt(applicationJson.getString("month")));
            applicationInfo.setAmount(Long.parseLong(applicationJson.getString("amount")));
            applicationInfo.setInterest(Double.parseDouble(applicationJson.getString("interest")));
            applicationInfo.setAgentName(applicationJson.getString("full_name"));
            applicationInfo.setBankName(applicationJson.getString("bank_name"));
            applicationInfo.setShortName(applicationJson.getString("short_name"));
            Date date = null;
            try {
                date = formatter.parse(applicationJson.getString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            applicationInfo.setDate(date);
            applicationInfo.setStatus(applicationJson.getString("status"));

            applicationList.add(applicationInfo);
        }
        return applicationList;
    }


}
