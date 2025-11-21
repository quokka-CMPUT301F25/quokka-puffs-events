package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;

public class NotificationDetailsFragment extends Fragment {
    private Notif notification;
    private Database db;
    private TextView eventNameText;
    private TextView senderText;
    private TextView messageText;
    private Button backButton;
    private Button eventDetailsButton;

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_view_details, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners(view);
        return view;
    }

    private void initializeViews(View view) {
        eventNameText = view.findViewById(R.id.orgEventNameText);
        senderText = view.findViewById(R.id.orgNameText);
        messageText = view.findViewById(R.id.notifMessage);
        backButton = view.findViewById(R.id.goBackToDashboardBtn);
        eventDetailsButton = view.findViewById(R.id.viewEventDetails);
    }

    private void displayInfo() {
        db.GetUser(notification.getOriginUser(), user -> {
            senderText.setText(user.getUserName());
        });

        db.GetEvent(notification.getOriginEvent(), event -> {
            eventNameText.setText(event.getName());
        });

        messageText.setText(notification.getMessage());
    }

    private void setUpListeners(View view) {
        backButton.setOnClickListener(v ->{
            NotificationFragment notifFragment = new NotificationFragment();
            ((DashboardActivity) getActivity()).replaceFragment(notifFragment);
        });

        eventDetailsButton.setOnClickListener(v -> {
           db.GetEvent(notification.getOriginEvent(), event ->{
               EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
               entrantFrag.setEvent(event);
               ((DashboardActivity) getActivity()).replaceFragment(entrantFrag);
           });
        });
    }

    public void setNotification(Notif notification) {
        this.notification = notification;
    }

}
