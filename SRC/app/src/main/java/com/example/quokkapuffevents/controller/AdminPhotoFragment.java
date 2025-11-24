package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.example.quokkapuffevents.view.AdminPhotoFragAdapter;

import java.util.ArrayList;

public class AdminPhotoFragment extends Fragment {

    ListView listView;
    private AdminPhotoFragAdapter adapter;
    private Database db;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> tempEventsList = new ArrayList<>();

    public AdminPhotoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View imageFragmentView = inflater.inflate(R.layout.frag_admin_images, container, false);

        db = Database.getInstance();

        listView = imageFragmentView.findViewById(R.id.adminImagesListView);

        // Create an adapter for the images
        adapter = new AdminPhotoFragAdapter(getContext(), eventList);
        listView.setAdapter(adapter);

        // Add all the images to the adapter
        db.ListEvents( events -> {
            // refresh adapter
            eventList.clear();

            for (Event event : events) {
                if (event.getImageID() != null) {
                    eventList.add(event);
                }
            }

            adapter.notifyDataSetChanged();
        });

        return imageFragmentView;
    }


}
