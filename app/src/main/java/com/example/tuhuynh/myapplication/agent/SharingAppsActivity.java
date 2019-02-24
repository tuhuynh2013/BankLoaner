package com.example.tuhuynh.myapplication.agent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.asynctask.GetAgentAppsCallBack;
import com.example.tuhuynh.myapplication.asynctask.GetSharingAppsAsync;
import com.example.tuhuynh.myapplication.customadapter.SharingAppAdapter;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.List;


public class SharingAppsActivity extends AppCompatActivity implements GetAgentAppsCallBack {

    UserProfile userProfile;
    ListView lvSharingApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_apps);
        setTitle(getString(R.string.title_sharing_history));

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
        lvSharingApplications = findViewById(R.id.lv_sharing_applications);

        new GetSharingAppsAsync(this, this, userProfile.getId()).execute();
    }


    @Override
    public void responseFromAsync(List<ApplicationInfo> applications, String msg) {
        // If applications not empty, set array adapter
        if (!applications.isEmpty()) {
            SharingAppAdapter sharingAppAdapter = new SharingAppAdapter(this, R.layout.sharing_app_adapter, applications);
            lvSharingApplications.setAdapter(sharingAppAdapter);

//            lvSharingApplications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                    ApplicationInfo application = (ApplicationInfo) parent.getItemAtPosition(position);
//                    Intent intent = new Intent(parent.getContext(), AgentAppInfoActivity.class);
//                    intent.putExtra("application", application);
//                    intent.putExtra("caller", caller);
//                    parent.getContext().startActivity(intent);
//                }
//            });
        } else {
            // Create text view to show message
            lvSharingApplications.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }
    }


}
