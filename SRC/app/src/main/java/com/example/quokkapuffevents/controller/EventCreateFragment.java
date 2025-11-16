package com.example.quokkapuffevents.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.quokkapuffevents.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.InputStream;
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
    Switch limitPar;
    //the switch in XML file that determines whether organizer would like to limit the numb of participants
    EditText numbPar; //not in views yet, number of participants to be chosen
    EditText maxPar; // max number of participants to join waiting list
    Switch addGeo; //TODO: idek???
    Button cancelEvent; //button to cancel event
    Button createEvent; //button to initialize creating the event
    String userID; //current user id

    ImageView qrcodeView;

    String maxParts;

    // IMAGE UPLOAD VARIABLES
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Bitmap selectedImageBitmap;
    Button addImagesBtn; //TODO: How will we implement images for events and add to database

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = Database.getInstance();
        userID = db.GetCurrentUserID();
        registerImagePickerLauncher();
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
        addImagesBtn = view.findViewById(R.id.eventAddImagesBtn);
        limitPar = view.findViewById(R.id.eventLimitParticipantsSwitch);
        maxPar = view.findViewById(R.id.eventMaxParticipantsInput);
        addGeo = view.findViewById(R.id.eventGeolocationSwitch);
        cancelEvent = view.findViewById(R.id.cancelEventCreationBtn);
        createEvent = view.findViewById(R.id.confirmEventCreationBtn);
        numbPar = view.findViewById(R.id.eventParticipantAmountInput);

        qrcodeView = view.findViewById(R.id.QRCODETEST);
    }

    public void setUpListeners(View view) {

        // opens ui which allows user to select where they want theyre images pulled from
        addImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerLauncher.launch(Intent.createChooser(intent, "Select Event Poster Image"));
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


               // Getting input values
                String title = eventTitle.getText().toString().trim();
                String desc = eventDesc.getText().toString().trim();

                //TODO: change if we are adding a calendar widget
                //Format that user type has to be yyyy-mm-dd in order for DateConverter to work
                String eventDateString = dateOfEvent.getText().toString().trim();
                String drawDateString = drawDate.getText().toString().trim();
                int parts = Integer.parseInt(numbPar.getText().toString()); // number of wanted participants in event
                String maxParts = "";

                boolean limitParts = limitPar.isChecked();
                if (limitParts){
                    maxParts = maxPar.getText().toString().trim();
                }

//                boolean addGeolocate = addGeo.isChecked(); //TODO: IDK YET???
                  //Validating inputs
                if (!validateInputs()) {
                    return;
                }

                //Creating Date Objects
                Date drawDate = dateConverter(drawDateString);
                Date eventDate = dateConverter(eventDateString);

                //Create event in database
                if (maxParts.isEmpty()){
                    Event event = db.CreateEvent(title, userID, desc, parts, drawDate, eventDate);
                    //Creating QR code
                    Bitmap bitmap = generateQRCode("quokka-puff://event/" + event.getId());
                    //Saving bitmap and image poster
//                    db.UploadImageToDatabase(bitmap, uri -> {
//                        event.setQrcodeID(uri);
//                        db.SaveEvent(event);
//                    });
//                    db.UploadImageToDatabase(selectedImageBitmap,uri -> {
//                        event.setImageID(uri);
//                        db.SaveEvent(event);
//                    });

                    qrcodeView.setImageBitmap(bitmap);
                } else {
                    int maxPar = Integer.parseInt(maxParts);
                    Event event = db.CreateEvent(title, userID, desc, parts, maxPar, drawDate, eventDate);
                    //Creating QR code
                    Bitmap bitmap = generateQRCode("quokka-puff://event/" + event.getId());
                    //Saving bitmap and image poster
//                    db.UploadImageToDatabase(bitmap, uri -> {
//                        event.setQrcodeID(uri);
//                        db.SaveEvent(event);
//                    }
//                    db.UploadImageToDatabase(selectedImageBitmap,uri -> {
//                        event.setImageID(uri);
//                        db.SaveEvent(event);
//                    });
                    qrcodeView.setImageBitmap(bitmap);
                }




               // ((DashboardActivity) getActivity()).replaceFragment(new HomeFragment());
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

    //This code was adapted from GeeksForGeeks. https://www.geeksforgeeks.org/android/how-to-generate-qr-code-in-android/
    private Bitmap generateQRCode(String text)
    {
        BarcodeEncoder barcodeEncoder
                = new BarcodeEncoder();
        try {
            // pixels.
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 280, 280);
            return(bitmap);
        }
        catch (WriterException e) {
            System.out.println("ERROR in creation");
            e.printStackTrace();
        }
        return(null);
    }

}
