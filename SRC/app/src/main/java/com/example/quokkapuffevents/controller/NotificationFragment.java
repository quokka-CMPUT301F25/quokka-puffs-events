package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;

public class NotificationFragment extends Fragment {
    // NOTIFICATIONS PAGE FOR DashboardActivity
    String userID; //current user id
    private Database db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // GET INSTANCE OF DATABASE AND CURRENT USER ID
        db = Database.getInstance();
        userID = String.valueOf(db.GetCurrentUserID());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_dashboard_fragment, container);
        return view;
    }
}
