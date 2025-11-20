package com.example.quokkapuffevents.controller;


import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.EventListFragAdapter;

import java.util.ArrayList;
import java.util.Date;

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
    ListView listView;
    EventListFragAdapter adapter;
    LinearLayout pastEventsContainer;


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
        //DisplayPastEvents();
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
    public void DisplayPastEvents() {
        ArrayList<Event> finalEvents = new ArrayList<>();
        db.GetUser(db.GetCurrentUserID(), user -> {
            db.GetEventsFromUser(user, events -> {
                for (Event event : events) {
                    if (event.getEventDate().before(new Date())) {
                        finalEvents.add(event);
                    }
                }
                adapter = new EventListFragAdapter(requireContext(), finalEvents, "Past");
                listView.setAdapter(adapter);
                adapter.setEvents(finalEvents);
            });
        });
    }
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
        listView = v.findViewById(R.id.past_events_listview);
        pastEventsContainer = v.findViewById(R.id.pastEventsContainer);
    }

    public void displayInfo() {
        String userId = db.GetCurrentUserID();
        ArrayList<Event> finalEvents = new ArrayList<>();
        if (currUser != null) {
            usernameText.setText(currUser.getUserName());
            String formatName = currUser.getFirstName() + " " + currUser.getLastName();
            firstAndLastNameText.setText(formatName);
            emailText.setText(currUser.getEmail());
            userPhoneNumber.setText(currUser.getPhoneNumber());

            if (currUser.getAccountType() != -1){
                db.GetEventsFromUser(currUser, events -> {
                    for (Event event : events) {
                        if (event.getEventDate().before(new Date()) || (event.getDrawn() == true) || (event.getEventUsers().get(currUser.getId()) != "Waiting")) {
                            finalEvents.add(event);
                        }
                    }
                    adapter = new EventListFragAdapter(requireContext(), finalEvents, "Past");
                    listView.setAdapter(adapter);
                    adapter.setEvents(finalEvents);
                    adapter.setActivity((DashboardActivity) getActivity());
                });
            }
            else{
                pastEventsContainer.setVisibility(GONE);
            }

        } else {
            Log.e("Firestore", "User not found: " + userId);
        }

    }

    public void setUpListeners(View v) {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.SetUserID(null);
                if (currUser.getAccountType() == -1){
                    ((AdminActivity) getActivity()).goBackToLogin();
                }
                else {
                    ((DashboardActivity) getActivity()).goBackToLogin();
                }

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currUser.getAccountType() == -1){
                    ((AdminActivity) getActivity()).changeProfile(currUser);
                }
                else {
                    ChangeProfileSettings editFragment = new ChangeProfileSettings();
                    editFragment.setUser(currUser);
                    ((DashboardActivity) getActivity()).replaceFragment(editFragment);
                }
            }
        });

    }


}