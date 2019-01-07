package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.bank.InterestAmount;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.util.BankArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BankListFragment extends Fragment {

    private View view;
    private List<BankInfo> banks;

    public BankListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.frag_bank_list, container, false);
        getBanks();
        return view;
    }

    /**
     *
     */
    private void getBanks() {

        @SuppressLint("StaticFieldLeak")
        class BankList extends AsyncTask<Void, Void, String> {

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
                params.put("getbanks", "true");
                // Return the response
                return requestHandler.sendPostRequest(URLs.URL_GETBANKS, params);
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
                    if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_retrieve_banklist_success))) {

                        // Getting the user from the response
                        JSONArray jsonArray = obj.getJSONArray("banks");
                        banks = extractBanklist(jsonArray);

                        ListView listView = view.findViewById(R.id.bank_list);
                        BankArrayAdapter bankArrayAdapter = new BankArrayAdapter(view.getContext(), R.layout.bank_list, banks);
                        listView.setAdapter(bankArrayAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                BankInfo bank = (BankInfo) parent.getItemAtPosition(position);
                                Toast.makeText(v.getContext(), bank.getName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(parent.getContext(), BankInformationActivity.class);
                                intent.putExtra("bank", bank);
                                parent.getContext().startActivity(intent);
                            }
                        });

                    } else {
                        Toast.makeText(view.getContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        BankList banklist = new BankList();
        banklist.execute();
    }

    /**
     *
     */
    private List<BankInfo> extractBanklist(JSONArray jsonArray) throws JSONException {
        List<BankInfo> bankList = new ArrayList<>();
        List<InterestAmount> interestAmounts = new ArrayList<>();
        BankInfo bankInfo;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bankJson = jsonArray.getJSONObject(i);
            String shortName = bankJson.getString("short_name");
            int year = bankJson.getInt("year");
            double interest = bankJson.getDouble("interest");

            // If bankList empty or shortName doesn't exist, create new bank info and add to bankList
            if (bankList.isEmpty() || !isShortNameExist(shortName, bankList)) {
                int id = bankJson.getInt("id");
                String bankName = bankJson.getString("name");

                interestAmounts.clear();
                interestAmounts.add(new InterestAmount(year, interest));

                //Create BankInfo and add to bankList
                bankInfo = new BankInfo(id, bankName, shortName, new ArrayList<>(interestAmounts));
                bankList.add(bankInfo);
            }
            // If shortName exist, add new interest into exist BankInfo
            else {
                bankList.get(bankList.size() - 1).setInterestAmounts(new InterestAmount(year, interest));
            }
        }
        return bankList;
    }

    /**
     *
     */
    private boolean isShortNameExist(String shortName, List<BankInfo> bankList) {
        for (BankInfo bankInfo : bankList) {
            if (bankInfo.getShortName().equalsIgnoreCase(shortName)) {
                return true;
            }
        }
        return false;
    }


}
