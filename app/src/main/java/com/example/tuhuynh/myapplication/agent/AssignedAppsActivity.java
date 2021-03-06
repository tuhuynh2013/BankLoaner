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
import com.example.tuhuynh.myapplication.asynctask.GetAgentAppsAsync;
import com.example.tuhuynh.myapplication.asynctask.GetAgentAppsCallBack;
import com.example.tuhuynh.myapplication.customadapter.AgentAppAdapter;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.List;


public class AssignedAppsActivity extends AppCompatActivity implements GetAgentAppsCallBack {

    UserProfile userProfile;
    ListView lvAssignedApplications;
    private final String caller = "AssignedAppsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_apps);
        setTitle(getString(R.string.title_assigned_applications));

        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            userProfile = SharedPrefManager.getInstance(this).getUser();
        }

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initial listview element
        lvAssignedApplications = findViewById(R.id.lv_assigned_applications);

        // Get assigned applications
        new GetAgentAppsAsync(this, this, userProfile.getId(), caller).execute();

    }

    @Override
    public void responseFromGetAgentApps(List<ApplicationInfo> applications, String msg) {
        // If applications not empty, set array adapter
        if (!applications.isEmpty()) {
            AgentAppAdapter assignedAdapter = new AgentAppAdapter(this, R.layout.agent_app_adapter, applications, caller);
            lvAssignedApplications.setAdapter(assignedAdapter);

            lvAssignedApplications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            lvAssignedApplications.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }

    }


}
