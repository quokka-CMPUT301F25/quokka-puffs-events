package com.example.quokkapuffevents.controller;


import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;
import com.google.android.gms.maps.model.Dash;

import java.util.concurrent.atomic.AtomicBoolean;

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
     *
     */

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
    Button setAddressBtn;

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
        setUpListeners();
    }

    public void initializeViews(View v) {
        /**
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
        setAddressBtn = v.findViewById(R.id.setUserAddressBtn);

        userID = db.GetCurrentUserID();
    }

    public void updateEditableUserInformation(){

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
    public void setUpListeners() {
        /**
         Initializes all buttons for the fragment
         */
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
                if (checkInput()) {
                    confirmChanges();
                } else {
                    CharSequence message = "One of your input is already taken.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getContext(), message, duration);
                    toast.show();
                }
            }
        });

        revertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveProfileSettings();
            }
        });

        allowNotifs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ask activity to check/request notification permission
                ((DashboardActivity) requireActivity()).checkNotificationPermission();
            } else {
                // User manually turned off switch
                Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        setAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseUserLocationFragment frag = new ChooseUserLocationFragment();
                frag.SetUser(currentUser);
                ((DashboardActivity) getActivity()).replaceFragment(frag);
            }
        });

    }
    public void deleteUserAccount() {
        /**
         Deletes user's account and sends them back to login page
         */
        db.DeleteUser(userID);
        db.SetUserID(null);
        ((DashboardActivity) getActivity()).goBackToLogin();
    }

    public void confirmChanges() {
        /**
         Confirms changes to user's edited profile information
         */
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
        /**
         Validates all user's input to edited profile page
         */
        AtomicBoolean valid = new AtomicBoolean(true);

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

        db.ListUsers(users -> {
//         Check all inputs
            for(User u: users) {
                if(u.getEmail().equals(newEmail)) {
                    valid.set(false);
                }
                if(u.getFirstName().equals(newFirstName)) {
                    valid.set(false);
                }
                if(u.getLastName().equals(newLastName)) {
                    valid.set(false);
                }
                if(u.getPhoneNumber().equals(newContact)) {
                    valid.set(false);
                }
            }
        });
        return valid.get();
    }

    public void leaveProfileSettings() {
        /**
         * Takes user to appropriate setting fragment in respectful activity
         */
        if (currentUser.getAccountType() == -1){
            ((AdminActivity) getActivity()).goSettingFragment(currentUser);
        }
        else {
            SettingFragment settingFragment = new SettingFragment();
            settingFragment.setCurrUser(currentUser);
            ((DashboardActivity) getActivity()).replaceFragment(settingFragment);
        }
    }


    public void onNotificationPermissionResult(boolean granted) {
        if (!granted) {
            // User denied permission -> force switch OFF
            Switch switchPush = getView().findViewById(R.id.enableNotificationsSwitchBtn);
            switchPush.setChecked(false);

            Toast.makeText(getContext(),
                    "Permission denied — notifications cannot be enabled",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void setUser(User user) {this.currentUser = user;}
}
