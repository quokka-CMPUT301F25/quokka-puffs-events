package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokkapuffevents.R;

public class AdminActivity extends AppCompatActivity {

    TextView title;
    ImageButton imagesIcon;
    ImageButton eventsIcon;
    ImageButton usersIcon;
    ImageButton notificationsIcon;
    ImageButton settingIcon;

    public AdminActivity() {
        super(R.layout.activity_admin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        FragmentManager fragmentManager = getSupportFragmentManager();

        replaceFragment(fragmentManager, new AdminEventFragment());


        title = findViewById(R.id.adminTitle);
        imagesIcon = findViewById(R.id.imagesIcon);
        eventsIcon = findViewById(R.id.eventsIcon);
        usersIcon = findViewById(R.id.usersIcon);
        notificationsIcon = findViewById(R.id.notificationsIcon);
        settingIcon = findViewById(R.id.settingsIcon);

        imagesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Images");
                replaceFragment(fragmentManager, new AdminPhotoFragment());
            }
        });

        eventsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Events");
                replaceFragment(fragmentManager, new AdminEventFragment());
            }
        });

        usersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Users");
                replaceFragment(fragmentManager, new AdminUserFragment());
            }
        });

        notificationsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Notifications");
                replaceFragment(fragmentManager, new AdminNotificationFragment());
            }
        });

        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Settings");
                replaceFragment(fragmentManager, new SettingFragment());
            }
        });

    }

    public void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.adminFragmentContainer, fragment, null)
                .commit();
    }

    public void goBackToLogin() {
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void changeProfile() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        replaceFragment(fragmentManager, new ChangeProfileSettings());
    }

    public void goSettingFragment () {
        FragmentManager fragmentManager = getSupportFragmentManager();
        replaceFragment(fragmentManager, new SettingFragment());
    }



}
