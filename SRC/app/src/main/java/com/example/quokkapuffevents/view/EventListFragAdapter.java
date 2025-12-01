package com.example.quokkapuffevents.view;

import android.Manifest;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.DashboardActivity;
import com.example.quokkapuffevents.controller.EntrantEventDetailsFragment;
import com.example.quokkapuffevents.controller.OrganizerEventDetails;
import com.example.quokkapuffevents.model.*;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * General adapter that is used whenever events need to be used in a list view. Excluding admin fragments.
 */
public class EventListFragAdapter extends ArrayAdapter<Event> {

    //* FIXED: Use SAME reference of events instead of making a COPY
    private final List<Event> events;   // adapter owns the list
    private Event event;
    private final Database db = Database.getInstance();
    private String type;
    private DashboardActivity activity;
    private User user;
    private Double Ulat;
    private Double Ulng;

    //region --Waiting UI Elements--
    private LinearLayout pastEvents;
    private TextView originUserText_waiting;
    private TextView eventText_waiting;
    private TextView eventDate_waiting;
    private Button cancelEventBtn_waiting;
    private Button detailsEventBtn_waiting;
    //endregion

    //region --Past Events Elements--
    private LinearLayout waitingEvents;
    private TextView originUserText_past;
    private TextView eventText_past;
    private TextView eventDate_past;
    private Button eventDetailsBtn_past;
    //endregion

    //region --All Events Elements--
    private LinearLayout findEvents;
    private TextView originUserText_all;
    private TextView eventText_all;
    private TextView eventDate_all;
    private Button eventDetailsBtn_all;
    Button eventRegisterBtn_all;
    //endregion


    // ------------------ CONSTRUCTORS ------------------ //

    // FIX APPLIED: Do NOT create a NEW list. Use same reference!
    public EventListFragAdapter(Context context, ArrayList<Event> events, String type) {
        super(context, 0, events);
        this.events = events;   // <-- FIXED
        this.type = type;
    }

    public EventListFragAdapter(Context context, ArrayList<Event> events, String type, DashboardActivity activity) {
        super(context, 0, events);
        this.events = events;   // <-- FIXED
        this.type = type;
        this.activity = activity;
    }


