package com.example.quokkapuffevents.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.AdminActivity;
import com.example.quokkapuffevents.controller.AdminNotifDetailsFrag;
import com.example.quokkapuffevents.controller.AdminPhotoDetailsFrag;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Adapter for the admin fragment that configures and displays all images
 */
public class AdminPhotoFragAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Database db;
    public AdminPhotoFragAdapter(@NonNull Context context, ArrayList<Event> list) {
        super(context, 0, list);
        this.eventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_images_content, parent, false);

        db = Database.getInstance();

        Event currentEvent = eventList.get(position);
        if (currentEvent.getImageID() != null) {
            ImageView eventImage = listItem.findViewById(R.id.imageView);
            db.GetImage(currentEvent.getImageID(), image -> {
                eventImage.setImageBitmap(image);
            });
        }

        TextView eventName =  listItem.findViewById(R.id.eventName);
        eventName.setText(currentEvent.getName());

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = getItem(position);
                db.DeleteImage(event.getImageID());
                notifyDataSetChanged();
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Image")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Event eventImg = getItem(position);
                            db.DeleteImage(eventImg.getImageID());
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
                AdminPhotoDetailsFrag detailsFrag = new AdminPhotoDetailsFrag();
                detailsFrag.setEvent(currentEvent);

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
