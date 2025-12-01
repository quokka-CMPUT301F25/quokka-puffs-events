package com.example.quokkapuffevents.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.example.quokkapuffevents.view.EventListFragAdapter;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class FindEventsFrag extends Fragment {

    ListView listView;
    private EventListFragAdapter adapter;
    private Database db;
    private ArrayList<Event> eventList = new ArrayList<>();
    Button selectInterests;
    Button informationButton;
    String[] templateInterests = {
            "Birthdays", "Weddings", "Concerts", "Lectures",
            "Tournaments", "Games", "Ceremonies",
            "Fundraisers", "Theater", "Party"
    };
    String[] templateAvailability = {
            "Open", "Full", "Any"
    };
    String[] templateDistances = {
            "50km", "100km", "200km", "500km", "1000km", "2000km"
    };
    ArrayList<String> selectedInterestArray;
    ArrayList<String> selectedAvailableArray;
    ArrayList<String> selectedDistanceArray;
    Bundle bundle;

    boolean[] checkedItems;

    DashboardActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_events_fragment, container, false);

        db = Database.getInstance();

        bundle = new Bundle();

        initializeViews(view);

// Receive filter results from FilterSettingsFrag
        getParentFragmentManager().setFragmentResultListener("filterRequest", this,
                (requestKey, result) -> {

                    selectedInterestArray = result.getStringArrayList("interests");
                    selectedAvailableArray = result.getStringArrayList("available");
                    selectedDistanceArray = result.getStringArrayList("distance");

                    updateFilterAdapter(); // refresh events
                });

        if (getArguments() == null) {
            selectedInterestArray = new ArrayList<>(Arrays.asList(templateInterests));
            selectedAvailableArray = new ArrayList<>(Arrays.asList(templateAvailability));
            selectedDistanceArray = new ArrayList<>(Arrays.asList(templateDistances));
        }

        adapter = new EventListFragAdapter(requireContext(), eventList, "all");
        listView.setAdapter(adapter);
        adapter.setActivity((DashboardActivity) getActivity());

        setUpListeners();

        // Create an adapter for the events
        adapter = new EventListFragAdapter(getContext(), eventList, "all", activity);
        listView.setAdapter(adapter);

        //selectedInterestArray = new ArrayList<>(Arrays.asList(templateInterests));
        checkedItems = new boolean[templateInterests.length];
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = true;
        }

        updateAdapter();

        return view;
    }

    public void updateFilterAdapter() {
        // Add all the events to the adapter
        db.ListEvents( events -> {
            // refresh adapter
            eventList.clear();
            events = filterInterests(events);
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }

    public void updateAdapter() {
        db.ListEvents(events -> {
            eventList.clear();
            eventList.addAll(events);
            adapter.notifyDataSetChanged();  // OK!
        });
    }


    public void setUpListeners() {
        selectInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle.putStringArrayList("interests", selectedInterestArray);
                bundle.putStringArrayList("available", selectedAvailableArray);
                bundle.putStringArrayList("distance", selectedDistanceArray);

                FilterSettingsFrag filterFrag = new FilterSettingsFrag();
                filterFrag.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, filterFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Registration Criterion/Guidelines");

                // TODO: Finish the dialogue pop up text box
                builder.setMessage("This event uses a lottery-based registration system to ensure " +
                        "fair access for all entrants. Joining the waiting list DOES NOT guarantee " +
                        "a spot, but it gives you an equal chance during the selection.\n" +
                        "\n" +
                        "1. Join the Waiting List\n" +
                        "• You may join or leave the waiting list at any time while registration " +
                        "is open.\n" +
                        "• Some events may limit the maximum number of entrants allowed on the list.\n" +
                        "\n" +
                        "2. Random Selection (Lottery Draw)\n" +
                        "• When registration closes, the system randomly selects participants based " +
                        "on event capacity.\n" +
                        "• Selection does not depend on when you joined, everyone on the list has " +
                        "an equal chance.\n" +
                        "\n" +
                        "3. Notifications\n" +
                        "• If selected, you will receive an invitation to accept or decline your spot.\n" +
                        "• If not selected, you will receive a notification letting you know.\n" +
                        "\n" +
                        "4. Accepting or Declining\n" +
                        "• Selected entrants must confirm their spot before the deadline.\n" +
                        "• If a selected entrant declines or fails to respond, a replacement will " +
                        "be randomly drawn from the remaining waiting list.\n" +
                        "\n" +
                        "5. Additional Information\n" +
                        "• You can view event details, including registration dates, location, " +
                        "and poster, through the event page or by scanning the event’s QR code.\n" +
                        "• Your event history (selected, not selected, or declined) will be saved " +
                        "in your profile.\n" +
                        "• Organizers may send notifications or updates related to the event; you " +
                        "can opt out in your settings.\n" +
                        "• Some events may require optional geolocation verification to show where " +
                        "entrants are joining from.\n" +
                        "\n" +
                        "This system is designed to promote fairness, accessibility, and equal " +
                        "opportunity for all participants.");

                builder.setNegativeButton("Done", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

                // Change negative button text color
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }
        });

    }

    public void initializeViews(View view) {

        Log.d("WURT", "WURT2");
        //selectInterests = view.findViewById(R.id.temp_id);
        System.out.println(selectInterests);
        selectInterests = view.findViewById(R.id.filterEventsBtn);
        informationButton = view.findViewById(R.id.infoBtn);
        listView = view.findViewById(R.id.findEventsListView);
    }

    public ArrayList<Event> filterInterests(ArrayList<Event> arrayList) {
        ArrayList<Event> filteredEvents = new ArrayList<>();

        for (Event event : arrayList) {

            boolean matchesInterest = false;
            boolean matchesAvailability = false;
            boolean matchesDistance = true;

            // 1. Interests
            if (event.getInterests() != null) {
                for (String interest : selectedInterestArray) {
                    if (event.getInterests().contains(interest)) {
                        matchesInterest = true;
                        break;
                    }
                }
            }

            // 2. Availability
            if (selectedAvailableArray.get(0).equals("Full")) {
                matchesAvailability = (event.getEventUsers().size() == event.getMaxNumWaitlist()
                || -1 == event.getMaxNumWaitlist());
            } else if (selectedAvailableArray.get(0).equals("Open")) {
                matchesAvailability = event.getEventUsers().size() < event.getMaxNumWaitlist()
                || -1 == event.getMaxNumWaitlist();
            } else {
                matchesAvailability = true; // "Any"
            }

            // 3. Distance
//            if (event.getDistance() != null) {
//                for (String dist : selectedDistanceArray) {
//                    if (event.getDistance().equals(dist)) {
//                        matchesDistance = true;
//                        break;
//                    }
//                }
//            }

            // Add event only if it matches ANY selected filter
            if (matchesInterest) {
                System.out.println("Ture interest");
            }
            if (matchesAvailability) {
                System.out.println("Ture available");
            }
            if (matchesInterest && matchesAvailability && matchesDistance) {

                filteredEvents.add(event);

            }
        }

        return filteredEvents;
    }


    public void setActivity(DashboardActivity activity) {this.activity = activity;}
}
