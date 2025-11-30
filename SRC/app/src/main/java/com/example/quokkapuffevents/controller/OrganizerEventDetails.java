package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class OrganizerEventDetails extends Fragment {

    Database db;
    Event event;
    Button runLottoButton;
    Button finishEventButton;
    Button viewParticipantsButton;
    Button changeDetailsButton;
    ImageView qrcodeView;
    Button exitButton;
    Button sendMessageButton;
    TextView description;

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
        /**
         * Initializes UI components and business logic for the fragment.
         * This includes displaying the QR code and enabling/disabling the
         * lottery button depending on the event status.
         *
         * @param view The view from which UI components are retrieved
         */
        db = Database.getInstance();
        description = view.findViewById(R.id.eventDescriptionText);
        description.setText(event.getDescription());

        //Button

        runLottoButton = view.findViewById(R.id.orgRunLotteryBtn);
        finishEventButton = view.findViewById(R.id.orgFinishEvent);
        viewParticipantsButton = view.findViewById(R.id.orgViewParticipantsBtn);
        changeDetailsButton = view.findViewById(R.id.orgChangeDetailsBtn);
        sendMessageButton = view.findViewById(R.id.orgSendMessageBtn);
        exitButton = view.findViewById(R.id.orgExitOutEventBtn);
        qrcodeView = view.findViewById((R.id.qrCode));

        //Removing button if after end of event
        if ((event.getEventDate().after(new Date())) && (event.getDrawn() == false)){
            runLottoButton.setOnClickListener(v -> {
                db.DrawUsers(event);
                runLottoButton.setVisibility(INVISIBLE);

            });
        }
        else {
            runLottoButton.setVisibility(INVISIBLE);
            if (event.getFinished() == false){
                finishEventButton.setVisibility(VISIBLE);
                finishEventButton.setOnClickListener(v -> {
                    db.FinishEvent(event);
                    finishEventButton.setVisibility(INVISIBLE);
                });
            }
        }

        //QRCode
        db.GetImage(event.getQrcodeID(), bitmap -> {
            if (bitmap != null) {
                qrcodeView.setImageBitmap(bitmap);
            } else {
                Log.e("IMAGES", "Bitmap from GetImage is null");
            }
        });
    }

    public void SetUpListeners(View view) {
        /**
         * Sets up button listeners for navigation and event management options.
         *
         * @param view The view from which buttons are retrieved
         */
        exitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer goes back to home view
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                HomeFragment newFrag = new HomeFragment();
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

        changeDetailsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer goes to change event details
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                EditEventFragment newFrag = new EditEventFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

        viewParticipantsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer can view participants in event
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                OrganizerViewParticipantsFragment newFrag = new OrganizerViewParticipantsFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer can sent message to entrants
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                SendMessageFragment newFrag = new SendMessageFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

    }

    /**
     * Basic getter of getting the current event
     * @param event
     */
    public void SetEvent(Event event) {this.event = event; }
}


