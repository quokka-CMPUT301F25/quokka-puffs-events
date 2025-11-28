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
import com.example.quokkapuffevents.controller.AdminActivity;
import com.example.quokkapuffevents.controller.AdminNotifDetailsFrag;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;

import java.util.ArrayList;

/**
 * Adapter for the admin fragment that configures and displays all notifications
 */
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

        TextView notifName = (TextView) listItem.findViewById(R.id.notifName);
        notifName.setText(currentNotif.getMessage());

        TextView notifTitles = (TextView) listItem.findViewById(R.id.originUser);
        db.GetUser(currentNotif.getOriginUser(), user -> {
            notifTitles.setText(user.getUserName());
        });

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notif notif = getItem(position);
                db.DeleteNotification(notif);
                notifList.remove(notif);
                notifyDataSetChanged();
            }
        });

        Button detailsButton = listItem.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminNotifDetailsFrag detailsFrag = new AdminNotifDetailsFrag();
                detailsFrag.setNotif(currentNotif);

                AdminActivity activity = (AdminActivity) getContext();

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, detailsFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return listItem;
    }
}
