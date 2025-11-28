package com.example.quokkapuffevents.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;

import java.util.ArrayList;


public class FilterSettingsFrag extends Fragment {

    LinearLayout selectInterests;
    String[] templateInterests = {
            "Birthdays", "Weddings", "Concerts", "Lectures",
            "Tournaments", "Games", "Ceremonies",
            "Fundraisers", "Theater", "Party"
    };
    ArrayList<String> selectedInterestArray;

    public FilterSettingsFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_event_fragment, container, false);
        initializeViews(view);
        //displayInfo();
        setUpListeners();
        return view;

    }

    public void initializeViews(View view) {

        selectInterests = view.findViewById(R.id.interestsButton);

    }

    public void setUpListeners() {
        selectInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addingInterests();

            }
        });

    }

    public void addingInterests() {

        boolean[] checkedItems = new boolean[templateInterests.length];

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
        });

        builder.setPositiveButton("Add Interests", (dialog, which) -> {
            Toast.makeText(getActivity(),
                    "Selected: " + selectedInterestArray.toString(),
                    Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

}
