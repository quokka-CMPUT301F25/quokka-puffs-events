package com.example.quokkapuffevents.view;

import android.content.Context;
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

    private View view;
    private DashboardActivity activity = (DashboardActivity) getContext();

    private Notif notification;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Database db = Database.getInstance();
        notification = getItem(position);

        if (notification.getType() == 1) {
            view = (convertView == null)
                    ? LayoutInflater.from(getContext()).inflate(R.layout.notification_content_invite, parent, false)
                    : convertView;

            BindInviteUIAndLogic();
        }
        else if(notification.getType() == 0) {
            view = (convertView == null)
                    ? LayoutInflater.from(getContext()).inflate(R.layout.notification_content_rejected, parent, false)
                    : convertView;

            BindRejectUIAndLogic();
        }
        else if(notification.getType() == -1) {
            view = (convertView == null)
                    ? LayoutInflater.from(getContext()).inflate(R.layout.notification_content_message, parent, false)
                    : convertView;

            BindMessageUIAndLogic();
        }

        //--- Universal Notification UI ---
        TextView eventText = view.findViewById(R.id.eventText);
        Button removeButton = view.findViewById(R.id.removeBtn);

        removeButton.setOnClickListener(v -> {
            db.DeleteNotification(notification);
            removeNotification(notification);
        });

        db.GetEvent(notification.getOriginEvent(), event -> {
            eventText.setText(event.getName());
        });

        return view;
    }

    /**
     *  Binds the UI to logic for an invite notification. Clicking the accept button will notify
     *  the database that the user has accepted the invite, clicking the reject button will notify the
     *  database that the user has rejected the invite, clicking the details button will show the event details
     */
    public void BindInviteUIAndLogic() {
        Button rejectButton = view.findViewById(R.id.rejectBtn);
        Button acceptButton = view.findViewById(R.id.acceptBtn);
        Button detailsButton = view.findViewById(R.id.detailsBtn);

        rejectButton.setOnClickListener(v -> {
            InvitationButtonClicked(notification, 0);
        });

        acceptButton.setOnClickListener(v -> {
            InvitationButtonClicked(notification, 1);
        });

        detailsButton.setOnClickListener(v -> {
            db.GetEvent(notification.getOriginEvent(), event -> {
                EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
                entrantFrag.setEvent(event);
                activity.replaceFragment(entrantFrag);
            });
        });
    }

    /**
     * Binds the UI to logic for a reject notification. Clicking the details button will bring
     * you to the event details.
     */
    private void BindRejectUIAndLogic() {
        Button detailsButton = view.findViewById(R.id.detailsBtn);
        detailsButton.setOnClickListener(v -> {
            db.GetEvent(notification.getOriginEvent(), event -> {
                EntrantEventDetailsFragment entrantFrag = new EntrantEventDetailsFragment();
                entrantFrag.setEvent(event);
                activity.replaceFragment(entrantFrag);
            });
        });
    }

    /**
     * Binds the UI to logic for a reject notification. Clicking the details button will bring
     * you to the notification details.
     */
    private void BindMessageUIAndLogic() {
        Button detailsButton = view.findViewById(R.id.detailsBtn);
        detailsButton.setOnClickListener(v -> {
            NotificationDetailsFragment notifDetailsFragment = new NotificationDetailsFragment();
            notifDetailsFragment.setNotification(notification);
            activity.replaceFragment(notifDetailsFragment);
        });
    }

    /**
     * @param notification -> The notification to update
     *
     * Changes the status of the user in the database based on selection in the notification.
     * If the user clicks the details button, the notification message will be shown in a new fragment
     */
    public void UpdateEventStatus(Notif notification) {
        db.GetEvent(notification.getOriginEvent(), event -> {
            if (notification.getChoice() == 1) {
                event.SetStatus(db.GetCurrentUserID(), "Accepted");
                db.SaveEvent(event);
            }
            else
                db.GetUser(db.GetCurrentUserID(), user -> {
                    db.CancelUserIntoEvent(event, user);
                });
        });
    }

    /**
     * @param notification -> The notification to update
     *
     * Updates the notification so that invitations options are not shown more than once after a
     * selection.
     */
    public void InvitationButtonClicked(Notif notification, int choice) {
        notification.setChoice(choice);
        notification.setChosen(true);
        db.SaveNotif(notification);
        UpdateEventStatus(notification);
        notifyDataSetChanged();
    }
}
