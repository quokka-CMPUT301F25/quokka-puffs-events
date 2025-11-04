package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
        Button signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(v -> {

            String uNameOrEmail = username.getText().toString();
            String pass = password.getText().toString();
            boolean remember = rememberMe.isChecked();

            db.UserExists(uNameOrEmail, userExists -> {
                if(userExists) {
                    if(uNameOrEmail.contains("@")){
                        db.ValidateUserEmail(uNameOrEmail, pass, users -> {
                            if (!users.isEmpty()) {
                                db.SetUserID(users.get(0).getId());
                                SwitchActivity(DashboardActivity.class);
                            }
                            else {
                                DisplayErrorMsg();
                            }
                        });
                    }
                    else {
                        db.ValidateUserUsername(uNameOrEmail, pass, users -> {
                            System.out.println(users.size());
                            if (!users.isEmpty()) {
                                db.SetUserID(users.get(0).getId());
                                SwitchActivity(DashboardActivity.class);
                            }
                            else {
                                DisplayErrorMsg();
                            }
                        });
                    }
                }
                else {
                    DisplayErrorMsg();
                }
            });
        });
    }

    private void SwitchActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void DisplayErrorMsg(){
        Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
    }
    private void ValidateInformation(String uNameOrEmail,  String pass){
        db.UserExists(uNameOrEmail, userExists -> {
            if(userExists) {
                if(uNameOrEmail.contains("@")){
                    db.ValidateUserEmail(uNameOrEmail, pass, users -> {
                        if (!users.isEmpty()) {
                            db.SetUserID(users.get(0).getId());
                            SwitchActivity(DashboardActivity.class);
                        }
                        else {
                            DisplayErrorMsg();
                        }
                    });
                }
                else {
                    db.ValidateUserUsername(uNameOrEmail, pass, users -> {
                        if (!users.isEmpty()) {
                            db.SetUserID(users.get(0).getId());
                            SwitchActivity(DashboardActivity.class);
                        }
                        else {
                            DisplayErrorMsg();
                        }
                    });
                }
            }
            else {
                DisplayErrorMsg();
            }
        });
    }
}


