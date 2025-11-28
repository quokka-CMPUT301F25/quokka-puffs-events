package com.example.quokkapuffevents.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class FindEventsFrag extends Fragment {

    ListView listView;
    private AdminEventFragAdapter adapter;
    private Database db;
    private ArrayList<Event> eventList = new ArrayList<>();
    LinearLayout selectInterests;
    String[] templateInterests = {
            "Birthdays", "Weddings", "Concerts", "Lectures",
            "Tournaments", "Games", "Ceremonies",
            "Fundraisers", "Theater", "Party"
    };
    ArrayList<String> selectedInterestArray;
    boolean[] checkedItems;


    public FindEventsFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_events_fragment, container, false);

        db = Database.getInstance();

        initializeViews(view);
        setUpListeners();

        // Create an adapter for the events
        adapter = new AdminEventFragAdapter(getContext(), eventList);
        listView.setAdapter(adapter);

        selectedInterestArray = new ArrayList<>(Arrays.asList(templateInterests));
        checkedItems = new boolean[templateInterests.length];
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = true;
        }

        updateAdapter();

        return view;
    }

    public void updateAdapter() {
        // Add all the events to the adapter
        db.ListEvents( events -> {
            // refresh adapter
            eventList.clear();
            events = filterInterests(events, selectedInterestArray);
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }

    public void setUpListeners() {
        selectInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                interestDropdown();

            }
        });



    }

    public void initializeViews(View view) {

        selectInterests = view.findViewById(R.id.interestsButton);
        System.out.println(selectInterests);
        listView = view.findViewById(R.id.findEventsListView);

    }

    public ArrayList<Event> filterInterests(ArrayList<Event> arrayList, ArrayList<String> selected) {
        ArrayList<Event> filteredEvents = new ArrayList<>();

        for (Event event : arrayList) {
            for (String interest : selected) {
                if (event.getInterests() != null) {
                    if (event.getInterests().contains(interest)) {
                        filteredEvents.add(event);
                    }
                }
            }
        }

        return filteredEvents;
    }

    public void interestDropdown() {

        // Pre-check previously selected interests
        for (int i = 0; i < templateInterests.length; i++) {
            checkedItems[i] = selectedInterestArray.contains(templateInterests[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select interests");

        builder.setMultiChoiceItems(templateInterests, checkedItems,
                (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!selectedInterestArray.contains(templateInterests[which])) {
                            selectedInterestArray.add(templateInterests[which]);
                        }
                    } else {
                        selectedInterestArray.remove(templateInterests[which]);
                    }
                });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            selectedInterestArray.clear();
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
            }
            updateAdapter();
        });

        builder.setPositiveButton("Add Interests", (dialog, which) -> {
            Toast.makeText(getActivity(),
                    "Selected: " + selectedInterestArray.toString(),
                    Toast.LENGTH_SHORT).show();
            updateAdapter();
        });

        builder.show();
    }


}
