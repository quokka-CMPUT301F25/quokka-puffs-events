package com.example.quokkapuffevents.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class AdminUserDetailsFrag extends Fragment {
    private Database db;
    private User user;
    TextView email;
    TextView firstAndLastName;
    TextView username;
    TextView userid;
    TextView phoneNumber;
    TextView accountType;
    ListView allEventsUser;
    Button goBackBtn;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // GET INSTANCE OF DATABASE AND CURRENT USER INFO
        db = Database.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_details_fragment, container, false);
        initializeViews(view);
        displayInfo();
        setUpListeners();
        return view;
    }

    public void setUser(User user) {
        /**
         * sets user
         * @param user
         */
        this.user = user;
    }

    public void initializeViews(View view) {
        /**
         * Finds each view and initializes each one
         * @param view The view that is accessed to find each view
         */
        firstAndLastName = view.findViewById(R.id.firstAndLastName);
        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        userid = view.findViewById(R.id.userid);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        accountType = view.findViewById(R.id.accountType);
        allEventsUser = view.findViewById(R.id.allEventsUser);
        goBackBtn = view.findViewById(R.id.goBackBtn);

    }

    public void displayInfo() {
        /**
         * Using the initialized views set each one to display each detail of the user
         */
        firstAndLastName.setText(user.getFirstName() + " " + user.getLastName());
        email.setText(user.getEmail());
        username.setText(user.getUserName());
        userid.setText(user.getId());
        phoneNumber.setText((String)user.getPhoneNumber());

        if (user.getAccountType() == -1) {
            accountType.setText("Admin");
        }
        if (user.getAccountType() == 0) {
            accountType.setText("Entrant");
        }
        if (user.getAccountType() == 1) {
            accountType.setText("Organizer");
        }

        db.GetEventsFromUser(user, events -> {
            ArrayList<String> tempArray = new ArrayList<>();
            for (int i = 0; i < events.size(); i++) {
                Event tempEvent = events.get(i);
                tempArray.add(tempEvent.getName());
            }
            adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    tempArray
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view =super.getView(position, convertView, parent);
                    TextView textView=(TextView) view.findViewById(android.R.id.text1);
                    textView.setTextColor(Color.WHITE);
                    return view;
                }
            };
            allEventsUser.setAdapter(adapter);
        });
    }

    public void setUpListeners() {
        /**
         * A back button to go back to all the users
         */
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
