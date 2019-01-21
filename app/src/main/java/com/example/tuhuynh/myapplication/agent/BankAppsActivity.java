package com.example.tuhuynh.myapplication.agent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.appication.AssignAppToAgentAsync;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;

import java.util.List;

public class BankAppsActivity extends AppCompatActivity implements GetAgentAppsCallBack {

    User user;
    SwipeMenuListView slvBankApplication;
    AgentAppAdapter assignedAdapter;
    private final String caller = "BankAppsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_apps);

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
        slvBankApplication = findViewById(R.id.slv_bank_application);

        // Get assigned applications
        new GetAgentAppsAsync(this, this, user.getId(), caller).execute();

    }


    @Override
    public void responseFromAsync(List<ApplicationInfo> applications, String msg) {
        // If applications not empty, set array adapter
        if (!applications.isEmpty()) {
            assignedAdapter = new AgentAppAdapter(this, R.layout.agent_app_adapter, applications, caller);
            slvBankApplication.setAdapter(assignedAdapter);
            createSwipeMenu(applications);

        } else {
            // Create text view to show message
            slvBankApplication.setVisibility(View.INVISIBLE);
            TextView tvMsg = new TextView(this);
            tvMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            tvMsg.setText(msg);
            setContentView(tvMsg);
        }

    }

    /**
     *
     */
    private void createSwipeMenu(final List<ApplicationInfo> applications) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create "assign to me" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // Set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(83, 244,
                        66)));
                // Set item width
                openItem.setWidth(270);
                // Set item title
                openItem.setTitle(getString(R.string.item_assign_to_me));
                // Set item title font size
                openItem.setTitleSize(18);
                // Set item title font color
                openItem.setTitleColor(Color.WHITE);
                // Add to menu
                menu.addMenuItem(openItem);
            }
        };
        // Set creator
        slvBankApplication.setMenuCreator(creator);

        slvBankApplication.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        new AssignAppToAgentAsync(applications.get(position).getId(), user.getId(), ApplicationStatus.VALIDATING).execute();
                        slvBankApplication.getChildAt(position).startAnimation(createAnimation(position));
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    /**
     * Create animation
     */
    private Animation createAnimation(final int position) {
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                assignedAdapter.remove(assignedAdapter.getItem(position));
                assignedAdapter.notifyDataSetChanged();
            }
        });
        return animation;
    }


}
