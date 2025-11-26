package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.AdminActivity;
import com.example.quokkapuffevents.controller.AdminEventDetailsFrag;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter for the admin fragment that configures and displays all events
 */
public class AdminEventFragAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Database db;
    public AdminEventFragAdapter(@NonNull Context context, ArrayList<Event> list) {
        super(context, 0, list);
        this.eventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_events_content, parent,false);

        db = Database.getInstance();

        Event currentEvent = eventList.get(position);

        TextView eventTexts = (TextView) listItem.findViewById(R.id.eventName);
        eventTexts.setText(currentEvent.getName());

        TextView eventCreators = (TextView) listItem.findViewById(R.id.eventCreator);
        db.GetUser(currentEvent.getOrg(), user -> {
            eventCreators.setText(user.getUserName() + "'s");
        });

        TextView eventDates = (TextView) listItem.findViewById(R.id.eventDate);
        Date startDate = currentEvent.getStartDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd, yyyy");
        String temp = "On " + (String)sdf.format(currentEvent.getStartDate());
        eventDates.setText(temp);

        Event event = getItem(position);

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.DeleteEvent(event);
                eventList.remove(event);
                notifyDataSetChanged();
            }
        });

        Button detailsButton = listItem.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEventDetailsFrag detailsFrag = new AdminEventDetailsFrag();
                detailsFrag.setEvent(event);

                AdminActivity activity = (AdminActivity) getContext();

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, detailsFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return listItem;
    }
}
