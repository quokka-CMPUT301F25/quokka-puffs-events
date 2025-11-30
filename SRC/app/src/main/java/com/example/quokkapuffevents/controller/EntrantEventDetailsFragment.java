package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class EntrantEventDetailsFragment extends Fragment implements OnMapReadyCallback {
//    Set up variables
    private Database db;
    private Event event;
    private GoogleMap mMap;
    TextView orgEventNameText;
    ImageView eventImage;
    TextView eventTotalParticiapntsWaitingText;
    TextView eventDrawDateText;
    TextView eventDrawn;
    TextView eventFinished;
    TextView eventDescriptionText;
    Button entrantRegisterForEventBtn;
    Button goBackToDashboardBtn;
    int waitingParticipants = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_view_event_fragment, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners();
        checkAdmin();

        Log.d("EVENT_LAT", "Lat: " + event.getLat());
        Log.d("EVENT_LNG", "Lng: " + event.getLng());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // THIS TRIGGERS onMapReady()
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        LatLng location = new LatLng(event.getLat(), event.getLng());

        googleMap.addMarker(new MarkerOptions().position(location).title(event.getName()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

    }

    public void setEvent(Event event) {
        /**
         * Sets current event
         * @param event
         */
        this.event = event;

    }

    public void initializeViews(View view) {
        /**
         * initializes all buttons/views/edits in fragment
         */
        orgEventNameText = view.findViewById(R.id.orgEventNameText);
        eventImage = view.findViewById(R.id.eventImageView);
        eventTotalParticiapntsWaitingText = view.findViewById(R.id.eventTotalParticipantsWaitingText);
        eventDrawDateText = view.findViewById(R.id.eventDrawDateText);
        eventDrawn = view.findViewById(R.id.eventDrawn);
        eventFinished = view.findViewById(R.id.eventFinished);
        eventDescriptionText = view.findViewById(R.id.eventDescriptionText);
        entrantRegisterForEventBtn = view.findViewById(R.id.entrantRegisterForEventBtn);
        goBackToDashboardBtn = view.findViewById(R.id.goBackToDashboardBtn);

    }

    public void displayInfo() {
        /**
         * displays all information to entrant in fragment
         */
//        Get values from event object
        String eventDescription = event.getDescription();
        String eventName = event.getName();
        Date eventDrawDateObj = event.getDrawnDate();
        Map<String, String> participants = event.getEventUsers();

        participants.forEach((user, status) -> {
            if(status.equals("Waitlist")) {
                waitingParticipants++;
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String eventDrawnDate = formatter.format(eventDrawDateObj);

//        Display info to fragment
        orgEventNameText.setText(eventName);

        db.GetImage(event.getImageID(), bitmap -> {
            eventImage.setImageBitmap(bitmap);
        });
        eventTotalParticiapntsWaitingText.setText(Integer.toString(event.getNumPeopleWaiting()));
        eventDescriptionText.setText(eventDescription);
        eventDrawDateText.setText(eventDrawnDate);
        eventDrawn.setText(event.getDrawn().toString());
        eventFinished.setText(String.valueOf(event.getFinished()));

        //Removing button if after end or full
        if (event.getEventDate().before(new Date())){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }
        if ((event.getNumPeopleWaiting() != -1) && (event.getNumPeopleWaiting() >= event.getMaxNumWaitlist())){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }
        if ((event.getMaxNumWaitlist() != -1) && (event.getNumPeopleWaiting() >= event.getMaxNumWaitlist())){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }
        if (event.getEventUsers().get(db.GetCurrentUserID()) != null){
            if (!Objects.equals(event.getEventUsers().get(db.GetCurrentUserID()), "Cancelled")){
                entrantRegisterForEventBtn.setVisibility(INVISIBLE);
            }
        }
        if (event.getFinished() == true){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }
        if ((event.getMaxNumWaitlist() != -1) && (event.getNumPeopleWaiting() >= event.getMaxNumWaitlist())){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }
        if (event.getEventUsers().get(db.GetCurrentUserID()) != null){
            if (!Objects.equals(event.getEventUsers().get(db.GetCurrentUserID()), "Cancelled")){
                entrantRegisterForEventBtn.setVisibility(INVISIBLE);
            }
        }
        if (event.getFinished() == true){
            entrantRegisterForEventBtn.setVisibility(INVISIBLE);
        }

    }

    public void setUpListeners() {
        /**
         * Adds functionality to buttons in fragment
         */
        goBackToDashboardBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();   // <- Correct Way
        });


        entrantRegisterForEventBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Allows entrant to register for event and takes them back to register events fragment
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                db.GetUser(db.GetCurrentUserID(), user -> {
                    db.RegisterUserIntoEvent(event, user);
                });

                CharSequence message = "You have been added to the waiting list.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getContext(), message, duration);
                toast.show();

                RegisterEventsFragment newFrag = new RegisterEventsFragment();
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);


            }
        });

    }

    public void checkAdmin() {
        /**
         * Checks whether user is an admin and sets up respectful button
         */
        db.GetUser(db.GetCurrentUserID(), user -> {
            if (user.getAccountType() == -1) {
                entrantRegisterForEventBtn.setVisibility(INVISIBLE);
                goBackToDashboardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        });
    }

    public void saveFilters() {

    }

}

