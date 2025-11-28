package com.example.quokkapuffevents.view;

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
        if (type == -1) {
            System.out.println("ADMIN");
        }
        else if (type == 0) {
            System.out.println("ENTRANT");
        }
        else if (type == 1) {
            System.out.println("ORG");
        }

        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = getItem(position);
                db.DeleteUser(user);
                userList.remove(user);
                notifyDataSetChanged();
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
