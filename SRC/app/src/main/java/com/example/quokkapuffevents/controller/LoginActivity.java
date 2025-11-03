package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;

public class LoginActivity extends AppCompatActivity {

    private Database db;

    public static final String PREFS_NAME = "RemeberMePrefs";
    private static final String PREF_USERID = "userID";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        db = Database.getInstance();



        EditText username = findViewById(R.id.login_email_address);
        EditText password = findViewById(R.id.login_password);
        CheckBox rememberMe = findViewById(R.id.remember_me_button);

        String uNameOrEmail = username.getText().toString();
        String pass = password.getText().toString();
        boolean remember = rememberMe.isChecked();

        



    }
}
