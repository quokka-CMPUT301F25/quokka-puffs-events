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
import com.example.quokkapuffevents.model.*;
import com.example.quokkapuffevents.view.EventListFragAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private Database db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Database.getInstance();

        ListView waitingEvents = view.findViewById(R.id.waiting_events_listview);


        db.GetUser(db.GetCurrentUserID(), user -> {
            if (user.getAccountType() == 0) {
                db.FilteredEventsForUser(user, "Waiting", events -> {
                    EventListFragAdapter adapter = new EventListFragAdapter(requireContext(), events, "Waiting");
                            waitingEvents.setAdapter(adapter);
                            adapter.setEvents(events);
                            adapter.setActivity((DashboardActivity) getActivity());
                });
//                db.GetEventsFromUser(user, events -> {
//                            EventListFragAdapter adapter = new EventListFragAdapter(requireContext(), events, "Waiting");
//                            waitingEvents.setAdapter(adapter);
//                            adapter.setEvents(events);
//                        });
//                EventListFragAdapter adapter = new EventListFragAdapter(requireContext(), events, "Waiting");
//                waitingEvents.setAdapter(adapter);

            }
            else{
                db.GetEventsFromUser(user, eventsOrg -> {
                    EventListFragAdapter adapter = new EventListFragAdapter(requireContext(), eventsOrg, "Past");
                    waitingEvents.setAdapter(adapter);
                    adapter.setActivity((DashboardActivity) getActivity());
                });
            }
        });
    }
}
