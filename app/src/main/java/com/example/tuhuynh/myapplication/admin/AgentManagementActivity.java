package com.example.tuhuynh.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.GetAgentsAsync;
import com.example.tuhuynh.myapplication.asynctask.GetAgentsCallBack;
import com.example.tuhuynh.myapplication.customadapter.AgentListAdapter;

import java.util.List;


public class AgentManagementActivity extends AppCompatActivity implements GetAgentsCallBack {

    ListView lvAgents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_management);
        setTitle(getString(R.string.title_agent_management));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initial listview element
        lvAgents = findViewById(R.id.lv_agents);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
                startActivity(new Intent(getApplicationContext(), AgentRegisterActivity.class));
            }
        });
        new GetAgentsAsync(this, this).execute();
    }

    @Override
    public void responseFromGetAgents(List<AgentProfile> agentProfiles, String msg) {
        AgentListAdapter agentListAdapter = new AgentListAdapter(this, R.layout.agent_list_adapter, agentProfiles);
        lvAgents.setAdapter(agentListAdapter);
    }

}
