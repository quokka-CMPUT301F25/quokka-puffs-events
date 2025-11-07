package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.view.EventListFragAdapter;
import com.example.quokkapuffevents.model.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterEventsFragment extends Fragment {
    // FILTER/REGISTER FOR EVENTS FOR DashboardActivity
    String userID; //current user id
    private Database db;

    private ListView listView;
    private EventListFragAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find_events_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
        db = Database.getInstance();
        userID = db.GetCurrentUserID();
        LoadEvent();
    }

    private void initializeUI(@NonNull View view) {
        listView = view.findViewById(R.id.findEventsListView);
    }

    private void LoadEvent(){
        db.ListEvents(events -> {
            ArrayList<Event> finalEvents = new ArrayList<>();
            db.GetUser(userID, user -> {
                ArrayList<String> userEvents = user.getEvents();
                for (Event event : events) {
                    if (!userEvents.contains(event.getId())) {
                        finalEvents.add(event);
                    }
                }
                adapter = new EventListFragAdapter(requireContext(), finalEvents, "all");
                listView.setAdapter(adapter);
                adapter.setEvents(finalEvents);
            });

        });
    }

}
