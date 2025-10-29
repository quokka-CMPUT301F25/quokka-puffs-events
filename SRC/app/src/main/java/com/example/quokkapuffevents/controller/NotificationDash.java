package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.*;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationDash extends AppCompatActivity {

    Database db = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notification_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.NotifDash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setup
        ListView listView = findViewById(R.id.NotifList);
        NotificationArrayAdapter adapter = new NotificationArrayAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);

        db.SetUserID("Y1Ysnx9E2DuI4ojqEwBd");
        //Getting the users notifications
        db.GetUser(db.GetCurrentUserID(), user -> {
            // refresh adapter
            db.GetUserNotifications(user, adapter::setNotifications);
        });

    }
}
