package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.view.EventListFragAdapter;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;

import java.util.ArrayList;

public class RegisterEventsFragment extends Fragment {
    /*// FILTER/REGISTER FOR EVENTS FOR DashboardActivity
    String userID; //current user id
    private Database db;

    private ListView listView;
    private EventListFragAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find_events_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeUI(@NonNull View view) {
        listView = view.findViewById(R.id.findEventsListView);
        adapter = new EventListFragAdapter(requireContext(), new ArrayList<>(), new String());
        listView.setAdapter(adapter);
    }

    private void LoadEvent(){
        String userID = db.GetCurrentUserID();
        db.GetUser(userID, user -> db.GetUserNotifications(user, ));
    }*/

}
