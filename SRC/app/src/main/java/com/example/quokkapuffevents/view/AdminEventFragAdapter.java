package com.example.quokkapuffevents.view;

import android.app.AlertDialog;
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
import com.example.quokkapuffevents.controller.AdminEventDetailsFrag;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter for the admin fragment that configures and displays all events
 */
public class AdminEventFragAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Database db;
    public AdminEventFragAdapter(@NonNull Context context, ArrayList<Event> list) {
        super(context, 0, list);
        this.eventList = list;
    }

    /**
     *
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     *  A View corresponding to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_events_content, parent,false);

        db = Database.getInstance();

        Event currentEvent = eventList.get(position);

        TextView eventTexts = (TextView) listItem.findViewById(R.id.eventName);
        eventTexts.setText(currentEvent.getName());

        TextView eventCreators = (TextView) listItem.findViewById(R.id.eventCreator);
        db.GetUser(currentEvent.getOrg(), user -> {
            eventCreators.setText(user.getUserName());
        });

        Date startDate = currentEvent.getStartDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd, yyyy");
        String temp = "On " + (String)sdf.format(currentEvent.getStartDate());

        TextView descriptionTextView = (TextView) listItem.findViewById(R.id.eventDescription);

        String shortDescription = currentEvent.getDescription();
        if(currentEvent.getDescription().length() > 28)
            shortDescription = shortDescription.substring(0, 28) + "...";
        descriptionTextView.setText(shortDescription);

        Event event = getItem(position);

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Notification")
                        .setMessage("Are you sure you want to delete this notification?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.DeleteEvent(event);
                            eventList.remove(event);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        Button detailsButton = listItem.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEventDetailsFrag detailsFrag = new AdminEventDetailsFrag();
                detailsFrag.setEvent(event);

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
