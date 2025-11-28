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
    TextView eventCategory;
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

    // Setter
    public void setUser(User user) {

        this.user = user;

    }

    /**
     * Finds each view and initializes each one
     * @param view The view that is accessed to find each view
     */
    public void initializeViews(View view) {

        username = view.findViewById(R.id.usernameTextView);
        firstAndLastName = view.findViewById(R.id.firstLastNameTextView);
        email = view.findViewById(R.id.emailTextView);
        userid = view.findViewById(R.id.idTextView);
        accountType = view.findViewById(R.id.accountTypeTextView);
        eventCategory = view.findViewById(R.id.eventCategory);
        phoneNumber = view.findViewById(R.id.phoneNumberTextView);
        allEventsUser = view.findViewById(R.id.eventsListView);
        goBackBtn = view.findViewById(R.id.goBackBtn);

    }

    /**
     * Using the initialized views set each one to display each detail of the user
     */
    public void displayInfo() {

        firstAndLastName.setText(user.getFirstName() + " " + user.getLastName());
        email.setText(user.getEmail());
        username.setText(user.getUserName());
        userid.setText(user.getId());
        phoneNumber.setText(user.getPhoneNumber().toString());

        if (user.getAccountType() == -1) {
            accountType.setText("Admin");
            eventCategory.setText("");
        }
        if (user.getAccountType() == 0) {
            accountType.setText("Entrant");
            eventCategory.setText("Joined Events:");
        }
        if (user.getAccountType() == 1) {
            accountType.setText("Organizer");
            eventCategory.setText("Events Created:");
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

                    textView.setTextColor(Color.BLACK);

                    return view;
                }
            };

            allEventsUser.setAdapter(adapter);
        });

    }

    /**
     * A back button to go back to all the users
     */
    public void setUpListeners() {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().popBackStack();

            }
        });

    }

}
