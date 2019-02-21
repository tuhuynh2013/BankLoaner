package com.example.tuhuynh.myapplication.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.user.AccountType;
import com.example.tuhuynh.myapplication.user.LoginActivity;
import com.example.tuhuynh.myapplication.user.UserProfileActivity;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.SharedPrefManager;
import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.PagerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CustomerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Creates the content view and toolbar, sets up the drawer layout and the
     * action bar toggle, and sets up the navigation view.
     *
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_config);
        setTitle(getString(R.string.title_home));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("946395224241-a6e4tp9hlm3392ekj13n3c2lsf09us60.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        // If the user is not logged in, starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            user = SharedPrefManager.getInstance(this).getUser();
            // Check if user is signed in (non-null) and update UI accordingly.
            if (user.getAccountType().equals(AccountType.GOOGLE)) {
                firebaseUser = mAuth.getCurrentUser();
            }
        }

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_bank_list));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_application_list));

        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Use PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // If activity was called from LoanApplication activity, set fragment to BankStatus
        Intent intent = this.getIntent();
        String caller = intent.getStringExtra("caller");
        if (caller != null) {
            if (caller.equals("SubmitApplication")) {
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
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
            String fullName = CustomUtil.setFullName(user.getName(), user.getSurname());
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

    }

    /**
     * Handles the Back button: closes the nav drawer.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
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
                displayToast(getString(R.string.msg_setting));
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


//    /**
//     * Inflates the options menu.
//     * @param menu  Menu to inflate
//     * @return Returns true if menu is inflated.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    /**
//     * Handles a click on the Settings item in the options menu.
//     * @param item  Item in options menu that was clicked.
//     * @return Returns true if the item was Settings.
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    /**
     * Displays a toast message.
     *
     * @param message Message to display in toast
     */
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
