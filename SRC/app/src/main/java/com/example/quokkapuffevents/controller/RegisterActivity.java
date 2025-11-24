package com.example.quokkapuffevents.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

        // For formatting the phone number after the input
        phoneNumber.addTextChangedListener(new TextWatcher() {
            boolean isFormatting; // prevents infinite looping

            // Not needed for formatting
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // Not needed for formatting
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                // Remove everything except digits
                String digits = s.toString().replaceAll("\\D", "");

                // Limit to 10 digits
                if (digits.length() > 10) {
                    digits = digits.substring(0, 10);
                }

                // Build formatted phone number
                String formattedNumber = formatPhoneDigits(digits);

                // Replace the EditText value
                phoneNumber.removeTextChangedListener(this);
                phoneNumber.setText(formattedNumber);
                phoneNumber.setSelection(formattedNumber.length());
                phoneNumber.addTextChangedListener(this);

                isFormatting = false;
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

        backButton.setOnClickListener(v -> SwitchActivity(LoginActivity.class));

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

    /**
     * Changes currently displayed activity depending on what the user has pressed.
     * @param activity
     * The new activity class to display.
     */
    private void SwitchActivity(Class<?> activity) {
        // Note the `Class<?>` means it can be any class

        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /**
     * Formats an inputted phone number to fit the (XXX) XXX-XXXX format.
     * @param digits
     * The unformatted phone number digits.
     * @return
     * The formatted phone number.
     */
    private String formatPhoneDigits(String digits) {
        StringBuilder formatted = new StringBuilder();

        int length = digits.length();

        if (length > 0) {
            formatted.append("(").append(digits, 0, Math.min(3, length));
        }
        if (length >= 4) {
            formatted.append(") ").append(digits, 3, Math.min(6, length));
        }
        if (length >= 7) {
            formatted.append("-").append(digits.substring(6));
        }

        return formatted.toString();
    }

    /**
     * A toast 'error' message to display when the entered Username or Email Address is already in
     * use.
     */
    private void DisplayInUseErrorMsg() {
        Toast.makeText(this, "Email Address or Username is already in use", Toast.LENGTH_SHORT).show();
    }

    /**
     * A toast 'error' message to display when one or more of the input fields are left blank and or
     * a role is not selected.
     */
    private void DisplayMissInfoErrorMsg() {
        Toast.makeText(this, "A field is missing information or a role is not selected", Toast.LENGTH_SHORT).show();
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

    /**
     * Validates that the information entered by the user is fully filled out / available.
     * @param username
     * The user submitted username.
     * @param email
     * The user submitted email address.
     * @param password
     * The user submitted password (unhashed).
     * @param firstName
     * The user submitted first name.
     * @param lastName
     * The user submitted last name.
     * @param phone
     * The user submitted phone number (optional).
     * @param entrant
     * A checkbox to check if the user wants to be registered as an entrant.
     * @param organizer
     * A checkbox to check if the user wants to be registered as an organizer.
     */
    private void ValidateInformation(String username, String email, String password, String firstName, String lastName, String phone, CheckBox entrant, CheckBox organizer){
        db.ValidatePasswordByEmail(email, password, users -> {
            if (!users.isEmpty()) {
                DisplayInUseErrorMsg();
            }
            else {
                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
                    if (entrant.isChecked()) {

                        //Hashing password
                        String hashedPassword = PasswordHashing(password);

                        //Creating User
                        User newUser = db.CreateUser(email, 0, hashedPassword, username, firstName, lastName, phone);
                        db.SetUserID(newUser.getId());

                        SwitchActivity(DashboardActivity.class);
                    }
                    else if (organizer.isChecked()) {

                        //Hashing password
                        String hashedPassword = PasswordHashing(password);

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
