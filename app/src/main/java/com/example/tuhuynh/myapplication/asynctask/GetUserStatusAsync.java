package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GetUserStatusAsync extends AsyncTask<Void, Void, String> {

    private GetUserStatusCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private String email;

    public GetUserStatusAsync(GetUserStatusCallBack cb, String email) {
        this.cb = cb;
        this.email = email;
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
        params.put("email", email);
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GET_USER_STATUS, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
            String status = "";

            // If no error in response
            if (!obj.getBoolean("error")) {
                status = obj.getString("status");
                cb.responseFromGetUserStatus(true, status, msg);
            } else {
                cb.responseFromGetUserStatus(false, status, msg);
            }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

}