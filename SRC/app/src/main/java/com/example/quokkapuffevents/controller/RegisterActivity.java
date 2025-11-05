package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registration_page);

        db = Database.getInstance();

        //--UI BINDING--
        EditText email = findViewById(R.id.register_email);
        EditText username = findViewById(R.id.register_username);
        EditText password = findViewById(R.id.register_password);
        EditText firstName = findViewById(R.id.register_first_name);
        EditText lastName = findViewById(R.id.register_last_name);
        EditText phoneNumber = findViewById(R.id.register_phone_number);
        CheckBox entrantButton = findViewById(R.id.register_entrant_button);
        CheckBox organizerButton = findViewById(R.id.register_organizer_button);
        Button backButton = findViewById(R.id.login_page_button);
        Button signUpButton = findViewById(R.id.register_info_button);

        // Setting filters to restrict Username input from containing symbols
        username.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    if (source.toString().matches("^[a-zA-Z0-9]+$")) {
                        return null; // Accept valid input
                    } else {
                        return "";   // Reject anything else (symbols, spaces, etc.)
                    }
                }
        });

        entrantButton.setOnCheckedChangeListener((button, isChecked) -> {
            // Uncheck organizer button if its checked
            if(isChecked) {
                organizerButton.setChecked(false);
            }
        });

        organizerButton.setOnCheckedChangeListener((button, isChecked) -> {
            // Uncheck entrant button if its checked
            if(isChecked) {
                entrantButton.setChecked(false);
            }
        });

        backButton.setOnClickListener(v -> {
            SwitchActivity(LoginActivity.class);
        });

        signUpButton.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String firstNameText = firstName.getText().toString().trim();
            String lastNameText = lastName.getText().toString().trim();
            String phoneNumberText = phoneNumber.getText().toString().trim();

            ValidateInformation(usernameText, emailText, passwordText, firstNameText, lastNameText, phoneNumberText, entrantButton, organizerButton);
        });
    }

    //Changing activity depending on what the user has selected
    // Note the `Class<?>` means it can be any class
    private void SwitchActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    //Display's if the Username or Email Address is already in use
    private void DisplayInUseErrorMsg() {
        Toast.makeText(this, "Email Address or Username is already in use", Toast.LENGTH_SHORT).show();
    }

    //Display's if the one of input fields are left blank or a role is not selected
    private void DisplayMissInfoErrorMsg() {
        Toast.makeText(this, "A field is missing information or a role is not selected", Toast.LENGTH_SHORT).show();
    }

    //Validates the information entered by the user
    private void ValidateInformation(String username, String email, String password, String firstName, String lastName, String phone, CheckBox entrant, CheckBox organizer){
        db.ValidatePasswordByEmail(email, password, users -> {
            if (!users.isEmpty()) {
                DisplayInUseErrorMsg();
            }
            else {
                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty()) {
                    if (entrant.isChecked()) {

                        //Hashing password
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-512");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        byte[] hashedPasswordByte = md.digest(password.getBytes(StandardCharsets.UTF_8));
                        String hashedPassword = new String(hashedPasswordByte);

                        //Creating User
                        User newUser = db.CreateUser(email, 0, hashedPassword, username, firstName, lastName, phone);
                        db.SetUserID(newUser.getId());

                        SwitchActivity(DashboardActivity.class);
                    }
                    else if (organizer.isChecked()) {

                        //Hashing password
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-512");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        byte[] hashedPasswordByte = md.digest(password.getBytes(StandardCharsets.UTF_8));
                        String hashedPassword = new String(hashedPasswordByte);

                        //Creating User
                        User newUser = db.CreateUser(email, 1, hashedPassword, username, firstName, lastName, phone);
                        db.SetUserID(newUser.getId());

                        SwitchActivity(DashboardActivity.class);
                    }
                    else {
                        DisplayMissInfoErrorMsg();
                    }
                }
                else {
                    DisplayMissInfoErrorMsg();
                }
            }
        });
    }
}
