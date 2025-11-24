package com.example.quokkapuffevents.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class AdminEventDetailsFrag extends Fragment {

    private Database db;
    private Event event;

    TextView eventName;
    TextView organizer;
    TextView maxEntrants;
    TextView startEndDate;
    TextView description;
    ListView allUsersEvent;
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

        View view = inflater.inflate(R.layout.event_details_fragment, container, false);
        initializeViews(view);
        displayInfo(view);
        setUpListeners(view);
        return view;

    }

    public void setEvent(Event event) {

        this.event = event;

    }

    public void initializeViews(View view) {

        eventName = view.findViewById(R.id.eventName);
        organizer = view.findViewById(R.id.organizerName);
        maxEntrants = view.findViewById(R.id.maxEntrants);
        startEndDate = view.findViewById(R.id.startAndEndDate);
        description = view.findViewById(R.id.description);
        allUsersEvent = view.findViewById(R.id.allUsersEvent);
        goBackBtn = view.findViewById(R.id.goBackBtn);

    }

    public void displayInfo(View view) {

        eventName.setText(event.getName());
        db.GetUser(event.getOrg(), user -> {
            organizer.setText(user.getFirstName());
        });

        maxEntrants.setText(String.valueOf(event.getMaxNumWaitlist()));
        if (event.getMaxNumWaitlist() == -1) {
            maxEntrants.setText("No limit");
        }


        String pattern = "MMM. dd, YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date startDate = event.getStartDate();
        Date endDate = event.getEventDate();

        startEndDate.setText(sdf.format(startDate) + " - " + sdf.format(startDate));

        description.setText(event.getId());
        description.setMovementMethod(new ScrollingMovementMethod());

        Map<String, String> userInEvent = event.getEventUsers();
        ArrayList<String> tempArray = new ArrayList<>();
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

        allUsersEvent.setAdapter(adapter);
        for (String key : userInEvent.keySet()) {
            db.GetUser(key, user -> {
                tempArray.add(user.getFirstName());
                adapter.notifyDataSetChanged();
            });
        }

    }

    public void setUpListeners(View view) {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().popBackStack();

            }
        });

    }


}
