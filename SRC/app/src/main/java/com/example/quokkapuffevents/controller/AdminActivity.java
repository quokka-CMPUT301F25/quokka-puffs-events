package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.quokkapuffevents.R;

public class AdminActivity extends AppCompatActivity {

    TextView title;
    ImageButton imagesIcon;
    ImageButton eventsIcon;
    ImageButton usersIcon;
    ImageButton notificationsIcon;

    public AdminActivity() {
        super(R.layout.activity_admin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.adminFragmentContainer, AdminEventFragment.class, null)
                    .commit();
        }

        title = findViewById(R.id.adminTitle);
        imagesIcon = findViewById(R.id.imagesIcon);
        eventsIcon = findViewById(R.id.eventsIcon);
        usersIcon = findViewById(R.id.usersIcon);
        notificationsIcon = findViewById(R.id.notificationsIcon);

        imagesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Images");
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.adminFragmentContainer, AdminPhotoFragment.class, null)
                        .commit();
            }
        });

        eventsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Events");
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.adminFragmentContainer, AdminEventFragment.class, null)
                        .commit();
            }
        });

        usersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Users");
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.adminFragmentContainer, AdminUserFragment.class, null)
                        .commit();
            }
        });

        notificationsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("All Notifications");
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.adminFragmentContainer, AdminNotificationFragment.class, null)
                        .commit();
            }
        });
    }
}
