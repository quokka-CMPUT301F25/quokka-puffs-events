package com.example.quokkapuffevents.controller;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.*;
import com.example.quokkapuffevents.view.EventListFragAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Database.getInstance();
        userID = String.valueOf(db.GetCurrentUserID());

        listView = view.findViewById(R.id.past_events_listview);

        DisplayPastEvents(view);
    }

    public void DisplayUserInfo() {
        //Displays the info at the top
    }

    public void DisplayPastEvents(View view) {
        ArrayList<Event> finalEvents = new ArrayList<>();


        db.GetUser(userID, user -> {
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

    public void EditProfileClicked() {

    }
}
