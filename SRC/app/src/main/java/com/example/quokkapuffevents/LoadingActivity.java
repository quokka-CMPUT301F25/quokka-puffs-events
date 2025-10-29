package com.example.quokkapuffevents;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Database;

public class LoadingActivity extends AppCompatActivity {

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loading_page);

        // Using a Handler for non-blocking delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Code to execute after 10 seconds
                Intent intent=new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 5000);

        //TESTING THE CREATE EVENTS COMMENT OUT WHEN NOT NEEDED
        db = new Database();
    }
}