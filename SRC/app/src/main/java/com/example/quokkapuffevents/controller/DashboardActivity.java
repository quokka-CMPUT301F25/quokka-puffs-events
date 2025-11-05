package com.example.quokkapuffevents.controller;

import android.content.Intent;
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

public class DashboardActivity extends AppCompatActivity {
    String userID;
    Database db;
    Button homeButton;
    Button viewEventsButton;
    Button addEventButton;
    Button notificationButton;
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
                if (user.getAccountType() == 0) {
                    entrantDashboard();
                } else if (user.getAccountType() == 1) {
                   //organizerDashboard();
                }
                // Load initial fragment
                replaceFragment(new NotificationFragment());
            }
        });
    }

    public void entrantDashboard() {
        //Initialize Buttons
        initializeViews();

        homeButton.setOnClickListener(View -> {
            replaceFragment(new NotificationFragment());
        });

        viewEventsButton.setOnClickListener(View -> {
            replaceFragment(new RegisterEventsFragment());
        });

        addEventButton.setOnClickListener(View -> {
            replaceFragment(new QRCodeFragment());
        });

        settingsButton.setOnClickListener(View -> {
            replaceFragment(new SettingFragment());
        });

    }

    public void organizerDashboard() {
        //Initialize Buttons
        initializeViews();

        homeButton.setOnClickListener(View -> {
            replaceFragment(new NotificationFragment());
        });

        viewEventsButton.setOnClickListener(View -> {
            replaceFragment(new RegisterEventsFragment()); //change this to smthg else
        });

        addEventButton.setOnClickListener(View -> {
            replaceFragment(new EventCreateFragment());
        });

        notificationButton.setOnClickListener(View -> {
            replaceFragment(new NotificationFragment());
        });

        settingsButton.setOnClickListener(View -> {
            replaceFragment(new SettingFragment());
        });

    }

    private void initializeViews() {
        homeButton = findViewById(R.id.button1);
        viewEventsButton = findViewById(R.id.button2);
        addEventButton = findViewById(R.id.button3);
        notificationButton = findViewById(R.id.button4);
        settingsButton = findViewById(R.id.button5);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void goBackToLogin() {

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
