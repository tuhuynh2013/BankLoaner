package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GetUserFCMTokensAsync extends AsyncTask<Void, Void, String> {

    private GetUserFCMTokensCallBack cb;
    private String userID;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private final String TAG = GetUserFCMTokensAsync.class.getSimpleName();

    public GetUserFCMTokensAsync(GetUserFCMTokensCallBack cb, Context context, String userID) {
        this.cb = cb;
        this.userID = userID;
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
        params.put("user_id", userID);

        return requestHandler.sendPostRequest(URLs.URL_GET_FCM_TOKENS, params);
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
            if (!obj.getBoolean("error")) {
                // Getting the user from the response
                JSONArray jsonArray = obj.getJSONArray("tokens");
                List<String> tokens = extractTokenList(jsonArray);
                // Return the result
                cb.responseFromGetUserFCMTokens(tokens);
                Log.v(TAG, msg);
            } else {
                Log.e(TAG, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract list of tokens from JSONArray
     *
     * @param jsonArray JSONArray
     * @return List of tokens
     */
    private List<String> extractTokenList(JSONArray jsonArray) throws JSONException {
        List<String> tokenList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tokenJson = jsonArray.getJSONObject(i);
            String token = tokenJson.getString("token");
            tokenList.add(token);
        }
        return tokenList;
    }


}