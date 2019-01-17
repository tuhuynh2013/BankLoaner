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

public class AssignedApplicationActivity extends AppCompatActivity implements GetAgentApplicationCallBack {

    User user;
    ListView lvAssignedApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_application);
        setTitle(getString(R.string.title_assigned_application));

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
        lvAssignedApplication = findViewById(R.id.lv_assigned_applications);

        // Get assigned applications
        new GetAgentApplicationsAsync(this, this, user.getId(), "AssignedApplicationActivity").execute();

    }

    @Override
    public void responseFromAsync(List<ApplicationInfo> applications, String msg) {
        // If applications not empty, set array adapter
        if (!applications.isEmpty()) {
            AssignedApplicationAdapter assignedAdapter = new AssignedApplicationAdapter(this, R.layout.assigned_application_adapter, applications);
            lvAssignedApplication.setAdapter(assignedAdapter);

            lvAssignedApplication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ApplicationInfo application = (ApplicationInfo) parent.getItemAtPosition(position);
                    Intent intent = new Intent(parent.getContext(), AgentApplicationActivity.class);
                    intent.putExtra("application", application);
                    parent.getContext().startActivity(intent);
                }
            });
        } else {
            // Create text view to show message
            lvAssignedApplication.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }

    }


}
