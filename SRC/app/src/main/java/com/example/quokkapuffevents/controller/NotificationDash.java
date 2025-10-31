package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.util.Log;
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
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;

import java.util.ArrayList;

/**
 * Fragment responsible for displaying notifications for the current user.
 */
public class NotificationDash extends Fragment {

    // --- DATABASE INSTANCE ---
    private final Database db = Database.getInstance();

    // --- UI ELEMENTS ---
    private ListView listView;
    private NotificationArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_dashboard, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(
                view.findViewById(R.id.notifications_fragment),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
        loadNotifications();
    }


    /**
     * Initializes UI elements like ListView and its adapter.
     */
    private void initializeUI(@NonNull View view) {
        listView = view.findViewById(R.id.NotifList);
        adapter = new NotificationArrayAdapter(requireContext(), new ArrayList<>());
        listView.setAdapter(adapter);
    }

    /**
     * Retrieves and displays the current user’s notifications.
     */
    private void loadNotifications() {
        String userId = ensureUserId();
        db.GetUser(userId, user -> db.GetUserNotifications(user, adapter::setNotifications));
    }

    /**
     * Ensures a valid user ID is set (injects a test ID if null during testing).
     */
    private String ensureUserId() {
        String userId = db.GetCurrentUserID();
        if (userId == null) {
            Log.w("NotificationDash", "Test environment: injecting fake userID");
            db.SetUserID("TEST_USER_ID");
            userId = "TEST_USER_ID";
        }
        return userId;
    }
}
