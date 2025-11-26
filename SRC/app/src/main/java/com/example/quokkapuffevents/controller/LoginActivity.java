package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Database db;
    private SharedPreferences.Editor loginPrefsEditor;
    private String possibleEventID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        //Handling QR Code
        // Check if the app was opened from a URI
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            Uri data = intent.getData(); // quokka-puff://event/awydgasuda
            List<String> segments = data.getPathSegments(); // ["awydgasuda"]
            possibleEventID = segments.get(0); //Getting the id
        }

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
            String userID = loginPreferences.getString("ID", "");
            Log.d("USER", userID);
            rememberMe.setChecked(true);
            db.SetUserID(userID);
            db.GetUser(userID, user -> {
                if(user.getAccountType() == -1) {
                    SwitchActivity(AdminActivity.class);
                }
                else {
                    Intent i = new Intent(this, DashboardActivity.class);
                    if (possibleEventID != null) {
                        i.putExtra("EVENT_ID", possibleEventID);
                    }
                    SwitchActivity(DashboardActivity.class);
                }
            });
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
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
        });
    }

    /**
     * Changes currently displayed activity depending on what the user has pressed.
     * @param activity
     * The new activity class to display.
     */
    private void SwitchActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /**
     * A toast error message to display for when the Email Address, Username or Password is incorrect.
     */
    private void DisplayErrorMsg(){
        Toast.makeText(this, "Email Address / Username or Password is incorrect", Toast.LENGTH_SHORT).show();
    }

    /**
     * Hashes the given password using the SHA-512 algorithm and returns the result as a String.
     * @param password
     * The password to hash.
     * @return
     * A String of the SHA-512 hash of the input password.
     */
    private String PasswordHashing(String password) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return new String(hashedPasswordByte);
    }

    //Validates the information entered by the user

    /**
     * Validates that the information entered by the user is fully filled out / available.
     * @param uNameOrEmail
     * The user submitted username or email address.
     * @param preHash
     * The user submitted password before being hashed.
     */
    private void ValidateInformation(String uNameOrEmail,  String preHash){

        // Hashing Password
        String pass = PasswordHashing(preHash);

        db.UserExists(uNameOrEmail, userExists -> {
            if(userExists) {
                //If the user has typed in an email, check it
                if(uNameOrEmail.contains("@")){
                    db.ValidatePasswordByEmail(uNameOrEmail, pass, users -> {
                        if (!users.isEmpty()) {
                            //Set the current user in the database and change activity
                            db.SetUserID(users.get(0).getId());

                            if(users.get(0).getAccountType() == -1) {
                                loginPrefsEditor.putString("ID", users.get(0).getId());
                                loginPrefsEditor.commit();
                                SwitchActivity(AdminActivity.class);
                            }
                            else {
                                Intent i = new Intent(this, DashboardActivity.class);
                                if (possibleEventID != null) {
                                    i.putExtra("EVENT_ID", possibleEventID);
                                }
                                loginPrefsEditor.putString("ID", users.get(0).getId());
                                loginPrefsEditor.commit();
                                SwitchActivity(DashboardActivity.class);
                            }
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
                            if(users.get(0).getAccountType() == -1) {
                                loginPrefsEditor.putString("ID", users.get(0).getId());
                                loginPrefsEditor.commit();
                                SwitchActivity(AdminActivity.class);
                            }
                            else {
                                Intent i = new Intent(this, DashboardActivity.class);
                                if (possibleEventID != null) {
                                    i.putExtra("EVENT_ID", possibleEventID);
                                }
                                loginPrefsEditor.putString("ID", users.get(0).getId());
                                loginPrefsEditor.commit();
                                SwitchActivity(DashboardActivity.class);
                            }
                        }
                        //If no then we have an error
                        else {
                            DisplayErrorMsg();
                        }
                    });
                }
            }
            // If no then we have an error
            else {
                DisplayErrorMsg();
            }
        });
    }
}


