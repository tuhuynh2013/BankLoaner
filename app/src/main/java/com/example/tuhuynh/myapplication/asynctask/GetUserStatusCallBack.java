package com.example.tuhuynh.myapplication.asynctask;

public interface GetUserStatusCallBack {
    void responseFromGetUserStatus(boolean isUserExisted, String status, String msg);
}
