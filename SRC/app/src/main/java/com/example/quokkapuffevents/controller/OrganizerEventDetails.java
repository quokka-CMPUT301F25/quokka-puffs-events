package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;

public class OrganizerEventDetails extends Fragment {

    Database db;
    Event event;

    Button drawUsers;
    Button viewParticipants;
    Button changeDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout."NAME", container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();

        drawUsers.setOnClickListener(v -> {
            event.drawUsers(event.getToBeDrawn());
            event.setDrawn(true);
        });
    }

    private void initialize() {
        db = Database.getInstance();

        //Button Shit
    }

    public void SetEvent(Event event) {this.event = event; }
}


