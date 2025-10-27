package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

        User mockUser = new User("123", "email", 0, "password", "username");

        User user = db.getUser(mockUser.getId());
        ArrayList<Notif> notifications = db.getUserNotifications(user);

        /*ToDo Display notifications in a list view
            * Create an array adapter
            * set the array adapter to the list view
        */

        NotificationArrayAdapter adapter = new NotificationArrayAdapter(this, notifications);
        ListView listView = findViewById(R.id.NotifList);
        listView.setAdapter(adapter);
    }
}
