package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class AssignFCMTokenAsync extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private String userID;
    private String token;
    private static String TAG = AssignFCMTokenAsync.class.getSimpleName();

    public AssignFCMTokenAsync(String userID, String token) {
        this.userID = userID;
        this.token = token;
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
        params.put("user_id", userID);
        params.put("token", token);

        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_ASSIGN_TOKEN, params);
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
                Log.v(TAG, msg);
            } else {
                Log.e(TAG, msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}