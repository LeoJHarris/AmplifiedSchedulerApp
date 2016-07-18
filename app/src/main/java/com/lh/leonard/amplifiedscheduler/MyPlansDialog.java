package com.lh.leonard.amplifiedscheduler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPlansDialog extends AppCompatActivity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    String objectId;
    Person person;
    BackendlessCollection<Slot> slots;
    SpannableString content;
    ProgressBar progressBar;
    Plan event;
    ProgressDialog ringProgressDialog;
    AutoResizeTextView textViewNote;
    AlertDialog dialog;
    // Button buttonMyPlanCancelSlot;
    private Menu optionsMenu;
    private View rootView;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    boolean noteSet = false;
    boolean locationSet = false;
    LatLng latLng;
    boolean timeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_plans_dialog);
        setTitle("fetching plan ...");
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");

        textViewNote = (AutoResizeTextView) findViewById(R.id.textViewPlanNote);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewPlanDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewMyPlanLocation);

        textViewDateAndTime.setTypeface(RobotoCondensedLight);
        textViewLocation.setTypeface(RobotoCondensedLight);
        textViewNote.setTypeface(RobotoCondensedLight);

        person = (Person) userLoggedIn.getProperty("persons");

        this.rootView = findViewById(R.id.map_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.just_map);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        googleMap = mySupportMapFragment.getMap();
        new LoadMyContacts().execute();
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

    private String getDateFormat(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("E d MMM");
        return sdf.format(c.getTime());
    }

    private String getYearFormat(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(c.getTime());
    }

    private String getTimeFormat(Calendar c) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa");
        return sdf.format(c.getTime());
    }


    private class LoadMyContacts extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {

            Bundle data = getIntent().getExtras();
            objectId = data.getString("objectId");
            event = Backendless.Data.of(Plan.class).findById(objectId);

            latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
            return (String) event.getLocation().getMetadata("address");

        }

        @Override
        protected void onPostExecute(String addresses) {

            setTitle(event.getSubject());

            textViewLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent mapIntent = new Intent(MyPlansDialog.this, JustMapActivity.class);

                    mapIntent.putExtra("lat", event.getLocation().getLatitude());
                    mapIntent.putExtra("long", event.getLocation().getLongitude());
                    mapIntent.putExtra("subject", event.getSubject());
                    startActivity(mapIntent);
                }
            });


            if (event.getStartCalendar() != null) {

                if (event.getStartCalendar().equals(event.getEndCalendar())) {
                    textViewDateAndTime.setText("When: " + getDateFormat(event.getStartCalendar()) + " at "
                            + getTimeFormat(event.getStartCalendar()) + " to " +
                            getTimeFormat(event.getEndCalendar()) + " " + getYearFormat(event.getEndCalendar()));
                } else {
                    textViewDateAndTime.setText("When: " + getDateFormat(event.getStartCalendar()) + " at "
                            + getTimeFormat(event.getStartCalendar()) + " to " +
                            getDateFormat(event.getEndCalendar()) + " " + getTimeFormat(event.getEndCalendar()) +
                            " " + getYearFormat(event.getEndCalendar()));
                }

            }

                textViewNote.setText(event.getNote());
                noteSet = true;


            if (event.getLocation() != null) {
                content = new SpannableString("Where: " + event.getLocation().getMetadata("address"));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                textViewLocation.setText(content);
                locationSet = true;
            }
            new GeocoderTask().execute();
        }
    }

    private void displayConfirmationDelete() {
        dialog = new AlertDialog.Builder(MyPlansDialog.this)
                .setTitle("Discard")
                .setMessage("Discard this Plan")
                .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        ringProgressDialog = ProgressDialog.show(MyPlansDialog.this, "Please wait ...", "Discarding Plan: " + event.getSubject() + " ...", true);
                        ringProgressDialog.setCancelable(false);
                        new CancelEvent().execute();
                    }
                })
                .setNegativeButton("KEEP", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

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
            progressBar = (ProgressBar) findViewById(R.id.progressBarMyPlanDialog);
            progressBar.setVisibility(View.GONE);


                textViewLocation.setVisibility(View.VISIBLE);
                rootView.setVisibility(View.VISIBLE);
                textViewDateAndTime.setVisibility(View.VISIBLE);
                textViewNote.setVisibility(View.VISIBLE);

        }
    }

    private class CancelEvent extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Map<String, String> args = new HashMap<>();
            args.put("id", "deleteplan");

            args.put("plan", event.getObjectId());

            Backendless.Events.dispatch("ManageEvent", args, new AsyncCallback<Map>() {
                @Override
                public void handleResponse(Map map) {
                    dialog.dismiss();
                    onBackPressed();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    dialog.dismiss();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_discard_event:
                displayConfirmationDelete();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plan, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey! Check out this free event/personal planner app: https://play.google.com/store/apps/details?id=com.lh.leonard.amplifiedscheduler");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void invalidateOptionsMenu() {

        super.invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyPlans.class);
        startActivity(intent);
        finish();
    }
}