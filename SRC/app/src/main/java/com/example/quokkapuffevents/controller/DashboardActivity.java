package com.example.quokkapuffevents.controller;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    String userID;
    Database db;
    ImageButton homeButton;
    ImageButton viewEventsButton;
    ImageButton addEventButton;
    ImageButton notificationButton;
    ImageButton settingsButton;
    TextView usernameText;
    TextView userFirstAndLastNameText;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);
        // GET INSTANCE OF DATABASE AND CURRENT USER ID
        db = Database.getInstance();
        userID = String.valueOf(db.GetCurrentUserID());

        checkNotificationPermission();

        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        db.GetUser(userID, user ->  {
            if (user.getAccountType() == 0) {
                entrantDashboard();
            } else if (user.getAccountType() == 1) {
                organizerDashboard();
            }
            // Load initial fragment
            replaceFragment(new HomeFragment());
        });

        //Handling QR Code events
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("EVENT_ID");
        if (eventID != null) {
            EntrantEventDetailsFragment frag = new EntrantEventDetailsFragment();
            db.GetEvent(eventID, event -> {
                frag.setEvent(event);
                replaceFragment(frag);
            });
        }
    }

    public void entrantDashboard() {
        //Initialize Buttons
        initializeViews();

        homeButton.setOnClickListener(View -> {
            replaceFragment(new HomeFragment());
        });

        viewEventsButton.setOnClickListener(View -> {
            replaceFragment(new RegisterEventsFragment());
        });

        addEventButton.setOnClickListener(View -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan a barcode or QR Code");
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        });

        settingsButton.setOnClickListener(View -> {
            //TODO Add a loading screen here
            db.GetUser(userID, user -> {
                SettingFragment settingFragment = new SettingFragment();
                settingFragment.setCurrUser(user);
                replaceFragment(settingFragment);
            });
        });

        notificationButton.setOnClickListener(View -> {
            replaceFragment(new NotificationFragment());
        });

    }

    public void organizerDashboard() {
        //Initialize Buttons
        initializeViews();
        viewEventsButton.setVisibility(GONE);

        homeButton.setOnClickListener(View -> {
            replaceFragment(new HomeFragment());
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
            //TODO Add a loading screen here
            db.GetUser(userID, user -> {
                SettingFragment settingFragment = new SettingFragment();
                settingFragment.setCurrUser(user);
                replaceFragment(settingFragment);
            });
        });

    }

    private void initializeViews() {
        homeButton = findViewById(R.id.home_button);
        viewEventsButton = findViewById(R.id.all_events_button);
        addEventButton = findViewById(R.id.add_events_button);
        notificationButton = findViewById(R.id.notifs_button);
        settingsButton = findViewById(R.id.settings_button);
        usernameText = findViewById(R.id.usernameText);
        userFirstAndLastNameText = findViewById(R.id.userFirstAndLastNameText);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void goBackToLogin() {
        loginPrefsEditor.clear();
        loginPrefsEditor.commit();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                String fullCode = intentResult.getContents(); // quokka-puff://event/awydgasuda
                String[] parts = fullCode.split("/"); // ["quokka-puff:", ... , "awydgasuda"]
                String eventID = parts[3];

                Toast.makeText(getBaseContext(), eventID, Toast.LENGTH_SHORT).show();

                db.GetEvent(eventID, event -> {
                    EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
                    entrantFrag.setEvent(event);
                    replaceFragment(entrantFrag);
                });
                //viewEventsButton.setText(intentResult.getFormatName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Ensures that the user has enabled push notifications for this app before sending one
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

}
