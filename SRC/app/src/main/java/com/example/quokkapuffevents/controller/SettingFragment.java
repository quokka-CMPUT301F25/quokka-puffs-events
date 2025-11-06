package com.example.quokkapuffevents.controller;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;

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
     *
     * Additional: Provide visual updates to show changes:
     *
     *   TODO: Background of changed text or edited switch becomes orange.
     *
     * */

    DashboardActivity dashboardActivity;
    AdminActivity adminActivity;
    private Database db; // collection we want to access
    private User currUser;


    Button editProfile;
    Button signOut;
    TextView usernameText;
    TextView firstAndLastNameText;
    TextView emailText;
    TextView userPhoneNumber;


    //    Stole this from Seth -- HAHA SORRY! -Kyle.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_settings_fragment, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayInfo();
    }

    public void setCurrUser(User currUser) {this.currUser = currUser;}

    public void initializeViews(View v) {
        /*
          Initializes all attributes for the fragment
          @param v
         * View of the ChangeProfileSettings Fragment
         */

        signOut = v.findViewById(R.id.signOutBtn);
        editProfile = v.findViewById(R.id.editProfileBtn);
        usernameText = v.findViewById(R.id.usernameText);
        firstAndLastNameText = v.findViewById(R.id.userFirstAndLastNameText);
        emailText = v.findViewById(R.id.userEmailText);
        userPhoneNumber = v.findViewById(R.id.userPhoneNumber);
    }

    public void displayInfo() {
        String userId = db.GetCurrentUserID();
        if (currUser != null) {
            usernameText.setText(currUser.getUserName());
            String formatName = currUser.getFirstName() + " " + currUser.getLastName();
            firstAndLastNameText.setText(formatName);
            emailText.setText(currUser.getEmail());
            userPhoneNumber.setText(currUser.getPhoneNumber());
        } else {
            Log.e("Firestore", "User not found: " + userId);
        }

    }

    public void setUpListeners(View v) {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.SetUserID(null);
                ((DashboardActivity) getActivity()).goBackToLogin();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeProfileSettings editFragment = new ChangeProfileSettings();
                editFragment.setUser(currUser);
                ((DashboardActivity) getActivity()).replaceFragment(editFragment);
            }
        });

}

















}
