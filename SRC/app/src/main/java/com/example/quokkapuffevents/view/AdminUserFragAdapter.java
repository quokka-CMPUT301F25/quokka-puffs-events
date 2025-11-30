package com.example.quokkapuffevents.view;

import android.app.AlertDialog;
import android.content.Context;
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

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.AdminActivity;
import com.example.quokkapuffevents.controller.AdminUserDetailsFrag;
import com.example.quokkapuffevents.controller.EntrantEventDetailsFragment;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.model.User;

import java.util.ArrayList;

/**
 * Adapter for the admin fragment that configures and displays all users
 */
public class AdminUserFragAdapter extends ArrayAdapter<User> {

    private ArrayList<User> userList;
    private Database db;
    public AdminUserFragAdapter(@NonNull Context context, ArrayList<User> list) {
        super(context, 0, list);
        this.userList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.admin_users_content, parent,false);

        db = Database.getInstance();

        User currentUser = userList.get(position);

        TextView userTexts = (TextView) listItem.findViewById(R.id.UserNameTextView);
        userTexts.setText(currentUser.getUserName());

        TextView firstAndLastNameText = (TextView) listItem.findViewById(R.id.FirstAndLastNameTextView);
        String firstLast = currentUser.getFirstName() + " " + currentUser.getLastName();
        firstAndLastNameText.setText(firstLast);

        ImageView accountImg = (ImageView) listItem.findViewById(R.id.imageView);

        Integer type = currentUser.getAccountType();
        if (type == -1) { // Admin
            accountImg.setImageResource(R.drawable.admin_icon);
        }
        else if (type == 0) { // Entrant
            accountImg.setImageResource(R.drawable.entrant_icon);
        }
        else if (type == 1) { // Organizer
            accountImg.setImageResource(R.drawable.organizer_icon);
        }

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = getItem(position);

                new AlertDialog.Builder(getContext())
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.DeleteUser(user);
                            userList.remove(user);
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
                AdminUserDetailsFrag detailsFrag = new AdminUserDetailsFrag();
                detailsFrag.setUser(currentUser);

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
