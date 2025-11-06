package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        System.out.println(currentEvent.getName() + "POP");

        TextView eventDates = (TextView) listItem.findViewById(R.id.eventDate);
        Date startDate = currentEvent.getStartDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd");
        eventDates.setText((String)sdf.format(currentEvent.getStartDate()));

        ImageButton deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = getItem(position);
                db.DeleteEvent(event);
                eventList.remove(event);
                notifyDataSetChanged();
            }
        });

        return listItem;
    }
}
