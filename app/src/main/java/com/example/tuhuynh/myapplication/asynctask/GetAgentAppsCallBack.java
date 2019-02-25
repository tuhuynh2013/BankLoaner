package com.example.tuhuynh.myapplication.asynctask;

import com.example.tuhuynh.myapplication.appication.ApplicationInfo;

import java.util.List;


public interface GetAgentAppsCallBack {
    void responseFromGetAgentApps(List<ApplicationInfo> applications, String msg);
}
