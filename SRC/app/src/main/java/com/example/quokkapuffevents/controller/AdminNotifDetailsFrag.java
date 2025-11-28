package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.model.User;

import java.util.ArrayList;

public class AdminNotifDetailsFrag extends Fragment {

    private Database db;
    private Notif notif;

    TextView notifName;
    TextView organizer;
    TextView recipient;
    TextView message;
    Button goBackBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notif_details_fragment, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners();
        return view;

    }

    // Setter
    public void setNotif(Notif notif) {

        this.notif = notif;

    }

    /**
     * Finds each view and initializes each one
     * @param view The view that is accessed to find each view
     */
    public void initializeViews(View view) {

        notifName = view.findViewById(R.id.notifNameTextView);
        organizer = view.findViewById(R.id.organizerNameTextView);
        recipient = view.findViewById(R.id.receiverTextView);
        message = view.findViewById(R.id.messageTextView);
        goBackBtn = view.findViewById(R.id.goBackBtn);

    }

    /**
     * Using the initialized views set each one to display each detail of the notification
     */
    public void displayInfo( ) {

        db.GetUser(notif.getOriginUser(), user -> {
            organizer.setText(user.getUserName());
        });

        db.GetUser(notif.getRecipient(), user -> {
            recipient.setText(user.getUserName());
        });

        db.GetEvent(notif.getOriginEvent(), event -> {
            notifName.setText(event.getName());

        });
        message.setText(notif.getMessage());

    }

    /**
     * A back button to go back to all the notifications
     */
    public void setUpListeners() {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().popBackStack();

            }
        });

    }

}
