package com.example.quokkapuffevents.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;

import java.util.ArrayList;


public class FilterSettingsFrag extends Fragment {

    LinearLayout selectInterests;
    LinearLayout selectAvailability;
    LinearLayout selectDistance;
    String[] templateInterests = {
            "Birthdays", "Weddings", "Concerts", "Lectures",
            "Tournaments", "Games", "Ceremonies",
            "Fundraisers", "Theater", "Party"
    };
    String[] templateAvailability = {
            "Any", "Open", "Full"
    };
    String[] templateDistances = {
            "50km", "100km", "200km", "500km", "1000km", "2000km"
    };
    ArrayList<String> selectedInterestArray;
    ArrayList<String> selectedAvailableArray;
    ArrayList<String> selectedDistanceArray;
    Button goBackbtn;
    Bundle bundle;

    public FilterSettingsFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bundle = new Bundle();

        View view = inflater.inflate(R.layout.filter_events_fragments, container, false);
        selectedInterestArray = getArguments().getStringArrayList("interests");
        selectedAvailableArray = getArguments().getStringArrayList("available");
        selectedDistanceArray = getArguments().getStringArrayList("distance");

        initializeViews(view);
        setUpListeners();

        return view;

    }

    public void initializeViews(View view) {

        selectInterests = view.findViewById(R.id.interestsLayoutButton);
        selectAvailability = view.findViewById(R.id.availabilityButton);
        selectDistance = view.findViewById(R.id.locationButton);
        goBackbtn = view.findViewById(R.id.goBackBtn);

    }

    public void setUpListeners() {

        selectInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                interestDropdown();

            }
        });

        selectAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                availableDropdown();

            }
        });

        selectDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                distanceDropdown();

            }
        });

        goBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundle.putStringArrayList("interests", selectedInterestArray);
                bundle.putStringArrayList("available", selectedAvailableArray);
                bundle.putStringArrayList("distance", selectedDistanceArray);

                getParentFragmentManager().setFragmentResult("filterRequest", bundle);

                getParentFragmentManager().popBackStack();

            }
        });

    }

    public void interestDropdown() {

        boolean[] checkedItems = new boolean[templateInterests.length];

        // Pre-check previously selected interests
        for (int i = 0; i < templateInterests.length; i++) {
            if (selectedInterestArray.contains(templateInterests[i])) {
                checkedItems[i] = true;
            }
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
        });

        builder.setPositiveButton("Add Interests", (dialog, which) -> {
            Toast.makeText(getActivity(),
                    "Selected: " + selectedInterestArray.toString(),
                    Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    public void availableDropdown() {
        // Determine the currently selected index (-1 if none)
        int checkedItemIndex = -1;
        for (int i = 0; i < templateAvailability.length; i++) {
            if (selectedAvailableArray.contains(templateAvailability[i])) {
                checkedItemIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Availability");

        // Single-choice items
        builder.setSingleChoiceItems(templateAvailability, checkedItemIndex, (dialog, which) -> {
            // When an item is clicked, clear previous selection and store new one
            selectedAvailableArray.clear();
            selectedAvailableArray.add(templateAvailability[which]);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Clear Selection", (dialog, which) -> {
            selectedAvailableArray.clear();
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (!selectedAvailableArray.isEmpty()) {
                Toast.makeText(getActivity(),
                        "Selected: " + selectedAvailableArray.get(0),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No selection made", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void distanceDropdown() {
        // Determine the currently selected index (-1 if none)
        int checkedItemIndex = -1;
        for (int i = 0; i < templateDistances.length; i++) {
            if (selectedDistanceArray.contains(templateDistances[i])) {
                checkedItemIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Availability");

        // Single-choice items
        builder.setSingleChoiceItems(templateDistances, checkedItemIndex, (dialog, which) -> {
            // When an item is clicked, clear previous selection and store new one
            selectedDistanceArray.clear();
            selectedDistanceArray.add(templateDistances[which]);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Clear Selection", (dialog, which) -> {
            selectedDistanceArray.clear();
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (!selectedDistanceArray.isEmpty()) {
                Toast.makeText(getActivity(),
                        "Selected: " + selectedDistanceArray.get(0),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No selection made", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

}
