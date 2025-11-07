package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.util.Log;
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

    Button runLottoButton;
    Button viewParticipantsButton;
    Button changeDetailsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_event_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    private void initialize(View view) {
        db = Database.getInstance();

        //Button Shit
        runLottoButton = view.findViewById(R.id.orgRunLotteryBtn);
        viewParticipantsButton = view.findViewById(R.id.orgViewParticipantsBtn);
        changeDetailsButton = view.findViewById(R.id.orgChangeDetailsBtn);

        runLottoButton.setOnClickListener(v -> {
            event.drawUsers(-1);
            event.setDrawn(true);
            db.SaveEvent(event);
        });
    }

    public void SetEvent(Event event) {this.event = event; }

    public static class EntrantEventDetailsFragment extends Fragment {
    }
}


