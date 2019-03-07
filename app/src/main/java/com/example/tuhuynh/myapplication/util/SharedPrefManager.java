package com.example.tuhuynh.myapplication.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;


public class SharedPrefManager {
    // The constants
    private static final String SHARED_PREF_NAME = "session";
    private static final String KEY_ID = "keyid";
    private static final String KEY_PROFILE_IMG = "keyprofileimg";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_SURNAME = "keysurname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_IDENTITY = "keyidentity";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_ADDRESS = "keyaddress";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_ACCOUNTTYPE = "keytype";

    @SuppressLint("StaticFieldLeak")
    private static SharedPrefManager mInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // This method will store the userProfile data in shared preferences
    public void userLogin(UserProfile userProfile) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, userProfile.getId());
        editor.putString(KEY_PROFILE_IMG, userProfile.getProfileImg());
        editor.putString(KEY_NAME, userProfile.getName());
        editor.putString(KEY_SURNAME, userProfile.getSurname());
        editor.putString(KEY_EMAIL, userProfile.getEmail());
        editor.putString(KEY_IDENTITY, userProfile.getIdentity());
        editor.putString(KEY_GENDER, userProfile.getGender());
        editor.putString(KEY_PHONE, userProfile.getPhone());
        editor.putString(KEY_ADDRESS, userProfile.getAddress());
        editor.putString(KEY_ROLE, userProfile.getRole());
        if (!TextUtils.isEmpty(userProfile.getAccountType())) {
            editor.putString(KEY_ACCOUNTTYPE, userProfile.getAccountType());
        }
        editor.apply();
    }

    // This method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    // This method will give the logged in user
    public UserProfile getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserProfile(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_PROFILE_IMG, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_SURNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_IDENTITY, null),
                sharedPreferences.getString(KEY_GENDER, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_ADDRESS, null),
                sharedPreferences.getString(KEY_ROLE, null),
                sharedPreferences.getString(KEY_ACCOUNTTYPE, null)
        );
    }

    // This method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }


}
