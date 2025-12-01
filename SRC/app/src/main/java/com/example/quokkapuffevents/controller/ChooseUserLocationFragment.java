package com.example.quokkapuffevents.controller;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChooseUserLocationFragment extends Fragment implements OnMapReadyCallback {

    private User user;

    private Database db;
    private GoogleMap mMap;
    private EditText editTextLocation;
    private Button buttonSearch;
    private Button confirmButton;
    private Button goBackToCreateEventBtn;

    private Double lat;
    private Double lng;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_user_location_fragment, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        confirmButton = view.findViewById(R.id.confirm_button);
        goBackToCreateEventBtn = view.findViewById(R.id.goBackToCreateEventBtn);
        db = Database.getInstance();


        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);


        // Search Button Click
        buttonSearch.setOnClickListener(v -> {
            String location = editTextLocation.getText().toString();
            if (!location.isEmpty()) {
                searchLocation(location);
            }

        });

        confirmButton.setOnClickListener(v->{
            //TODO: save user location / address

            Toast.makeText(requireContext(), "Loaction has been changed.", Toast.LENGTH_SHORT).show();

            db.SaveUser(user);

            ChangeProfileSettings frag = new ChangeProfileSettings();
            frag.setUser(user);
            ((DashboardActivity) getActivity()).replaceFragment(frag);

        });

        goBackToCreateEventBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Location has not been saved.", Toast.LENGTH_SHORT).show();
            ChangeProfileSettings frag = new ChangeProfileSettings();
            frag.setUser(user);
            ((DashboardActivity) getActivity()).replaceFragment(frag);

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // TODO: Get event, get lat and lng, set the marker there
        // Default location = Edmonton
        LatLng defaultLocation = new LatLng(53.5461, -113.4938);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
    }

    private void searchLocation(String locationName) {

        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocationName(locationName, 1);

                if (addressList == null || addressList.isEmpty()) {
                    Log.e("SEARCH", "No results found for: " + locationName);
                    return;
                }

                Address address = addressList.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();

                user.setLat(lat);
                user.setLng(lng);

                Log.d("SEARCH", "lat=" + lat + ", lng=" + lng);

                LatLng location = new LatLng(lat, lng);

                // Update map on UI thread
                requireActivity().runOnUiThread(() -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(location).title(locationName));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void SetUser(User u) {
        this.user = u;
    }
}

