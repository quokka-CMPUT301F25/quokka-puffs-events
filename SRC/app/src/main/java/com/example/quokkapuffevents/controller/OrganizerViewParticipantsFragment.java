package com.example.quokkapuffevents.controller;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.AdminUserFragAdapter;
import com.example.quokkapuffevents.view.OrgViewParticipantsFragAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private Button csvCreateBtn;

    ListView listView;
    private OrgViewParticipantsFragAdapter adapter;
    private ArrayList<User> userList = new ArrayList<>();

    public void SetEvent(Event event) {
        this.event = event;
    }


    /**
     * Sets up the view, listview/adapters and the default information is displayed
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
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

    /**
     * Sets up functionality for interactables
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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
        csvCreateBtn = view.findViewById(R.id.createFinalParticipantsCSV);

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
        csvCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED CSV BTN");
                createCSV();
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

    /**
     * This method grabs the users from the event and creates an arraylist of the ID's of users who are 'Accepted' into the event.
     * It then sends the arraylist of id's to the writeCSV method
     */
    public void createCSV() {

        ArrayList<String> userIDs = new ArrayList<>();

        for (Map.Entry<String, String> entry: event.getEventUsers().entrySet()) {
            if (entry.getValue().equals("Accepted")) {
                userIDs.add(entry.getKey());
            }
        }

        ArrayList<User> users = new ArrayList<>();
        AtomicInteger remaining = new AtomicInteger(userIDs.size());

        for (String id : userIDs) {
            db.GetUser(id, user -> {
                users.add(user);

                if (remaining.decrementAndGet() == 0) {
                    writeCSV(users);
                }
            });
        }

    }

    /**
     * This method grabs from a list of users provided and writes their
     *  firstname, lastname, email, phone number
     *  in a csv format and is then saved into their files.
     * @param users provided by {@link #createCSV()}
     */
    public void writeCSV(List<User> users) {

        File file = new File(getContext().getExternalFilesDir(null), event.getName() + "_accepted_participants.csv");


        try {

            FileWriter output = new FileWriter(file);
            CSVWriter writer = new CSVWriter(output);

            String[] header = {"Username", "First Name", "Last Name", "Email", "Phone Number"};
            writer.writeNext(header);

            for(User u: users) {

                String[] data = {u.getUserName(), u.getFirstName(), u.getLastName(), u.getEmail(),u.getPhoneNumber()};
                writer.writeNext(data);

            }

            Toast.makeText(
                    getContext(),
                    "CSV exported successfully to: " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

            writer.close();

        } catch (IOException e) {

            Toast.makeText(
                    getContext(),
                    "Error exporting CSV: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            throw new RuntimeException(e);

        }

    }
 }
