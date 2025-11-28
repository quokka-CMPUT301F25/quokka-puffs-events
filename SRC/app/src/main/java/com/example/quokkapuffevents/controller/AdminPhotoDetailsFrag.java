package com.example.quokkapuffevents.controller;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;
import com.google.android.gms.tasks.OnSuccessListener;

public class AdminPhotoDetailsFrag extends Fragment {
    private Database db;
    private Event event;
    TextView eventName;
    ImageView imageView;
    TextView organizer;
    Button goBackBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_details_fragment, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners();
        return view;

    }

    public void setEvent(Event event) {
        /**
         * Sets event
         * @param event
         */
        this.event = event;
    }

    public void initializeViews(View view) {
        /**
         * Finds each view and initializes each one
         * @param view The view that is accessed to find each view
         */
        eventName = view.findViewById(R.id.eventName);
        imageView = view.findViewById(R.id.imageView);
        organizer = view.findViewById(R.id.organizerName);
        goBackBtn = view.findViewById(R.id.goBackBtn);

    }

    public void displayInfo() {
        /**
         * Using the initialized views set each one to display each detail of the image
         */
        eventName.setText(event.getName());
        if (event.getImageID() != null) {
            db.GetImage(event.getImageID(), new OnSuccessListener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
        }

        db.GetUser(event.getOrg(), user -> {
            organizer.setText(user.getFirstName());
        });
    }

    public void setUpListeners() {
        /**
         * A back button to go back to all the images
         */
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
