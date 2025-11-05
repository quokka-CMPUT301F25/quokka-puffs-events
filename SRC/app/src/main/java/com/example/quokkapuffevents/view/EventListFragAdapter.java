package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.*;

import java.util.ArrayList;
import java.util.List;

public class EventListFragAdapter extends ArrayAdapter<Event> {

    private final List<Event> events;   // adapter owns the list
    private final Database db = Database.getInstance();
    private int type;

    public EventListFragAdapter(Context context, ArrayList<Event> events, int type) {
        super(context, 0, events);
        this.events = new ArrayList<>(events);
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.find_events_content, parent, false)
                : convertView;

    }

}
