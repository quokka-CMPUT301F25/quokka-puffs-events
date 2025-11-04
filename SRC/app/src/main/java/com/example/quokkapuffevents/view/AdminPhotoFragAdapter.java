package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Event;

import java.util.ArrayList;

public class AdminPhotoFragAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> eventList;

    public AdminPhotoFragAdapter(@NonNull Context context, ArrayList<Event> list) {
        super(context, 0, list);
        this.eventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_images_content, parent, false);

        Event currentEvent = eventList.get(position);
        if (currentEvent.getImageID() == null) {
            ImageView eventImage = listItem.findViewById(R.id.imageView);
            eventImage.setImageResource(R.drawable.image_temp);
        }

        return listItem;
    }
}
