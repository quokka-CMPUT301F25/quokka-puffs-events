package com.example.quokkapuffevents.controller;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
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
    EditText firstName;
    EditText lastName;

    /* Buttons / Interactions */

    Switch allowNotifs;
    Button revertBtn;
    Button confirmBtn;
    Button deleteBtn;


    //    Stole this from Seth -- HAHA SORRY! -Kyle.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.change_profile_settings, container, false);

        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        updateEditableUserInformation();
        setUpListeners(view);
    }

    public void initializeViews(View v) {
        /*
          Initializes all attributes for the fragment
          @param v
         * View of the ChangeProfileSettings Fragment
         */

        email = v.findViewById(R.id.userEmailTextInput);
        firstName = v.findViewById(R.id.userFirstNameTextInput);
        lastName = v.findViewById(R.id.userLastNameTextInput);
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

        String prevEmail = currentUser.getEmail();
        String prevFirstName = currentUser.getFirstName();
        String prevLastName = currentUser.getLastName();
        String prevContact = currentUser.getPhoneNumber();
        Boolean prevAllowNotifs = currentUser.getSendNotifications();

        email.setText(prevEmail);
        firstName.setText(prevFirstName);
        lastName.setText(prevLastName);
        contact.setText(prevContact);
        allowNotifs.setChecked(prevAllowNotifs);

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


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                if(currEmail.equals(email.getText().toString())) {
//
//                    email.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));
//
//                }
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
//                if(currContact.equals(contact.getText().toString())) {
//
//                    contact.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));
//
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                if(currFirstName.equals(firstName.getText().toString())) {
//
//                     firstName.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));
//
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                if(currLastName.equals(lastName.getText().toString())) {
//
//                    lastName.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orangebtn));
//
//                }
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
        db.DeleteUser(userID);
        db.SetUserID(null);
        ((DashboardActivity) getActivity()).goBackToLogin();
    }

    public void confirmChanges() {
        String newEmail = email.getText().toString().trim();
        String newFirstName = firstName.getText().toString().trim();
        String newLastName = lastName.getText().toString().trim();
        String newContact = contact.getText().toString().trim();
        Boolean newAllowNotifs = allowNotifs.getShowText();

        currentUser.setEmail(newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setPhoneNumber(newContact);
        currentUser.setSendNotifications(newAllowNotifs);

        db.SaveUser(currentUser);
        leaveProfileSettings();
    }

    public boolean checkInput() {

        String newEmail = email.getText().toString().trim();
        String newFirstName = firstName.getText().toString().trim();
        String newLastName = lastName.getText().toString().trim();
        String newContact = contact.getText().toString().trim();

        if (!(newEmail.length() < 40)) {
            return false;
        }
        if (!(newFirstName.length() < 20)) {
            return false;
        }
        if (!(newLastName.length() < 20)) {
            return false;
        }
//        if (!(newContact.length() < )) {
//            return false;
//        }
        return true;
    }

    public void revertChanges() {
        leaveProfileSettings();
    }

    public void leaveProfileSettings() {
        if (currentUser.getAccountType() == -1){
            ((AdminActivity) getActivity()).goSettingFragment(currentUser);
        }
        else {
            SettingFragment settingFragment = new SettingFragment();
            settingFragment.setCurrUser(currentUser);
            ((DashboardActivity) getActivity()).replaceFragment(settingFragment);
        }
    }

    public void setUser(User user) {this.currentUser = user;}
}
