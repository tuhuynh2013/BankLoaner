package com.example.tuhuynh.myapplication.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
import com.example.tuhuynh.myapplication.connecthandler.URLs;
import com.example.tuhuynh.myapplication.firebase.NotificationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SendNotificationAsync extends AsyncTask<Void, Void, String> {
    @SuppressLint("StaticFieldLeak")
    private NotificationInfo notificationInfo;
    private final String TAG = SendNotificationAsync.class.getSimpleName();

    public SendNotificationAsync(NotificationInfo notificationInfo) {
        this.notificationInfo = notificationInfo;
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
        params.put("fcm_token", notificationInfo.getFcmToken());
        params.put("title", notificationInfo.getTile());
        params.put("message", notificationInfo.getMessage());

        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_SEND_NOTIFICATION, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
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
