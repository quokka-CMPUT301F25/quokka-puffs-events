package com.example.quokkapuffevents.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Bitmap selectedImageBitmap;

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
        db = Database.getInstance();
        userID = db.GetCurrentUserID();
        registerImagePickerLauncher();
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
        addImagesBtn = view.findViewById(R.id.eventAddImagesBtn);
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
            throw new RuntimeException("EVENT DOES NOT EXIST");
        }
    }

    public void setUpListeners(View view) {
        /**
         * Creates Listeners for all buttons for the fragment
         * @param view
         * View of the EventCreateFragment
         */

        addImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerLauncher.launch(Intent.createChooser(intent, "Select Event Poster Image"));
            }
        });

        saveEventEditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    saveEventChanges();
                } else {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
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

    public void registerImagePickerLauncher() {
        /**
         * Registers the activity result launcher for image picking
         */
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            handleImageSelection(imageUri);
                        }
                    }
                }
        );
    }

    public void handleImageSelection(Uri imageUri) {
        /**
         * Handles the selected image and converts it to Bitmap
         * @param imageUri
         * URI of the selected image
         */
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            selectedImageBitmap = bitmap;
            Toast.makeText(requireContext(), "Image selected successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("ImageHandling", "Error converting URI to Bitmap", e);
            Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    public Date dateConverter(String dateString){
        /**
         * Converts string input from user into a Date object for event creation
         * @param dateString
         * This is the trimmed input of the user in the date(s) editText
         * @return
         * Returns a Date object required for the event creation
         */
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        // the try is here for error handling
        try {
            Date date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveEventChanges() {
        // Getting input values
        String title = eventTitle.getText().toString().trim();
        String desc = eventDesc.getText().toString().trim();
        String eventDateString = dateOfEvent.getText().toString().trim();
        String drawDateString = drawDate.getText().toString().trim();
        int parts = Integer.parseInt(numbPar.getText().toString()); // number of wanted participants in event

        boolean limitParts = limitPar.isChecked();
        int maxWaitlist = -1;
        if (limitParts && !maxPar.getText().toString().trim().isEmpty()) {
            maxWaitlist = Integer.parseInt(maxPar.getText().toString().trim());
        }

        // Creating Date Objects
        Date drawDateObj = dateConverter(drawDateString);
        Date eventDateObj = dateConverter(eventDateString);

        // Update event properties
        event.setName(title);
        event.setDescription(desc);
        event.setToBeDrawn(parts);
        event.setMaxNumWaitlist(maxWaitlist);
        if (drawDateObj != null) {
            event.setDrawnDate(drawDateObj);
        }
        if (eventDateObj != null) {
            event.setEventDate(eventDateObj);
        }

        if (selectedImageBitmap != null) {
            db.UploadImageToDatabase(selectedImageBitmap, new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String imageUri) {
                    // Delete old image if it exists
                    if (event.getImageID() != null) {
                        db.DeleteImage(event.getImageID());
                    }
                    event.setImageID(imageUri);
                    db.SaveEvent(event);
                    Toast.makeText(requireContext(), "Event updated with new image!", Toast.LENGTH_SHORT).show();
                    ((DashboardActivity) getActivity()).replaceFragment(new HomeFragment());
                }
            });
        } else {
            db.SaveEvent(event);
            Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
            ((DashboardActivity) getActivity()).replaceFragment(new HomeFragment());
        }
    }
}
