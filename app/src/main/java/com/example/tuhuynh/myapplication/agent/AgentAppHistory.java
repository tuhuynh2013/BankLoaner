package com.example.tuhuynh.myapplication.agent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.List;

public class AgentAppHistory extends AppCompatActivity implements GetAgentAppsCallBack {

    User user;
    ListView lvAppHistory;
    private final String caller = "AgentAppHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_app_history);
        setTitle(getString(R.string.title_application_history));

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            user = SharedPrefManager.getInstance(this).getUser();
        }

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial listview element
        lvAppHistory = findViewById(R.id.lv_app_history);

        // Get assigned applications
        new GetAgentAppsAsync(this, this, user.getId(), caller).execute();
    }

    @Override
    public void responseFromAsync(List<ApplicationInfo> applications, String msg) {
        // If applications not empty, set array adapter
        if (!applications.isEmpty()) {
            AgentAppAdapter assignedAdapter = new AgentAppAdapter(this, R.layout.agent_app_adapter, applications, caller);
            lvAppHistory.setAdapter(assignedAdapter);

            lvAppHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ApplicationInfo application = (ApplicationInfo) parent.getItemAtPosition(position);
                    Intent intent = new Intent(parent.getContext(), AgentAppInfoActivity.class);
                    intent.putExtra("application", application);
                    intent.putExtra("caller", caller);
                    parent.getContext().startActivity(intent);
                }
            });
        } else {
            // Create text view to show message
            lvAppHistory.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }
    }


}
