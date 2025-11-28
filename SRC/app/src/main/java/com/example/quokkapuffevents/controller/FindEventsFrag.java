package com.example.quokkapuffevents.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
    Button informationButton;
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

        //selectedInterestArray = new ArrayList<>(Arrays.asList(templateInterests));
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
            // events = filterInterests(events, selectedInterestArray);
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }

    public void setUpListeners() {
//        selectInterests.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                interestDropdown();
//            }
//        });

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

        selectInterests = view.findViewById(R.id.interestsButton);
        informationButton = view.findViewById(R.id.infoBtn);
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
