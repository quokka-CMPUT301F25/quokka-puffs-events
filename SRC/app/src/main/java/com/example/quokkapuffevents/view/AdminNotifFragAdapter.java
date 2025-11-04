package com.example.quokkapuffevents.view;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;

import java.util.ArrayList;

public class AdminNotifFragAdapter extends ArrayAdapter<Notif> {

    private ArrayList<Notif> notifList;
    private final Database db = Database.getInstance();


    public AdminNotifFragAdapter(@NonNull Context context, ArrayList<Notif> list) {
        super(context, 0, list);
        this.notifList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_notifications_content, parent,false);

        Notif currentNotif = notifList.get(position);

        TextView notifTitles = (TextView) listItem.findViewById(R.id.notifTitle);
        notifTitles.setText(currentNotif.getMessage());

        TextView notifSubtitles1 = (TextView) listItem.findViewById(R.id.notifSubtitle);
        String originUser;
        db.GetUser(currentNotif.getOriginUser(), user ->
                notifSubtitles1.setText(String.format("%s sent this notification to ",
                        user.getUserName())));

        TextView notifSubtitles2 = (TextView) listItem.findViewById(R.id.notifSubtitle2);
        db.GetUser(currentNotif.getRecipient(), user ->
                notifSubtitles2.setText(String.format("%s", user.getUserName())));

        return listItem;
    }
}
