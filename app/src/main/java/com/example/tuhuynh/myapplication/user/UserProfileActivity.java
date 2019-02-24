package com.example.tuhuynh.myapplication.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tuhuynh.myapplication.R;


public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle(getString(R.string.title_profile));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String[] menu = new String[]{"Profile", "Change password"};
        final ListView lvUserProfile = findViewById(R.id.lv_userProfile);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        lvUserProfile.setAdapter(arrayAdapter);

        lvUserProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) lvUserProfile.getItemAtPosition(position);

                switch (itemValue) {
                    case "Profile":
                        startActivity(new Intent(parent.getContext(), UserProfileEditorActivity.class));
                        break;
                    case "Change password":
                        startActivity(new Intent(parent.getContext(), ChangePasswordActivity.class));
                        break;
                    default:
                        break;
                }

            }
        });
    }


}
