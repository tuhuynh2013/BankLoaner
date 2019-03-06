package com.example.tuhuynh.myapplication.customer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.bank.BankInformationActivity;
import com.example.tuhuynh.myapplication.bank.InterestAmount;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.customadapter.BankListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BankListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View view;
    private List<BankInfo> banks;
    private ProgressDialog pDialog;
    private BankListAdapter bankListAdapter;

    public BankListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.frag_bank_list, container, false);
        pDialog = new ProgressDialog(getContext());
        displayProgressDialog();

        setHasOptionsMenu(true);

        FloatingActionButton fabSearch = view.findViewById(R.id.fab_search);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoanSearchActivity.class);
                intent.putExtra("banks", (Serializable) banks);
                startActivity(intent);
            }
        });

        getBanks();

        return view;
    }

    /**
     * Uses to get list of banks from database
     */
    private void getBanks() {

        @SuppressLint("StaticFieldLeak")
        class BankList extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                // Creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("getbanks", "true");
                // Return the response
                return requestHandler.sendPostRequest(URLs.URL_GET_BANKS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    // Converting response to json object
                    JSONObject obj = new JSONObject(s);
                    String message = obj.getString("message");

                    // If no error in response
                    if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_retrieve_bank_list_success))) {
                        // Getting the userProfile from the response
                        JSONArray jsonArray = obj.getJSONArray("banks");
                        banks = extractBankList(jsonArray);
                        setListView();

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
    private void setListView() {
        final ListView lvBanks = view.findViewById(R.id.lv_bank_list);
        bankListAdapter = new BankListAdapter(view.getContext(), R.layout.bank_list_adapter, banks);
        lvBanks.setAdapter(bankListAdapter);

        lvBanks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                BankInfo bank = (BankInfo) parent.getItemAtPosition(position);
                Toast.makeText(v.getContext(), bank.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(parent.getContext(), BankInformationActivity.class);
                intent.putExtra("caller", "BankListFragment");
                intent.putExtra("bank", bank);
                parent.getContext().startActivity(intent);
            }
        });

        pDialog.dismiss();
    }

    /**
     * Extract list of banks from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of banks
     */
    private List<BankInfo> extractBankList(JSONArray jsonArray) throws JSONException {
        List<BankInfo> bankList = new ArrayList<>();
        List<InterestAmount> interestAmounts = new ArrayList<>();
        BankInfo bankInfo;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bankJson = jsonArray.getJSONObject(i);
            String shortName = bankJson.getString("short_name");
            int month = bankJson.getInt("month");
            double interest = bankJson.getDouble("interest");

            // If bankList empty or shortName doesn't exist, create new bank info and add to bankList
            if (bankList.isEmpty() || !isShortNameExist(shortName, bankList)) {
                int id = bankJson.getInt("id");
                String bankName = bankJson.getString("name");

                interestAmounts.clear();
                interestAmounts.add(new InterestAmount(month, interest));

                //Create BankInfo and add to bankList
                bankInfo = new BankInfo(id, bankName, shortName, new ArrayList<>(interestAmounts));
                bankList.add(bankInfo);
            }
            // If shortName exist, add new interest into exist BankInfo
            else {
                bankList.get(bankList.size() - 1).setInterestAmounts(new InterestAmount(month, interest));
            }
        }
        return bankList;
    }

    /**
     * Check if the bank is existed in bankList or not
     *
     * @param shortName short name of the bank
     * @param bankList List of banks
     * @return true if existed
     */
    private boolean isShortNameExist(String shortName, List<BankInfo> bankList) {
        for (BankInfo bankInfo : bankList) {
            if (bankInfo.getShortName().equalsIgnoreCase(shortName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Display Progress bar
     */
    private void displayProgressDialog() {
        pDialog.setMessage(getString(R.string.dialog_get_bank));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        this.bankListAdapter.getFilter().filter(s);
        return true;
    }


}
