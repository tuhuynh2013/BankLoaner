package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GoogleRegisterAsync extends AsyncTask<Void, Void, String> {

    private GoogleRegisterCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private UserProfile userProfile;

    public GoogleRegisterAsync(GoogleRegisterCallBack cb, UserProfile userProfile) {
        this.cb = cb;
        this.userProfile = userProfile;
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
        params.put("id", userProfile.getId());
        params.put("name", userProfile.getName());
        params.put("email", userProfile.getEmail());
        params.put("role", userProfile.getRole());

        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_GOOGLE_REGISTER, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
            cb.responseFromGoogleRegister(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}