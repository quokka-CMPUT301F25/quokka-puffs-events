package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.quokkapuffevents.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventCreateFragment extends Fragment {
    // DATABASE ATTRIBUTES
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
    Button cancelEvent; //button to cancel event
    Button createEvent; //button to initialize creating the event
    String userID; //current user id

    String maxParts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        db = Database.getInstance();
        userID = db.GetCurrentUserID();
        initializeViews(view);
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
        addImagesBtn = view.findViewById(R.id.addPhotosBtn);
        limitPar = view.findViewById(R.id.eventLimitParticipantsSwitch);
        maxPar = view.findViewById(R.id.eventMaxParticipantsInput);
        addGeo = view.findViewById(R.id.eventGeolocationSwitch);
        cancelEvent = view.findViewById(R.id.cancelEventCreationBtn);
        createEvent = view.findViewById(R.id.confirmEventCreationBtn);
        numbPar = view.findViewById(R.id.eventParticipantAmountInput);
    }

    public void setUpListeners(View view) {
        createEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


//               // Getting input values
                String title = eventTitle.getText().toString().trim();
                String desc = eventDesc.getText().toString().trim();
//
//                //TODO: change if we are adding a calendar widget
                //Format that user type has to be yyyy-mm-dd in order for DateConverter to work
//                String startDateString = startDateDraw.getText().toString().trim();
//                String endDateString = endDateDraw.getText().toString().trim();
                String eventDateString = dateOfEvent.getText().toString().trim();
                String drawDateString = drawDate.getText().toString().trim();
                int parts = Integer.parseInt(numbPar.getText().toString()); // number of wanted participants in event
                String maxParts = "";
//
                boolean limitParts = limitPar.isChecked();
                if (limitParts){
                    maxParts = maxPar.getText().toString().trim();
                }

//                boolean addGeolocate = addGeo.isChecked(); //TODO: IDK YET???
                  //Validating inputs
//                if (!validateInputs()) {
//                    return;
//                }

                //Creating Date Objects
                Date drawDate = dateConverter(drawDateString);
                Date eventDate = dateConverter(eventDateString);

                //Create event in database
//                createEventObject(userID, desc, parts, maxParts, new Date(), new Date(), new Date(), title);
                if (maxParts.isEmpty()){
                    db.CreateEvent(title, userID, desc, parts, drawDate, eventDate);
                } else {
                    int maxPar = Integer.parseInt(maxParts);
                    db.CreateEvent(title, userID, desc, parts, maxPar, drawDate, eventDate);
                }


                ((DashboardActivity) getActivity()).replaceFragment(new AdminEventFragment());
            }
            //TODO: navigate back to the DashboardActivity with EventListFragment (show updated event list)
        });

        cancelEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: navigate back to the DashboardActivity with EventListFragment
            }
        });
    }

    public Date dateConverter(String dateString){
        /**
         * Converts string input from user into a Date object for event creation
         * @param dateString
         * This is the trimmed input of the user in the date(s) editText
         * @return
         * Returns a Date object required for the event creation
         */
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // the try is here for error handling
        try {
            Date date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateInputs(){
        /**
         * Checks if user inputted into these specific required fields
         * @return
         * Returns a boolean value (false if one of the fields is blank, true if all fields are filled)
         */
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

    public void createEventObject(String id, String desc, int parts, String maxParts,
                                  Date startDate, Date endDate, Date eventDate, String title){
        /**
         * Method to create the event itself (easier for testing)
         * @param id
         * Current user ID using the app (automatic that they are an organizer)
         * @param desc
         * Description of event
         * @param parts
         * Number of Participants to be in event
         * @param maxParts
         * Max participants to join waiting list
         * (This is an OPTIONAL feature dealt with within this method)
         * @param startDate
         * Start of when entrants are allowed to join waiting list
         * @param endDate
         * End of when entrants are allowed to join waiting list
         * @param eventDate
         * Day of the event
         * @param title
         * Title of event
         */
        if (maxParts.isEmpty()){
            db.CreateEvent(title, id, desc, parts, endDate, eventDate);
        } else {
            int maxPar = Integer.parseInt(maxParts);
            db.CreateEvent(title, id, desc, parts, maxPar, endDate, eventDate);
        }
    }

}
