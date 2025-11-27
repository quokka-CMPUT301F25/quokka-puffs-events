package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.*;
import com.google.android.gms.common.api.Response;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import java.util.Map;

public class SendMessageFragment extends Fragment {
    private Database db;
    private Event event;

    EditText notifTitle;
    TextView eventTitle;
    EditText message;
    Button backBtn;
    Button sendMessage;


    public void SetEvent(Event event) {
        this.event = event;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notif_message_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = Database.getInstance();
        InitializeViews(view);
        SetUpListeners(view);
    }

    /**
     * Initializes all view references including message inputs and buttons.
     *
     * @param view The root view of this fragment.
     */
    private void InitializeViews(View view) {
        notifTitle = view.findViewById(R.id.notifName);
        eventTitle = view.findViewById(R.id.event);
        eventTitle.setText(event.getName());
        message = view.findViewById(R.id.messageText);
        backBtn = view.findViewById(R.id.goBackBtn);
        sendMessage = view.findViewById(R.id.sendMessageBtn);
    }

    /**
     * Sets up interaction listeners for navigating back and sending messages.
     *
     * @param view The root view of the fragment.
     */
    public void SetUpListeners(View view) {
        backBtn.setOnClickListener(v -> {
            OrganizerEventDetails newFrag = new OrganizerEventDetails();
            newFrag.SetEvent(event);
            ((DashboardActivity) getActivity()).replaceFragment(newFrag);
        });

        sendMessage.setOnClickListener(v -> {
            Map<String, String> eventUsers = event.getEventUsers();

            for(Map.Entry<String, String> entry : eventUsers.entrySet()) {
                if(entry.getValue().equals("Cancelled"))
                    continue;

                db.CreateNotification(0, entry.getKey(), event.getId(), event.getOrg(), message.getText().toString(), notifTitle.getText().toString());
            }

            OrganizerEventDetails newFrag = new OrganizerEventDetails();
            newFrag.SetEvent(event);
            ((DashboardActivity) getActivity()).replaceFragment(newFrag);
        });
    }
}