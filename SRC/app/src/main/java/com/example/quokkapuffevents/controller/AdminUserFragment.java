package com.example.quokkapuffevents.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.AdminEventFragAdapter;
import com.example.quokkapuffevents.view.AdminUserFragAdapter;

import java.util.ArrayList;

public class AdminUserFragment extends Fragment {

    ListView listView;
    private AdminUserFragAdapter adapter;
    private Database db;
    private ArrayList<User> userList = new ArrayList<>();

    public AdminUserFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View userFragmentView = inflater.inflate(R.layout.frag_admin_users, container, false);

        db = Database.getInstance();

        listView = userFragmentView.findViewById(R.id.adminUsersListView);

        adapter = new AdminUserFragAdapter(requireContext(), userList);
        listView.setAdapter(adapter);

        db.ListUsers( users -> {
            // refresh adapter
            userList.clear();
            userList.addAll(users);
            adapter.notifyDataSetChanged();
        });

        return userFragmentView;
    }
}
