package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Notif;

import java.sql.Array;
import java.util.ArrayList;

public class NotificationArrayAdapter extends ArrayAdapter<Notif> {
    public NotificationArrayAdapter(Context context, ArrayList<Notif> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_content, parent, false);
        } else {
            view = convertView;
        }

        Notif notification = getItem(position);

        TextView notificationType = view.findViewById(R.id.notificationTypeText);
        TextView userText = view.findViewById(R.id.userText);
        TextView eventText = view.findViewById(R.id.eventText);

        if (notification.getType() == 1) {
            notificationType.setText(R.string.winner_header);
        } else {
            notificationType.setText(R.string.not_picked);
        }

        userText.setText(notification.getOriginUser());
        eventText.setText(notification.getOriginEvent());



        return view;
    }

}
