package com.example.quokkapuffevents.controller;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;
import androidx.core.content.ContextCompat;

public class ChangeProfileSettings extends Fragment {

    /*
     * Main Purpose: Change user settings and profile
     *
     * User can change:
     *   Notifications
     *   Email
     *   Name
     *   Phone Number // Optional
     *   Delete Account
     *
     * Additional: Provide visual updates to show changes:
     *
     *   TODO: Background of changed text or edited switch becomes orange.
     *
     * */

    DashboardActivity dashboardActivity;
    private Database db; // collection we want to access
    private User currentUser; //
    String userID;

    /* Editable Text Inputs*/
    EditText email;
    EditText contact;
    EditText name;

    /* Buttons / Interactions */

    Switch allowNotifs;
    Button revertBtn;
    Button confirmBtn;
    Button deleteBtn;

    String currName;
    String currEmail;
    String currContact;


    //    Stole this from Seth -- HAHA SORRY! -Kyle.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
        String userID = String.valueOf(db.GetCurrentUserID());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_profile_settings, container);
        initializeViews(view);
        updateEditableUserInformation();
        setUpListeners(view);
        return view;
    }

    public void initializeViews(View v) {
        /*
          Initializes all attributes for the fragment
          @param v
         * View of the ChangeProfileSettings Fragment
         */

        email = v.findViewById(R.id.userEmailTextInput);
        name = v.findViewById(R.id.userFirstAndLastNameTextInput);
        contact = v.findViewById(R.id.userContactInformationInput);

        allowNotifs = v.findViewById(R.id.enableNotificationsSwitchBtn);
        revertBtn = v.findViewById(R.id.revertChangesBtn);
        confirmBtn = v.findViewById(R.id.confirmChangesBtn);
        deleteBtn = v.findViewById(R.id.deleteAccountBtn);

        userID = db.GetCurrentUserID();

    }

    public void updateEditableUserInformation(){
        /*
         * TODO: Update the editable information with the current user information
         *
         * */

        db.GetUser(userID, user -> {

            email.setText(user.getEmail());
            name.setText(user.getUserName());

//            TODO: check if theres a phone number associated to the user
            /*
             *  if have a phone number
             *
             *       edit text and display phone number
             * */
        });

        currName = name.getText().toString();
        currEmail = email.getText().toString();
        currContact = contact.getText().toString();


    }
    public void setUpListeners(View v) {

//        BUTTONS vvv

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkInput()) {
                    confirmChanges();
                }

            }

        });

        revertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertChanges();
            }
        });


//        EditText vvvv


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(currEmail.equals(email.getText().toString())) {

                    email.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(currContact.equals(contact.getText().toString())) {

                    contact.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(currName.equals(name.getText().toString())) {

                     name.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }

    public void deleteUserAccount() {
        /*
         * TODO: Delete the user from the database. Also have a confirm button with it.
         * */
        db.DeleteUser(userID);
        dashboardActivity.goBackToLogin();
//        Go back to login

    }

    public boolean checkInput() {
        /*
         * TODO: Check all user input is in the correct format before confirm changes
         *
         * */



        return true;
    }

    public void confirmChanges() {
        /*
         * TODO: Confirm the changes of the user input. Also send a confirmation notifications with it.
         *
         * */



    }

    public void revertChanges() {
        /*
         *  TODO: Rever the the changes the users may have done.
         *
         *
         * */




    }

}
