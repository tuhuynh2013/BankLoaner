package com.example.tuhuynh.myapplication.agent;

import com.example.tuhuynh.myapplication.appication.ApplicationInfo;

import java.util.List;

public interface GetAgentApplicationCallBack {
    void responseFromAsync(List<ApplicationInfo> applications, String msg);
}