    // ------------------ MAIN ADAPTER LOGIC ------------------ //

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false)
                : convertView;

        // FIX: Use events.get(position) instead of getItem(position)
        event = events.get(position);

        db.GetUser(db.GetCurrentUserID(), currUser -> {
            user = currUser;
        });

        //region --Waiting UI Elements--
        waitingEvents = view.findViewById(R.id.waiting_events_content);
        originUserText_waiting = view.findViewById(R.id.user_text_waiting);
        eventText_waiting = view.findViewById(R.id.event_text_waiting);
        eventDate_waiting = view.findViewById(R.id.event_date_waiting);

        cancelEventBtn_waiting = view.findViewById(R.id.cancel_event_btn_waiting);
        detailsEventBtn_waiting = view.findViewById(R.id.details_event_btn_waiting);
        //endregion

        //region --Past Events Elements--
        pastEvents = view.findViewById(R.id.past_events_content);
        originUserText_past = view.findViewById(R.id.user_text_past);
        eventText_past = view.findViewById(R.id.event_text_past);
        eventDate_past = view.findViewById(R.id.event_date_past);

        eventDetailsBtn_past = view.findViewById(R.id.event_details_btn_past);
        //endregion

        //region --All Events Elements--
        findEvents = view.findViewById(R.id.find_events_content_all);
        originUserText_all = view.findViewById(R.id.user_text_all);
        eventText_all = view.findViewById(R.id.event_text_all);
        eventDate_all = view.findViewById(R.id.event_date_all);

        eventDetailsBtn_all = view.findViewById(R.id.event_details_btn_all);
        eventRegisterBtn_all = view.findViewById(R.id.event_register_btn_all);
        //endregion

        UIBinding();

        if(type.equals("Waiting")) {
            eventText_waiting.setText(event.getName());
            eventDate_waiting.setText(event.getStartDate().toString());

            cancelEventBtn_waiting.setOnClickListener(v -> {
                leaveWaitingList(event);
            });

            detailsEventBtn_waiting.setOnClickListener(v -> {
                seeDetails(event);
            });

            originUserText_waiting.setText("Loading...");
            db.GetUser(event.getOrg(), user -> {
                originUserText_waiting.setText(user.getUserName().toString() + "'s     ");
            });
        }

        if(type.equals("Past")) {
            eventText_past.setText(event.getName());
            eventDate_past.setText(event.getStartDate().toString());

            eventDetailsBtn_past.setOnClickListener(v -> {
                seeDetails(event);
            });

            originUserText_past.setText("Loading...");
            db.GetUser(event.getOrg(), user -> {
                originUserText_past.setText(user.getUserName().toString() + "'s    ");
            });
        }

        if (type.equals("all")) {
            eventText_all.setText(event.getName());
            eventDate_all.setText(event.getStartDate().toString());

            eventDetailsBtn_all.setOnClickListener(v -> {
                seeDetails(event);
            });

            eventRegisterBtn_all.setOnClickListener(v -> {
                registerForEvent(event);
                notifyDataSetChanged();
            });

            originUserText_all.setText("Loading...");
            db.GetUser(event.getOrg(), user -> {
                originUserText_all.setText(user.getUserName().toString() + "'s     ");
            });
        }

        return view;
    }


    // ------------------ UI BINDING ------------------ //

    public void UIBinding(){
        if(type.equals("Waiting")) {
            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Past")) {
            pastEvents.setVisibility(View.VISIBLE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.GONE);
        }
        else if(type.equals("all")) {
            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.VISIBLE);
            waitingEvents.setVisibility(View.GONE);
        }
    }


    // ------------------ DATA UPDATE ------------------ //

    public void setEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }


    // ------------------ EVENT ACTIONS ------------------ //

    public void registerForEvent(Event event) {
        db.GetUser(db.GetCurrentUserID(), user -> {

            if (user.getLat() == null || user.getLng() == null) {
                Toast.makeText(activity,
                        "This event is locked by distance. No location set, please add one in settings.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double userLat = user.getLat();
            double userLng = user.getLng();
            double eventLat = event.getLat();
            double eventLng = event.getLng();
            Log.d("DEBUG_LOC",
                    "User: (" + userLat + ", " + userLng + ")  Event: (" + eventLat + ", " + eventLng + ")");

            float[] distance = new float[1]; // result in METERS
            Location.distanceBetween(userLat, userLng, eventLat, eventLng, distance);

            Log.d("Distance", "Distance from home: " + distance[0] + "m");

            if (distance[0] <= event.getLockRadius()) {
                db.RegisterUserIntoEvent(event, user);
                Toast.makeText(activity, "Registered!", Toast.LENGTH_SHORT).show();
                events.remove(event);
            } else {
                Toast.makeText(activity,
                        "Too far from the event (" + distance[0] + "m away)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void leaveWaitingList(Event event) {
        db.CancelUserIntoEvent(event, user);
    }

    public void seeDetails(Event event) {
        if (user == null) return;

        db.GetEvent(event.getId(), freshEvent -> {  // <-- FETCH AGAIN FROM FIREBASE!
            if (user.getAccountType() == 0) {
                EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
                entrantFrag.setEvent(freshEvent);   // <-- use the fresh event from Firestore
                activity.openFragment(entrantFrag);
            }
            else { // Organizer
                OrganizerEventDetails orgFrag = new OrganizerEventDetails();
                orgFrag.SetEvent(freshEvent);
                activity.replaceFragment(orgFrag);
            }
        });
    }


    public void setActivity(DashboardActivity activity) {this.activity = activity;}
}
