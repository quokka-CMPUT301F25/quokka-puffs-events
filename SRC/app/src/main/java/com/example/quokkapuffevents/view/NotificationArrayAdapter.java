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
import com.example.quokkapuffevents.controller.DashboardActivity;
import com.example.quokkapuffevents.controller.EntrantEventDetailsFragment;
import com.example.quokkapuffevents.controller.NotificationDetailsFragment;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter to configure and display notifications for all users. Excluding admin fragments.
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notif> {

    private final List<Notif> notifications;
    private final Database db = Database.getInstance();
    private DashboardActivity activity = (DashboardActivity) getContext();

    public NotificationArrayAdapter(Context context, ArrayList<Notif> notifications) {
        super(context, 0, notifications);
        this.notifications = new ArrayList<>(notifications);
    }

    @Override
    public int getItemViewType(int position) {
        Notif notification = getItem(position);

        // Avoid crash:
        if (notification == null) return 0;
        if (notification.getType() == null) return 0;

        if (notification.getType() == 1 && !Boolean.TRUE.equals(notification.getChosen())) {
            return 1; // invite layout
        }
        return 0; // message layout
    }



    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Notif notification = getItem(position);
        int viewType = getItemViewType(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (viewType == 1) {
                view = inflater.inflate(R.layout.notification_content_invite, parent, false);
                BindInviteUIAndLogic(view, notification);
            } else {
                view = inflater.inflate(R.layout.notification_content_message, parent, false);
            }
        }
        InitializeViews(view, notification);
        return view;
    }

    public void InitializeViews(View view, Notif notification) {
        TextView eventText = view.findViewById(R.id.eventText);
        Button removeButton = view.findViewById(R.id.removeBtn);
        TextView notificationTitle = view.findViewById(R.id.notificationTitle);

        notificationTitle.setText(notification.getTitle());
        eventText.setText("Loading...");

        db.GetEvent(notification.getOriginEvent(), event -> {
            eventText.setText(event.getName());
        });

        removeButton.setOnClickListener(v -> {
            db.DeleteNotification(notification);
            removeNotification(notification);
        });

        Button detailsButton = view.findViewById(R.id.detailsBtn);
        if (detailsButton != null) {
            detailsButton.setOnClickListener(v -> {
                NotificationDetailsFragment fragment = new NotificationDetailsFragment();
                fragment.setNotification(notification);
                activity.replaceFragment(fragment);
            });
        }
    }

    /**
     *  Binds the UI to logic for an invite notification. Clicking the accept button will notify
     *  the database that the user has accepted the invite, clicking the reject button will notify the
     *  database that the user has rejected the invite, clicking the details button will show the event details
     */
    public void BindInviteUIAndLogic(View view, Notif notification) {
        Button rejectButton = view.findViewById(R.id.rejectBtn);
        Button acceptButton = view.findViewById(R.id.acceptBtn);

        rejectButton.setOnClickListener(v -> InvitationButtonClicked(notification, 0));
        acceptButton.setOnClickListener(v -> InvitationButtonClicked(notification, 1));
    }

    public void InvitationButtonClicked(Notif notification, int choice) {
        notification.setChoice(choice);
        notification.setChosen(true);
        db.SaveNotif(notification);
        notifyDataSetChanged();

        db.GetEvent(notification.getOriginEvent(), event -> {
            if (choice == 1) {
                event.SetStatus(db.GetCurrentUserID(), "Accepted");
            } else {
                event.SetStatus(db.GetCurrentUserID(), "Cancelled");
            }
            db.SaveEvent(event);
        });
    }

    public void removeNotification(Notif notif) {
        remove(notif);
        notifyDataSetChanged();
    }

    public void setNotifications(List<Notif> newNotifs) {
        clear();                // CLEAR ArrayAdapter list
        addAll(newNotifs);      // ADD new data
        notifyDataSetChanged();
    }
}
