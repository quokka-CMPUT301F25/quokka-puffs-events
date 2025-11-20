package com.example.quokkapuffevents.controller;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.AdminUserFragAdapter;
import com.example.quokkapuffevents.view.OrgViewParticipantsFragAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrganizerViewParticipantsFragment extends Fragment {

//    TYPES of filters: "Invited", "Waiting", "Canceled"

    private Database db;
    private Event event;

    private TextView viewType;
    private TextView waitingListAmt;

    private Button canceledBtn;
    private Button waitlistBtn;
    private Button chosenBtn;
    private Button finalParticipantsBtn;
    private Button redrawBtn;
    private Button backBtn;

    ListView listView;
    private OrgViewParticipantsFragAdapter adapter;
    private ArrayList<User> userList = new ArrayList<>();

    public void SetEvent(Event event) {
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View userFragmentView = inflater.inflate(R.layout.organizer_view_participants, container, false);

        db = Database.getInstance();

        listView = userFragmentView.findViewById(R.id.orgParticipantListView);

        adapter = new OrgViewParticipantsFragAdapter(requireContext(), userList);
        listView.setAdapter(adapter);

        Map<String, String> eventUsers = event.getEventUsers();
        userList.clear();
        AtomicInteger pending = new AtomicInteger(0);

        for (Map.Entry<String, String> entry : eventUsers.entrySet()) {

            if (entry.getValue().equals("Cancelled")) {

                pending.incrementAndGet();

                db.GetUser(entry.getKey(), user -> {

                    if (user != null) {
                        userList.add(user);
                    }

                    if (pending.decrementAndGet() == 0) {

                        if (userList.isEmpty()) {
                            System.out.println("USERLIST IS EMPTY");
                        } else {
                            for (User u : userList) {
                                System.out.println(u.getFirstName());
                            }

                        }
                        adapter.notifyDataSetChanged();


                    }

                });

            }
        }




        return userFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        SetUpListeners(view);
    }

    /**
     * Initializes all UI components and references.
     *
     * @param view The root view of the fragment.
     */
    public void initialize(View view) {


        viewType = view.findViewById(R.id.viewParticipantsTypeText);
        waitingListAmt = view.findViewById(R.id.waitingListAmt);
//        Buttons
        canceledBtn = view.findViewById(R.id.viewCanceledParticipantsBtn);
        waitlistBtn = view.findViewById(R.id.viewWaitingParticipantsBtn);
        chosenBtn = view.findViewById(R.id.viewChosenParticipantsBtn);
        redrawBtn = view.findViewById(R.id.orgRedrawEntrantBtn);
        backBtn = view.findViewById(R.id.orgViewParticipantsBackToDashboardBtn);
        finalParticipantsBtn = view.findViewById(R.id.viewFinalParticipantsBtn);

    }

    /**
     * Sets up button listeners for filtering, navigation, participant detail view,
     * and redrawing users if event spot opens.
     *
     * @param view The root view of the fragment.
     */
    public void SetUpListeners(View view) {

        canceledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewType("Cancelled", view);
            }
        });

        waitlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewType("Waiting", view);
            }
        });

        chosenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewType("Invited", view);
            }
        });

        finalParticipantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewType("Accepted", view);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrganizerEventDetails orgFrag = new OrganizerEventDetails();
                orgFrag.SetEvent(event);
                ((DashboardActivity) getActivity()).replaceFragment(orgFrag);
            }
        });

        redrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.RedrawUsers(event, event.getToBeDrawn() - event.getNumPeopleWaiting());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganizerViewParticipantsInformationFragment orgFrag = new OrganizerViewParticipantsInformationFragment();
                orgFrag.SetEvent(event);
                orgFrag.SetUser(userList.get(position));
                ((DashboardActivity) getActivity()).replaceFragment(orgFrag);
            }
        });


    }


    /**
     * Filters the participant list based on their registration status
     * (e.g. "Invited", "Waiting", "Cancelled", "Accepted").
     * Loads users asynchronously and updates UI accordingly.
     *
     * @param filterType The status to filter by.
     * @param view       The root view of the fragment.
     */
    public void changeViewType(String filterType, View view) {

        Map<String, String> eventUsers = event.getEventUsers();
        userList.clear();
        AtomicInteger pending = new AtomicInteger(0);

        for (Map.Entry<String, String> entry : eventUsers.entrySet()) {

            if (entry.getValue().equals(filterType)) {
//                Async stuff #annoying
                pending.incrementAndGet();

                db.GetUser(entry.getKey(), user -> {
//                    Gett user and add it to the userList

                    if (user != null) {
                        userList.add(user);

                    }

                    if (pending.decrementAndGet() == 0) {

                        if (userList.isEmpty()) {
                            System.out.println("USERLIST IS EMPTY");
                        } else {
                            for (User u : userList) {
                                System.out.println(u.getFirstName());
                            }

                        }

                        adapter.notifyDataSetChanged();

                        //            If waiting, view the amount.
                        if(filterType.equals("Waiting")) {
                            view.findViewById(R.id.amountWaitingListContainer).setVisibility(VISIBLE);
                            view.findViewById(R.id.redrawContainer).setVisibility(GONE);
                            waitingListAmt.setText(String.valueOf(userList.size()));

                        }
                    }

                });

            }
        }

        if ((filterType.equals("Canceled")) && (event.getDrawn() == true)) {
            view.findViewById(R.id.amountWaitingListContainer).setVisibility(GONE);
            view.findViewById(R.id.redrawContainer).setVisibility(VISIBLE);

        }
        else {
            view.findViewById(R.id.amountWaitingListContainer).setVisibility(GONE);
            view.findViewById(R.id.redrawContainer).setVisibility(GONE);

        }

//      Show view type:
        String temp = "View by type: " + filterType;
        viewType.setText(temp);

        adapter.notifyDataSetChanged();
    }
 }
