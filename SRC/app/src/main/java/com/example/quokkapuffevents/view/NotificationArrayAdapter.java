package com.example.quokkapuffevents.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;

import java.util.ArrayList;
import java.util.List;

public class NotificationArrayAdapter extends ArrayAdapter<Notif> {

    private final List<Notif> notifications;   // adapter owns the list
    private final Database db = Database.getInstance();

    public NotificationArrayAdapter(Context context, ArrayList<Notif> notifications) {
        super(context, 0, notifications);
        this.notifications = new ArrayList<>(notifications); // make a copy
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Nullable
    @Override
    public Notif getItem(int position) {
        return notifications.get(position);
    }

    /** External update helpers **/
    public void setNotifications(List<Notif> newNotifs) {
        notifications.clear();
        notifications.addAll(newNotifs);
        notifyDataSetChanged();
    }

    public void removeNotification(Notif notif) {
        notifications.remove(notif);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.notification_content, parent, false)
                : convertView;

        Notif notification = getItem(position);

        TextView notificationType = view.findViewById(R.id.notificationTypeText);
        TextView userText = view.findViewById(R.id.userText);
        TextView eventText = view.findViewById(R.id.eventText);

        Button removeButton = view.findViewById(R.id.removeBtn);
        Button rejectButton = view.findViewById(R.id.rejectBtn);
        Button acceptButton = view.findViewById(R.id.acceptBtn);

        // --- UI Binding ---
        if (notification.getType() == 1 && !notification.getChosen()) {
            notificationType.setText(R.string.winner_header);
            removeButton.setVisibility(View.GONE);
        } else if (!notification.getChosen()) {
            notificationType.setText(R.string.not_picked);
            rejectButton.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
        }

        // --- Button Logic ---
        removeButton.setOnClickListener(v -> {
            db.DeleteNotification(notification);
            removeNotification(notification);   // remove locally
        });

        rejectButton.setOnClickListener(v -> {
            notification.setChoice(0);
            notification.setChosen(true);
            db.SaveNotif(notification);
            UpdateEventStatus(notification);
            notifyDataSetChanged();
        });

        acceptButton.setOnClickListener(v -> {
            notification.setChoice(1);
            notification.setChosen(true);
            db.SaveNotif(notification);
            UpdateEventStatus(notification);
            notifyDataSetChanged();
        });

        // --- Async user/event UI Binding ---
        db.GetUser(notification.getOriginUser(), user ->
                userText.setText(String.format("%s's: ", user.getUserName())));

        db.GetEvent(notification.getOriginEvent(), event -> {
                eventText.setText(event.getName());
        });

        if (notification.getChosen()) {
            removeButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
        }

        return view;
    }

    public void UpdateEventStatus(Notif notification) {
        db.GetEvent(notification.getOriginEvent(), event -> {
            if (notification.getChoice() == 1)
                event.SetStatus(db.GetCurrentUserID(), "Accepted");
            else
                event.SetStatus(db.GetCurrentUserID(), "Rejected");

            db.SaveEvent(event);
        });
    }
}
