package com.example.tuhuynh.myapplication.asynctask;

import com.example.tuhuynh.myapplication.agent.AgentProfile;

import java.util.List;

public interface GetAgentsCallBack {
    void responseFromGetAgents(List<AgentProfile> agentProfiles, String msg);
}
