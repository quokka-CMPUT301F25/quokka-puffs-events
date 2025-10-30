package com.example.quokkapuffevents.controller;

import android.os.Bundle;
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

public class SettingFragment extends Fragment {

    /*
    * Main Purpose: Change user settings and profile
    *
    * User can change:
    *   Notifications
    *   Email
    *   Name
    *   Phone Number // Optional
    *   Delete Account
    * */

    private Database db; // collection we want to access
    private User currentUser; //

    /* Editable Text Inputs*/
    EditText email;
    EditText contact;
    EditText name;

    /* Buttons / Interactibles */

    Switch allowNotifs;
    Button revertBtn;
    Button confirmBtn;

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
        setUpListeners(view);
        return view;
    }
    public void initializeViews(View v) {
        /**
         * Initializes all attributes for the fragment
         * @param v
         * View of the ChangeProfileSettings Fragment
         */

        email = v.findViewById(R.id.userEmailTextInput);
        name = v.findViewById(R.id.userFirstAndLastNameTextInput);
        contact = v.findViewById(R.id.userContactInformationInput);

        allowNotifs = v.findViewById(R.id.enableNotificationsSwitchBtn);
        revertBtn = v.findViewById(R.id.revertChangesBtn);
        confirmBtn = v.findViewById(R.id.confirmChangesBtn);


    }

















}
