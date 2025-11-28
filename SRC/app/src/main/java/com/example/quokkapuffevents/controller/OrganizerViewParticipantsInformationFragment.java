package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrganizerViewParticipantsInformationFragment extends Fragment {

    private User user;
    private Event event;
    private Database db;

    private String status;

    private TextView username;
    private TextView firstname;
    private TextView lastname;
    private TextView email;
    private TextView phone;
    private Button backBtn;
    private Button cancelInviteBtn;


    /**
     * Sets the current user to view their information
     * @param u
     */
    public void SetUser(User u) {
        this.user = u;
    }

    /**
     * Sets the current user and their status to view their information
     * @param u
     * @param status
     */
    public void SetUser(User u, String status) {
        this.user = u;
        this.status = status;
    }

    /**
     * sets the current event
     * @param e
     */
    public void SetEvent(Event e) {
        this.event = e;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.organizer_view_participant_information, container, false);
        db = Database.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        setUpListeners();
    }

    public void initialize(View view) {
        /**
         * Initializes all UI components and references.
         *
         * @param view The root view of the fragment.
         */
        username = view.findViewById(R.id.usernameText);
        firstname = view.findViewById(R.id.userFirstNameText);
        lastname = view.findViewById(R.id.userLastNameText);
        email = view.findViewById(R.id.userEmailText);
        phone = view.findViewById(R.id.userContactInformation);
        backBtn = view.findViewById(R.id.goBackToDashboardBtn);
        cancelInviteBtn = view.findViewById(R.id.revokeInviteBtn);

        if(user != null) {
            username.setText(user.getUserName());
            firstname.setText(user.getFirstName());
            lastname.setText(user.getLastName());
            email.setText(user.getEmail());
            phone.setText(user.getPhoneNumber());
        }

    }

    public void setUpListeners() {
        /**
         * This method sets up the onclick listeners for the interactables on the fragment.
         * backBtn: goes back to the viewparticipantsfragment
         * cancelinviteBtn: Cancel's the users invited status. Changing their status to 'canceled'
         * #TODO: Find out how to change the information in the firebase and update it to show it in real time.
         * @param view
         */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrganizerViewParticipantsFragment orgFrag = new OrganizerViewParticipantsFragment();
                orgFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(orgFrag);

            }
        });

        cancelInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.CancelUserIntoEvent(event, user);  // TS DONT WORK

                OrganizerViewParticipantsFragment orgFrag = new OrganizerViewParticipantsFragment();
                orgFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(orgFrag);

            }
        });

    }

}
