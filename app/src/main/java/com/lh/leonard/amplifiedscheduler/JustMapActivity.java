package com.lh.leonard.amplifiedscheduler;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class JustMapActivity extends FragmentActivity {

    private View rootView;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.just_map_activity);

        this.rootView = findViewById(R.id.map_view);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.just_map);
        googleMap = mySupportMapFragment.getMap();

        this.getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            //TODO CHECKS
            latLng = new LatLng(extras.getDouble("lat"), extras.getDouble("long"));
            subject = extras.getString("subject");
        }
        new GeocoderTask().execute();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect rect = new Rect();
        rootView.getHitRect(rect);
        if (!rect.contains((int) event.getX(), (int) event.getY())) {
            setFinishOnTouchOutside(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            finish();
        }
        return true;
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<Void, Integer, List<Address>> {

        @Override
        protected List<Address> doInBackground(Void... result) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            for (int i = 0; i < addresses.size(); i++) {
                Address address = (Address) addresses.get(i);
                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                Marker location = googleMap.addMarker(new MarkerOptions()
                        .position(latLng).title(addressText)
                        .draggable(true));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng) // Center Set
                        .zoom(11.0f)                // Zoom
                        .bearing(0)                // Orientation of the camera to east
                        .tilt(30)                   // Tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }
}
