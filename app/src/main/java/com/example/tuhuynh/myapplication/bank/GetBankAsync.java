package com.example.tuhuynh.myapplication.bank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetBankAsync extends AsyncTask<Void, Void, String> {

    private GetBankCallBack response;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;

    public GetBankAsync(GetBankCallBack response, Context context) {
        this.response = response;
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
        params.put("getbanks", "true");
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GET_SIMPLE_BANKS, params);
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
            if (!obj.getBoolean("error") && message.equalsIgnoreCase(context.getString(R.string.msg_retrieve_banklist_success))) {

                // Getting the user from the response
                JSONArray jsonArray = obj.getJSONArray("banks");
                List<BankInfo> banks = extractBanklist(jsonArray);
                response.responseFromAsync(banks);

            } else {
                Toast.makeText(context, R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract list of banks from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of banks
     */
    private List<BankInfo> extractBanklist(JSONArray jsonArray) throws JSONException {
        List<BankInfo> bankList = new ArrayList<>();
        BankInfo bankInfo;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bankJson = jsonArray.getJSONObject(i);
            int id = bankJson.getInt("id");
            String bankName = bankJson.getString("name");
            String shortName = bankJson.getString("short_name");
            //Create BankInfo and add to bankList
            bankInfo = new BankInfo(id, bankName, shortName);
            bankList.add(bankInfo);
        }
        return bankList;
    }
}
