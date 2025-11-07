package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;
import com.google.android.gms.tasks.OnSuccessListener;

public class AdminDashboardActivity extends AppCompatActivity {
    String userID;
    Database db;
    Button viewEventsButton;
    Button viewNotifsButton;
    Button viewProfilesButton;
    Button viewImagesButton;
    Button settingsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);
        // GET INSTANCE OF DATABASE AND CURRENT USER ID
        db = Database.getInstance();
        userID = String.valueOf(db.GetCurrentUserID());

        db.GetUser(userID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getAccountType() == -1) {
                    adminDashboard();
                }
                // Load initial fragment
               // replaceFragment(new ());
            }
        });
    }

    public void adminDashboard() {
        //Initialize Buttons
        initializeViews();

        viewEventsButton.setOnClickListener(View -> {
            //TODO: make a viewEventsFragment
            //replaceFragment(new insertFragmentHere);
        });

        viewNotifsButton.setOnClickListener(View -> {
            //TODO: make a viewNotifsFragment and XML
            //replaceFragment(new insertFragmentHere);
        });

        viewProfilesButton.setOnClickListener(View -> {
            //TODO: make a viewProfilesFragment and XML
            //replaceFragment(new insertFragmentHere);
        });

        viewImagesButton.setOnClickListener(View -> {
            //TODO: make a viewImagesFragment and XML
            //replaceFragment(new insertFragmentHere);
        });

        settingsButton.setOnClickListener(View -> {
            replaceFragment(new SettingFragment());
        });

    }

    private void initializeViews() {
        viewEventsButton = findViewById(R.id.home_button);
        viewNotifsButton = findViewById(R.id.all_events_button);
        viewProfilesButton = findViewById(R.id.add_events_button);
        viewImagesButton = findViewById(R.id.notifs_button);
        settingsButton = findViewById(R.id.settings_button);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
