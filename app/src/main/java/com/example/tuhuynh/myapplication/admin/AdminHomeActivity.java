package com.example.tuhuynh.myapplication.admin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.user.AccountType;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.user.UserProfileActivity;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    UserProfile userProfile;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_nav_config);
        setTitle(getString(R.string.title_home));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("946395224241-a6e4tp9hlm3392ekj13n3c2lsf09us60.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // If the userProfile is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            userProfile = SharedPrefManager.getInstance(this).getUser();
            // Check if userProfile is signed in (non-null) and update UI accordingly.
            if (userProfile.getAccountType().equals(AccountType.GOOGLE)) {
                firebaseUser = mAuth.getCurrentUser();
            }
        }

        // Initial Navigation View
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.getHeaderView(0);
            TextView tvUsername = headerView.findViewById(R.id.tv_username);
            String fullName = CustomUtil.setFullName(userProfile.getName(), userProfile.getSurname());
            tvUsername.setText(fullName);
            ImageView imgProfile = headerView.findViewById(R.id.img_profile);
            if (firebaseUser != null) {
                Uri profilePicUrl = firebaseUser.getPhotoUrl();
                if (profilePicUrl != null) {
                    Glide.with(this).load(profilePicUrl)
                            .into(imgProfile);
                }
            }
        }

        // Set action for assigned linear layout
        findViewById(R.id.lnl_agent_management).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AgentManagementActivity.class));
            }
        });

    }

    /**
     * Handles a navigation drawer item click. It detects which item was
     * clicked and displays a toast message showing which item.
     *
     * @param item Item in the navigation drawer
     * @return Returns true after closing the nav drawer
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;

            case R.id.nav_setting:
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_signout:
                mAuth.signOut();
                // Google sign out
                mGoogleSignInClient.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                drawer.closeDrawer(GravityCompat.START);
                finish();
                return true;

            default:
                return false;
        }
    }


}

