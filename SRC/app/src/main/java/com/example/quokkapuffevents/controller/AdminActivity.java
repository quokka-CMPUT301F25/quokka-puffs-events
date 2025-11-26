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
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;

public class AdminActivity extends AppCompatActivity {

    Database db = Database.getInstance();
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

        replaceFragment(new AdminEventFragment());

        title = findViewById(R.id.adminTitle);
        imagesIcon = findViewById(R.id.imagesIcon);
        eventsIcon = findViewById(R.id.eventsIcon);
        usersIcon = findViewById(R.id.usersIcon);
        notificationsIcon = findViewById(R.id.notificationsIcon);
        settingIcon = findViewById(R.id.settingsIcon);

        imagesIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes admin user to view all images
             */
            @Override
            public void onClick(View v) {
                title.setText("All Images");
                replaceFragment(new AdminPhotoFragment());
            }
        });

        eventsIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes admin user to view all events
             */
            @Override
            public void onClick(View v) {
                title.setText("All Events");
                replaceFragment(new AdminEventFragment());
            }
        });

        usersIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes admin user to view all users
             */
            @Override
            public void onClick(View v) {
                title.setText("All Users");
                replaceFragment(new AdminUserFragment());
            }
        });

        notificationsIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes admin user to view all notifications
             */
            @Override
            public void onClick(View v) {
                title.setText("All Notifications");
                replaceFragment(new AdminNotificationFragment());
            }
        });

        settingIcon.setOnClickListener(new View.OnClickListener() {
            /**
             * Takes admin user to setting fragment
             */
            @Override
            public void onClick(View v) {
                title.setText("Settings");
                db.GetUser(db.GetCurrentUserID(), user -> {
                    SettingFragment settingFragment = new SettingFragment();
                    settingFragment.setCurrUser(user);
                    replaceFragment(settingFragment);
                });
            }
        });

    }

    public void replaceFragment(Fragment fragment) {
        /**
         * Replaces fragment in the slot depending on button clicked
         * @param fragment fragment to replace previous one with
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.adminFragmentContainer, fragment, null)
                .commit();
    }

    public void goBackToLogin() {
        /**
         * Takes user back to login page
         */
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeProfile(User user) {
        /**
         * Takes user to change profile settings page
         */
        ChangeProfileSettings editFragment = new ChangeProfileSettings();
        editFragment.setUser(user);
        replaceFragment(editFragment);
    }

    public void goSettingFragment (User user) {
        /**
         * Takes user to settings page
         */
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setCurrUser(user);
        replaceFragment(settingFragment);
    }

}
