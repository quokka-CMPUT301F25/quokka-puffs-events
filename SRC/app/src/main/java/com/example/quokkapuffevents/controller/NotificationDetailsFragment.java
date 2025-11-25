package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.util.Log;
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
    private TextView notifTitleText;
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

    /**
     * Initializes all the views in the fragment, allows for edits in other functions
     * @param view The current view, needed to allow access to the UI elements
     */
    private void initializeViews(View view) {
        eventNameText = view.findViewById(R.id.orgEventNameText);
        notifTitleText = view.findViewById(R.id.notificationTitle);
        senderText = view.findViewById(R.id.orgNameText);
        messageText = view.findViewById(R.id.notifMessage);
        backButton = view.findViewById(R.id.goBackToDashboardBtn);
        eventDetailsButton = view.findViewById(R.id.viewEventDetails);
    }


    /**
     * Displays the notification details in the fragment i.e. The title, the event it came from,
     * the sender and the message attached
     */
    private void displayInfo() {
        notifTitleText.setText(notification.getTitle());
        db.GetUser(notification.getOriginUser(), user -> {
            senderText.setText(user.getUserName());
        });

        db.GetEvent(notification.getOriginEvent(), event -> {
            eventNameText.setText(event.getName());
        });

        messageText.setText(notification.getMessage());
    }

    /**
     * Sets up the button listeners. The back button will return to the notification fragment,
     * The event details button will open the details of the event from which the message was sent from
     * @param view The current view, needed to allow button logic to work
     */
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
