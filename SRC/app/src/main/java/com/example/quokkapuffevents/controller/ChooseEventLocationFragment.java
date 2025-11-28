package com.example.quokkapuffevents.controller;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

import com.example.quokkapuffevents.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;

public class ChooseEventLocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText editTextLocation;
    private Button buttonSearch;
    private Button confirmButton;
    private EventCreateFragment frag;
    private Double lat;
    private Double lng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_event_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        confirmButton = view.findViewById(R.id.confirm_button);


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
            //TODO: check if the switch is on, if so check the radius
            // For now radius will default to -1
            frag.setEventLocation(lat, lng);
            frag.setLockRadius(-1);
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
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear(); // remove old marker
                mMap.addMarker(new MarkerOptions().position(location).title(locationName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFrag(EventCreateFragment frag) {
        this.frag = frag;
    }
}
