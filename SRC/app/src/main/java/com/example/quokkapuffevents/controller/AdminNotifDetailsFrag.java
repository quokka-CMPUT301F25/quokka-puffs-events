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
    TextView sender;
    TextView recipient;
    TextView message;
    TextView status;
    TextView originEvent;
    TextView choice;
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
        displayInfo(view);
        setUpListeners(view);
        return view;

    }

    public void setNotif(Notif notif) {

        this.notif = notif;

    }

    public void initializeViews(View view) {

        notifName = view.findViewById(R.id.notifName);
        sender = view.findViewById(R.id.sender);
        recipient = view.findViewById(R.id.recipient);
        message = view.findViewById(R.id.messageText);
        status = view.findViewById(R.id.status);
        originEvent = view.findViewById(R.id.event);
        choice = view.findViewById(R.id.choice);
        goBackBtn = view.findViewById(R.id.goBackBtn);

        if (notif.getType() == 0) {
            choice.setVisibility(INVISIBLE);
            status.setVisibility(INVISIBLE);
        }

    }

    public void displayInfo(View view) {

        db.GetUser(notif.getOriginUser(), user -> {
            notifName.setText(user.getFirstName());
            sender.setText(user.getFirstName());
        });

        db.GetUser(notif.getRecipient(), user -> {
            recipient.setText(user.getFirstName());
        });

        db.GetEvent(notif.getOriginEvent(), event -> {
            originEvent.setText(event.getName());
        });
        System.out.println(notif.getMessage());
        //message.setText(notif.getMessage());

        if (notif.getType() == 1) {
            choice.setVisibility(VISIBLE);
            status.setVisibility(VISIBLE);


            if (notif.getChoice() == 0) {
                choice.setText("Recipient has not accepted the invitation");
            }
            else {
                choice.setText("Recipient has accepted the invitation");
            }

            if (notif.getChosen() == true) {
                status.setText("Recipient has been chosen");
            }
            else {
                status.setText("Recipient has not been chosen");
            }

        }

    }

    public void setUpListeners(View view) {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().popBackStack();

            }
        });

    }

}
