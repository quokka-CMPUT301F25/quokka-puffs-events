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
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.example.quokkapuffevents.view.AdminNotifFragAdapter;

import java.util.ArrayList;

public class AdminNotificationFragment extends Fragment {
    ListView listView;
    private AdminNotifFragAdapter adapter;
    private Database db;
    private ArrayList<Notif> notifList = new ArrayList<>();

    public AdminNotificationFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View notifFragmentView = inflater.inflate(R.layout.frag_admin_notifications, container, false);

        db = Database.getInstance();

        listView = notifFragmentView.findViewById(R.id.adminNotificationsListView);

        // Create an adapter for the notifications
        adapter = new AdminNotifFragAdapter(getContext(), notifList);
        listView.setAdapter(adapter);

        // Add all the notifications to the adapter
        db.ListNotifs( notifs -> {
            // refresh adapter
            notifList.clear();
            notifList.addAll(notifs);
            adapter.notifyDataSetChanged();
        });
        return notifFragmentView;
    }
}
