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


/**
 * Request update userProfile profile to db
 */
public class UpdateUserProfileAsync extends AsyncTask<Void, Void, String> {

    private UpdateUserProfileCallBack cb;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private UserProfile userProfile;

    public UpdateUserProfileAsync(UpdateUserProfileCallBack cb, Context context, UserProfile updateUserProfile) {
        this.cb = cb;
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
        this.userProfile = updateUserProfile;
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
        params.put("id", userProfile.getId());
        params.put("name", userProfile.getName());
        params.put("surname", userProfile.getSurname());
        params.put("identity", userProfile.getIdentity());
        params.put("gender", userProfile.getGender());
        params.put("phone", userProfile.getPhone());
        params.put("address", userProfile.getAddress());
        // Return the response
        return requestHandler.sendPostRequest(URLs.URL_UPDATE_USER_PROFILE, params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);

        try {
            // Converting response to json object
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("message");
            cb.responseFromUpdateUserProfile(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}