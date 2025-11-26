package com.example.quokkapuffevents.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;

import java.util.ArrayList;

/**
 * Adapter to configure and display users that have joined an event to the organiser of that event.
 */
public class OrgViewParticipantsFragAdapter extends ArrayAdapter<User> {

    private ArrayList<User> userList;
    private Database db;

    public OrgViewParticipantsFragAdapter(@NonNull Context context, ArrayList<User> list) {
        super(context, 0, list);
        this.userList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.organizer_view_participants_content, parent,false);

        db = Database.getInstance();

        User currentUser = userList.get(position);
        TextView userTexts = (TextView) listItem.findViewById(R.id.entrantName);
        userTexts.setText(currentUser.getUserName());


        return listItem;
    }
}

