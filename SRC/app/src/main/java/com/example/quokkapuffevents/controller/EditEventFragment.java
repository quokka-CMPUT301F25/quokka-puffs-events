package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditEventFragment extends Fragment {

    private Event event;
    private Database db;

    // EVENT INFORMATION
    EditText eventTitle; //title of event
    EditText drawDate; //not in views yet, start of registration period
    EditText dateOfEvent; // date of event
    EditText eventDesc; //description of event
    Button addImagesBtn; //TODO: How will we implement images for events and add to database
    Switch limitPar;
    //the switch in XML file that determines whether organizer would like to limit the numb of participants
    EditText numbPar; //not in views yet, number of participants to be chosen
    EditText maxPar; // max number of participants to join waiting list
    Switch addGeo; //TODO: idek???
    Button cancelChangesBtn; //button to cancel event
    Button saveEventEditsBtn; //button to initialize creating the event
    String userID; //current user id

    String maxParts;
    public void SetEvent(Event event) {
        this.event = event;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_event_organizer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        db = Database.getInstance();
        userID = db.GetCurrentUserID();
        initializeViews(view);
        initalizeValues();
        setUpListeners(view);
    }

    public void initializeViews(View view) {
        /**
         * Initializes all attributes for the fragment
         * @param view
         * View of the EventCreateFragment
         */
        eventTitle = view.findViewById(R.id.eventTitleInput);
        dateOfEvent = view.findViewById(R.id.eventDateInput);
        drawDate = view.findViewById(R.id.drawDateInput);
        eventDesc = view.findViewById(R.id.eventDescInput);
        //addImagesBtn = view.findViewById(R.id.addPhotosBtn);
        limitPar = view.findViewById(R.id.eventLimitParticipantsSwitch);
        maxPar = view.findViewById(R.id.eventMaxParticipantsInput);
        addGeo = view.findViewById(R.id.eventGeolocationSwitch);
        cancelChangesBtn = view.findViewById(R.id.cancelEventCreationBtn);
        saveEventEditsBtn = view.findViewById(R.id.confirmEventEditBtn);
        numbPar = view.findViewById(R.id.eventParticipantAmountInput);
    }

    public void initalizeValues() {
        /**
         * Sets all values for the fragment
         * @param view
         * View of the EditEventFragment
         */

        if(event != null) {


            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String eventEventDate = formatter.format(event.getEventDate());
            String eventDrawDate = formatter.format(event.getDrawnDate());


            eventTitle.setText(event.getName());
            dateOfEvent.setText(eventEventDate);
            drawDate.setText(eventDrawDate);
            numbPar.setText(String.valueOf(event.getToBeDrawn()));
            eventDesc.setText(event.getDescription());

            if(event.getMaxNumWaitlist() != -1) {
                limitPar.setChecked(true);
                maxPar.setText(String.valueOf(event.getMaxNumWaitlist()));
            } else {
                limitPar.setChecked(false);
            }

        } else {
            throw new RuntimeException("EVENT DOES NOT EXIST"); // lowkey should never throw anyways.
        }
    }

    public void setUpListeners(View view) {
        /**
         * Creates Listeners for all buttons for the fragment
         * @param view
         * View of the EventCreateFragment
         */

        saveEventEditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    saveEventChanges();
                    db.SaveEvent(event);
                } else {
//                    Todo: Throw a toast error
                }
            }

        });

        cancelChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment newFrag = new HomeFragment();
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

    }

    public boolean validateInput() {

        Map<EditText, String> requiredFields = new HashMap<>();
        requiredFields.put(eventTitle, "Event Title Is Required");
        requiredFields.put(eventDesc, "Event Description Is Required");
        requiredFields.put(drawDate, "Draw Date Is Required");
        requiredFields.put(dateOfEvent, "Event Date Is Required");

        for (Map.Entry<EditText, String> entry : requiredFields.entrySet()) {
            if (entry.getKey().getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), entry.getValue(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;

    }

    public void saveEventChanges() {



//               // Getting input values
        String title = eventTitle.getText().toString().trim();
        String desc = eventDesc.getText().toString().trim();
        String eventDateString = dateOfEvent.getText().toString().trim();
        String drawDateString = drawDate.getText().toString().trim();
        int parts = Integer.parseInt(numbPar.getText().toString()); // number of wanted participants in event
        String maxParts = "";
//
        boolean limitParts = limitPar.isChecked();
        if (limitParts){
            maxParts = maxPar.getText().toString().trim();
        }

        //Creating Date Objects
//        Date drawDate = dateConverter(drawDateString);
//        Date eventDate = dateConverter(eventDateString);


//        event.Set
    }



}
