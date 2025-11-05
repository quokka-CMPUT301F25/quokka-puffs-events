package com.example.quokkapuffevents.controller;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.*;
import com.example.quokkapuffevents.view.EventListFragAdapter;

import java.util.ArrayList;
import java.util.Map;

public class SettingFragment extends Fragment {

    Database db;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_settings_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Database.getInstance();
        userID = String.valueOf(db.GetCurrentUserID());

        DisplayPastEvents(view);
    }

    public void DisplayUserInfo() {
        //Displays the info at the top
    }

    public void DisplayPastEvents(View view) {
        ListView pastEvents = view.findViewById(R.id.past_events_listview);
        ArrayList<Event> pastEventsList = new ArrayList();
        EventListFragAdapter adapter = new EventListFragAdapter(requireContext(), pastEventsList, "Past");
        pastEvents.setAdapter(adapter);

        db.GetUser(db.GetCurrentUserID(), user -> {
            db.GetEventsFromUser(user, adapter::setEvents);
        });
    }

    public void EditProfileClicked() {

    }
}
