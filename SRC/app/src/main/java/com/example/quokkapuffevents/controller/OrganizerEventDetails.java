package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;

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

import java.util.Date;

public class OrganizerEventDetails extends Fragment {

    Database db;
    Event event;

    Button runLottoButton;
    Button viewParticipantsButton;
    Button changeDetailsButton;

    Button exitButton;

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
        SetUpListeners(view);
    }

    private void initialize(View view) {
        db = Database.getInstance();

        //Button Shit
        runLottoButton = view.findViewById(R.id.orgRunLotteryBtn);
        viewParticipantsButton = view.findViewById(R.id.orgViewParticipantsBtn);
        changeDetailsButton = view.findViewById(R.id.orgChangeDetailsBtn);
        exitButton = view.findViewById(R.id.orgExitOutEventBtn);

        //Removing button if after end of event
        if (event.getEventDate().after(new Date())){
            runLottoButton.setOnClickListener(v -> {
                event.drawUsers(-1);
                event.setDrawn(true);
                db.SaveEvent(event);
            });
        }
        else {
            runLottoButton.setVisibility(INVISIBLE);
        }
    }

    public void SetUpListeners(View view) {



//        Goes back to the home view.
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeFragment newFrag = new HomeFragment();
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

//        This goes to the event details for organizer to change details.

        changeDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                EditEventFragment newFrag = new EditEventFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });



    }

    public void SetEvent(Event event) {this.event = event; }

    public static class EntrantEventDetailsFragment extends Fragment {
    }
}


