package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private Database db;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        db = Database.getInstance();

        //--UI BINDING--
        EditText username = findViewById(R.id.login_email_address);
        EditText password = findViewById(R.id.login_password);
        CheckBox rememberMe = findViewById(R.id.remember_me_button);
        Button signUpButton = findViewById(R.id.register_page_button);
        Button signInButton = findViewById(R.id.sign_in_button);

        //Setting up login preferences for "remember me" option
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        //If the user has previously logged in, and has selected 'Remember me" fill Username and password
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            username.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }

        signUpButton.setOnClickListener(v -> {
            SwitchActivity(RegisterActivity.class);
        });

        signInButton.setOnClickListener(v -> {
            ValidateInformation(username.getText().toString(), password.getText().toString());

            //Store login information in SharedPreferences if `Remember me` is checked
            if (rememberMe.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", username.getText().toString());
                loginPrefsEditor.putString("password", password.getText().toString());
                loginPrefsEditor.commit();
            } else {
                //Clear the data if they uncheck it?

                //TODO: Decide if we want to do this or not/if we to even open the activity
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
        });
    }

    //Changing activity depending on what the user has selected
    // Note the `Class<?>` means it can be any class
    private void SwitchActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    //Display's if the Username or password is incorrect
    private void DisplayErrorMsg(){
        Toast.makeText(this, "Email Address / Username or Password is incorrect", Toast.LENGTH_SHORT).show();
    }

    //Validates the information entered by the user
    private void ValidateInformation(String uNameOrEmail,  String preHash){
        //Hashing password
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest(preHash.getBytes(StandardCharsets.UTF_8));
        String pass = new String(hashedPasswordByte);

        db.UserExists(uNameOrEmail, userExists -> {
            if(userExists) {
                //If the user has typed in an email, check it
                if(uNameOrEmail.contains("@")){
                    db.ValidatePasswordByEmail(uNameOrEmail, pass, users -> {
                        if (!users.isEmpty()) {
                            //Set the current user in the database and change activity
                            db.SetUserID(users.get(0).getId());
                            if(users.get(0).getAccountType() == -1)
                                SwitchActivity(AdminActivity.class);
                            else
                                SwitchActivity(DashboardActivity.class);
                        }
                        //If password is wrong then we have an error
                        else {
                            DisplayErrorMsg();
                        }
                    });
                }
                //If the user has typed in a username, check it
                else {
                    db.ValidateUserUsername(uNameOrEmail, pass, users -> {
                        if (!users.isEmpty()) {
                            //Set the current user in the database and change activity
                            db.SetUserID(users.get(0).getId());
                            if(users.get(0).getAccountType() == -1)
                                SwitchActivity(AdminActivity.class);
                            else
                                SwitchActivity(DashboardActivity.class);

                        }
                        //If no user is found then we have an error
                        else {
                            DisplayErrorMsg();
                        }
                    });
                }
            }
            // If no
            else {
                DisplayErrorMsg();
            }
        });
    }
}


