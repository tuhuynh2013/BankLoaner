package com.example.tuhuynh.myapplication.asynctask;

public interface GetUserStatusCallBack {
    void responseFromGoogleRegister(boolean isUserExisted, String status, String msg);
}
