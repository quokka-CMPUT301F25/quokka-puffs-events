package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.DashboardActivity;
import com.example.quokkapuffevents.controller.EntrantEventDetailsFragment;
import com.example.quokkapuffevents.controller.OrganizerEventDetails;
import com.example.quokkapuffevents.model.*;

import java.util.ArrayList;
import java.util.List;

public class EventListFragAdapter extends ArrayAdapter<Event> {

    private final List<Event> events;   // adapter owns the list
    private final Database db = Database.getInstance();
    private String type;
    //private Class<?> activity;
    private DashboardActivity activity;
    private User user;

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

    public EventListFragAdapter(Context context, ArrayList<Event> events, String type) {
        super(context, 0, events);
        this.events = new ArrayList<>(events);
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false)
                : convertView;

        Event event = getItem(position);

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
            originUserText_waiting.setText(event.getOrg());
            eventText_waiting.setText(event.getName());
            eventDate_waiting.setText(event.getStartDate().toString());

            cancelEventBtn_waiting.setOnClickListener(v -> {
                leaveWaitingList(event);
            });

            detailsEventBtn_waiting.setOnClickListener(v -> {
                seeDetails(event);
            });
        }

        if(type.equals("Past")) {
            //originUserText_past.setText(event.getOrg());
            eventText_past.setText(event.getName());
            eventDate_past.setText(event.getStartDate().toString());

            eventDetailsBtn_past.setOnClickListener(v -> {
                seeDetails(event);
            });
        }

        if (type.equals("all")) {
            //originUserText_all.setText(event.getOrg());
            eventText_all.setText(event.getName());
            eventDate_all.setText(event.getStartDate().toString());

            eventDetailsBtn_all.setOnClickListener(v -> {
                seeDetails(event);
            });

            eventRegisterBtn_all.setOnClickListener(v -> {
                registerForEvent(event);
            });
        }

        return view;
    }

    public void UIBinding(){
        if(type.equals("Waiting")) {

            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.VISIBLE);

        } else if(type.equals("Past")) {

            pastEvents.setVisibility(View.VISIBLE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.GONE);

        } else if(type.equals("all")) {

            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.VISIBLE);
            waitingEvents.setVisibility(View.GONE);

        }
    }

    public void setEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    public void registerForEvent(Event event) {
        db.RegisterUserIntoEvent(event, user);
    }

    public void leaveWaitingList(Event event) {
        db.CancelUserIntoEvent(event, user);
    }

    public void seeDetails(Event event) {
        if(user == null) {
            System.out.println("USER IS NULL");
        } else {
            if (user.getAccountType() == 0){
                EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
                entrantFrag.setEvent(event);
                activity.replaceFragment(entrantFrag);
            }
            else { //Organizer
                OrganizerEventDetails orgFrag = new OrganizerEventDetails();
                orgFrag.SetEvent(event);
                activity.replaceFragment(orgFrag);
            }

        }

    }

    public void setActivity(DashboardActivity activity) {this.activity = activity;}
}
