package com.example.tuhuynh.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.asynctask.GetAgentsAsync;
import com.example.tuhuynh.myapplication.asynctask.GetAgentsCallBack;
import com.example.tuhuynh.myapplication.customadapter.AgentListAdapter;

import java.util.List;


public class AgentManagementActivity extends AppCompatActivity implements GetAgentsCallBack, SearchView.OnQueryTextListener {

    ListView lvAgents;
    AgentListAdapter agentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_management);
        setTitle(getString(R.string.title_agent_management));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial listview element
        lvAgents = findViewById(R.id.lv_agents);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AgentRegisterActivity.class));
            }
        });
        new GetAgentsAsync(this, this).execute();
    }

    @Override
    public void responseFromGetAgents(List<AgentProfile> agentProfiles, String msg) {
        // If agentProfiles not empty, set array adapter
        if (!agentProfiles.isEmpty()) {
            agentListAdapter = new AgentListAdapter(this, R.layout.agent_list_adapter, agentProfiles);
            lvAgents.setAdapter(agentListAdapter);

            lvAgents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AgentProfile agentProfile = (AgentProfile) parent.getItemAtPosition(position);
                    Intent intent = new Intent(parent.getContext(), AgentProfileActivity.class);
                    intent.putExtra("agentProfile", agentProfile);
                    parent.getContext().startActivity(intent);
                }
            });

        } else {
            // Create text view to show message
            lvAgents.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        this.agentListAdapter.getFilter().filter(s);
        return true;
    }


}
