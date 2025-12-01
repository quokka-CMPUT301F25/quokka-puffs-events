package com.example.quokkapuffevents.controller;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChooseEventLocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText editTextLocation;
    private Button buttonSearch;
    private Button confirmButton;
    private Button goBackToCreateEventBtn;
    private SeekBar radiusSeekBar;

    private Switch switchGeolock;
    private TextView radiusLabel;
    private Circle circle;
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
        radiusSeekBar = view.findViewById(R.id.radiusSeekBar);
        radiusLabel = view.findViewById(R.id.radiusLabel);
        switchGeolock = view.findViewById(R.id.switchGeolock);
        goBackToCreateEventBtn = view.findViewById(R.id.goBackToCreateEventBtn);


        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        int STEP = 1; // km

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return; // only respond to real user movement

                // SNAP to nearest step
                int snappedValue = Math.round(progress / STEP) * STEP;
                seekBar.setProgress(snappedValue);

                // Update Label TEXT → this was missing!
                radiusLabel.setText(snappedValue + " km");

                // MOVE label under the slider thumb
                int thumbPos = seekBar.getThumb().getBounds().centerX();
                radiusLabel.setX(seekBar.getX() + thumbPos - radiusLabel.getWidth() / 2);

                // Update map circle
                if (circle != null) {
                    circle.setRadius(snappedValue * 1000); // meters
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

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
            if(switchGeolock.isChecked()) {
                frag.setLockRadius(radiusSeekBar.getProgress());

            } else {
                frag.setLockRadius(-1);
            }
            ((DashboardActivity) getActivity()).replaceFragment(frag);


        });

        goBackToCreateEventBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Loaction has not been set.", Toast.LENGTH_SHORT).show();
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

                Log.d("SEARCH", "lat=" + lat + ", lng=" + lng);

                LatLng location = new LatLng(lat, lng);

                // Update map on UI thread
                requireActivity().runOnUiThread(() -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(location).title(locationName));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                    circle = mMap.addCircle(new CircleOptions()
                            .center(location)
                            .radius(0)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x220000FF));
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void setFrag(EventCreateFragment frag) {
        this.frag = frag;
    }
}
