package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.user.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class UpdateUserStatusAsync extends AsyncTask<Void, Void, String> {

    private UpdateUserStatusCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private UserProfile userProfile;

    public UpdateUserStatusAsync(UpdateUserStatusCallBack cb, UserProfile userProfile) {
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
        HashMap<String, String> params = new HashMap<>();
        params.put("id", userProfile.getId());
        params.put("status", userProfile.getStatus());
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_UPDATE_USER_STATUS, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
            cb.responseFromUpdateUserStatus(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
