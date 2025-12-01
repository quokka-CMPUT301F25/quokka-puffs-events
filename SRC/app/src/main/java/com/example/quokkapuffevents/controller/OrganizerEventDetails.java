package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class OrganizerEventDetails extends Fragment {

    Database db;
    Event event;
    Button runLottoButton;
    Button finishEventButton;
    Button viewParticipantsButton;
    Button changeDetailsButton;
    ImageView qrcodeView;
    ImageView eventImage;
    Button exitButton;
    Button sendMessageButton;
    Button updateImgBtn;
    Button showQrCode;
    Button showPosterImg;
    TextView description;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Bitmap selectedImageBitmap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_event_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerImagePickerLauncher();
        initialize(view);
        SetUpListeners(view);
    }

    private void initialize(View view) {
        /**
         * Initializes UI components and business logic for the fragment.
         * This includes displaying the QR code and enabling/disabling the
         * lottery button depending on the event status.
         *
         * @param view The view from which UI components are retrieved
         */
        db = Database.getInstance();
        description = view.findViewById(R.id.eventDescriptionText);
        description.setText(event.getDescription());

        //Button

        runLottoButton = view.findViewById(R.id.orgRunLotteryBtn);
        finishEventButton = view.findViewById(R.id.orgFinishEvent);
        viewParticipantsButton = view.findViewById(R.id.orgViewParticipantsBtn);
//        changeDetailsButton = view.findViewById(R.id.orgChangeDetailsBtn);
        sendMessageButton = view.findViewById(R.id.orgSendMessageBtn);
        exitButton = view.findViewById(R.id.orgExitOutEventBtn);
        qrcodeView = view.findViewById((R.id.qrCode));
        updateImgBtn = view.findViewById(R.id.updatePosterBtn);
        showPosterImg = view.findViewById(R.id.viewEventPosterBtn);
        showQrCode = view.findViewById(R.id.viewQRCodeBtn);
        eventImage = view.findViewById(R.id.eventImage);



        //Removing button if after end of event
        if ((event.getEventDate().after(new Date())) && (event.getDrawn() == false)){
            runLottoButton.setOnClickListener(v -> {
                db.DrawUsers(event);
                runLottoButton.setVisibility(INVISIBLE);

            });
        }
        else {
            runLottoButton.setVisibility(INVISIBLE);
            if (event.getFinished() == false){
                finishEventButton.setVisibility(VISIBLE);
                finishEventButton.setOnClickListener(v -> {
                    db.FinishEvent(event);
                    finishEventButton.setVisibility(INVISIBLE);
                });
            }
        }

        //QRCode
        db.GetImage(event.getQrcodeID(), bitmap -> {
            if (bitmap != null) {
                qrcodeView.setImageBitmap(bitmap);
            } else {
                Log.e("IMAGES", "Bitmap from GetImage is null");
            }
        });

        //poster Image

        db.GetImage(event.getImageID(), bitmap -> {
            eventImage.setImageBitmap(bitmap);
        });

        eventImage.setVisibility(view.GONE);
        qrcodeView.setVisibility(view.GONE);
    }

    public void SetUpListeners(View view) {
        /**
         * Sets up button listeners for navigation and event management options.
         *
         * @param view The view from which buttons are retrieved
         */
        exitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer goes back to home view
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                HomeFragment newFrag = new HomeFragment();
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

        viewParticipantsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer can view participants in event
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                OrganizerViewParticipantsFragment newFrag = new OrganizerViewParticipantsFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Organizer can sent message to entrants
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                SendMessageFragment newFrag = new SendMessageFragment();
                newFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(newFrag);
            }
        });


        showQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(qrcodeView.getVisibility() == view.GONE) {

                    showQrCode.setText("Hide QR Code");
                    qrcodeView.setVisibility(view.VISIBLE);
                } else {
                    showQrCode.setText("View QR Code");
                    qrcodeView.setVisibility(view.GONE);
                }

            }
        });


        updateImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerLauncher.launch(Intent.createChooser(intent, "Select Event Poster Image"));

                if (selectedImageBitmap != null) {
                    db.UploadImageToDatabase(selectedImageBitmap,uri -> {
                        event.setImageID(uri);
                        db.SaveEvent(event);
                    });
                }

            }

        });

        showPosterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eventImage.getVisibility() == view.GONE) {

                    showPosterImg.setText("Hide Poster Image");
                    eventImage.setVisibility(view.VISIBLE);
                } else {
                    showPosterImg.setText("View Poster Image");
                    eventImage.setVisibility(view.GONE);
                }

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

    /**
     * Basic getter of getting the current event
     * @param event
     */
    public void SetEvent(Event event) {this.event = event; }
}


