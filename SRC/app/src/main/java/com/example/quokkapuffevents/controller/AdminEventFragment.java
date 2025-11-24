package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.model.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminEventFragment extends Fragment {

    ListView listView;
    private AdminEventFragAdapter adapter;
    private Database db;
    private ArrayList<Event> eventList = new ArrayList<>();

    public AdminEventFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View eventFragmentView = inflater.inflate(R.layout.frag_admin_events, container, false);

        db = Database.getInstance();

        listView = eventFragmentView.findViewById(R.id.adminEventsListView);

        // Create an adapter for the events
        adapter = new AdminEventFragAdapter(getContext(), eventList);
        listView.setAdapter(adapter);

        // Add all the events to the adapter
        db.ListEvents( events -> {
            // refresh adapter
            eventList.clear();
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        });



        return eventFragmentView;
    }
}
