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
import com.example.quokkapuffevents.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class EventListFragAdapter extends ArrayAdapter<Event> {

    private final List<Event> events;   // adapter owns the list
    private final Database db = Database.getInstance();
    private String type;

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

        return view;
    }

    public void UIBinding(){
        AtomicReference<ArrayList<Event>> eventsList = new AtomicReference<>(new ArrayList<>());

        if(type.equals("Waiting")) {

            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.VISIBLE);

//            db.GetEventsFromUser(user, events -> {
//               for(Event e : events) {
//                   Map<String, String> users = e.getEventUsers();
//                   String status = users.get(user.getId());
//
//                   if(status.equals("Waiting")) {
//                       eventsList.get().add(e);
//                   }
//               }
//           });
        } else if(type.equals("Past")) {

            pastEvents.setVisibility(View.VISIBLE);
            findEvents.setVisibility(View.GONE);
            waitingEvents.setVisibility(View.GONE);

//            db.GetEventsFromUser(user, Events ->{
//                for(Event e : events) {
//                   eventsList.get().add(e);
//                }
//            });
        } else if(type.equals("all")) {

            pastEvents.setVisibility(View.GONE);
            findEvents.setVisibility(View.VISIBLE);
            waitingEvents.setVisibility(View.GONE);

//            db.ListEvents(events ->{
//               eventsList.set(events);
//            });
        }

        //return eventsList.get();
    }

    public void setEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    public void registerForEvent(Event event) {

    }

    public void leaveWaitingList() {

    }

    public void seeDetails() {

    }
}
