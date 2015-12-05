package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSlot extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, CalendarDatePickerDialogFragment.OnDateSetListener,
        GoogleApiClient.ConnectionCallbacks,
        TimePickerDialogFragment.TimePickerDialogHandler {

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoResizeTextView mAddressTextView;
    private AutoResizeTextView mAttTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;

    Calendar now;
    Boolean allDayEvent = false;
    Place place;
    private AutoResizeTextView textViewStartTime;
    private Menu optionsMenu;
    int datePickerSelected = 0;
    Boolean sendSMS = true;
    Boolean subjectSet = false;
    Boolean contactsAdded = false;

    List<Person> myContactsPersonsList;
    BackendlessCollection<Person> myContactPersons;
    BackendlessCollection<Person> persons;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();

    int timeSelected = 0;
    Person personLoggedIn;
    LatLng latLng;
    Button recipientsForSlotBtn;
    public Button buttonSendSlot;
    EditText editTextNumberAttendeesAvaliable;
    EditText slotSubjectEditText;
    EditText slotMessageEditText;
    Switch aSwitch;
    CheckBox allDaySwitch;
    Calendar startCalendar;
    Calendar endCalendar;
    AutoResizeTextView textViewEndTime;
    //  ImageButton btnGetLocationGeoPoint;

    CharSequence[] testArray;
    ArrayList<Integer> mSelectedItems;
    Drawable tickIconDraw;
    ArrayList<Person> addedContactsForSlot;

    GeoPoint eventLocation;
    private Toolbar toolbar;
    String subject;
    String message;
    String locationString;
    Integer numberAttendeesAvaliable = 0;
    String my_var;
    AutoResizeTextView textViewEndDate;
    AutoResizeTextView textViewStartDate;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_slot);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(CreateSlot.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAddressTextView = (AutoResizeTextView) findViewById(R.id.address);
        mAttTextView = (AutoResizeTextView) findViewById(R.id.att);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                null, null); //BOUNDS_MOUNTAIN_VIEW
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        tickIconDraw = getResources().getDrawable(R.drawable.ic_tick);

        aSwitch = (Switch) findViewById(R.id.switchAutomatedSMS);
        allDaySwitch = (CheckBox) findViewById(R.id.checkboxAllDay);
        editTextNumberAttendeesAvaliable = (EditText) findViewById(R.id.numberPickerAttendees);
        recipientsForSlotBtn = (Button) findViewById(R.id.recipientsForSlot);
        textViewEndTime = (AutoResizeTextView) findViewById(R.id.textViewEndTime);
        slotSubjectEditText = (EditText) findViewById(R.id.editSlotSubject);
        slotMessageEditText = (EditText) findViewById(R.id.editTextSlotMessage);
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        buttonSendSlot = (Button) findViewById(R.id.buttonSendSlot);
        textViewStartDate = (AutoResizeTextView) findViewById(R.id.textViewStartDate);
        // btnGetLocationGeoPoint = (ImageButton) findViewById(R.id.btnGetLocationGeoPoint);
        textViewEndDate = (AutoResizeTextView) findViewById(R.id.textViewEndDate);
        textViewStartTime = (AutoResizeTextView) findViewById(R.id.textViewStartTime);


        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        // final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "RobotoCondensed-Light.ttf");

        textViewEndDate.setTypeface(RobotoCondensedLight);
        recipientsForSlotBtn.setTypeface(RobotoCondensedLight);
        textViewStartTime.setTypeface(RobotoCondensedLight);
        slotSubjectEditText.setTypeface(RobotoCondensedLight);
        slotMessageEditText.setTypeface(RobotoCondensedLight);
        mAddressTextView.setTypeface(RobotoCondensedLight);
        mAttTextView.setTypeface(RobotoCondensedLight);
        mAutocompleteTextView.setTypeface(RobotoCondensedLight);
        buttonSendSlot.setTypeface(RobotoCondensedLight);
        aSwitch.setTypeface(RobotoCondensedLight);
        allDaySwitch.setTypeface(RobotoCondensedLight);
        aSwitch.setChecked(true);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        if (userLoggedIn.getProperty("persons") != null) {
            personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        } else {
            BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
            Backendless.Data.mapTableToClass("Person", Person.class);
            Backendless.Persistence.mapTableToClass("Person", Person.class);
            personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        }


        /********* display current time on screen Start ********/
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_YEAR, 1);

        //SET CURRENT TIME AND DATES AND ADD 1 TO END CAL
        textViewStartDate.setText(getDateFormat(startCalendar));

        textViewEndDate.setText(getDateFormat(endCalendar));

        textViewStartTime.setText(getTimeFormat(startCalendar));

        textViewEndTime.setText(getTimeFormat(endCalendar));


        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelected = 1;
                TimePickerBuilder tpb = new TimePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light);
                tpb.show();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelected = 2;
                TimePickerBuilder tpb = new TimePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light);
                tpb.show();
            }
        });

        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerSelected = 1;
                FragmentManager fm = getSupportFragmentManager();
                now = Calendar.getInstance();

                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(CreateSlot.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));

                calendarDatePickerDialogFragment.setDateRange(new MonthAdapter.CalendarDay(now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH) - 1, now.get(Calendar.DAY_OF_MONTH)), null);

                calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerSelected = 2;
                FragmentManager fm = getSupportFragmentManager();
                now = Calendar.getInstance();

                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(CreateSlot.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));

                calendarDatePickerDialogFragment.setDateRange(new MonthAdapter.CalendarDay(now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH) - 1, now.get(Calendar.DAY_OF_MONTH)), null);

                calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        new GetContactsThread().execute();

        /**
         * Unset the var whenever the user types. Validation will
         * then fail. This is how we enforce selecting from the list.
         */
        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_var = null;
                mAutocompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recipientsForSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSlot.this);

                mSelectedItems = new ArrayList<>(); // TODO temporary

                // set the dialog title
                builder.setTitle("Invite contacts for event")

                        // specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive call backs when items are selected
                        // R.array.choices were set in the resources res/values/strings.xml
                        .setMultiChoiceItems(testArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                //TODO set so when dialog opens again it has all contacts ticked that were intially ticked mSelectedItems has the list

                                if (isChecked) {
                                    // if the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // else if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }

                                // you can also add other codes here,
                                // for example a tool tip that gives user an idea of what he is selecting
                                // showToast("Just an example description.");
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // user clicked OK, so save the mSelectedItems results somewhere
                        // here we are trying to retrieve the selected items indices
                        String selectedIndex = "";
                        for (Integer i : mSelectedItems) {
                            selectedIndex += i + ", ";
                        }

                        addedContactsForSlot = new ArrayList<Person>();

                        String[] selectedContacts = selectedIndex.split(", ");

                        for (int j = 0; j < selectedContacts.length; j++) {

                            if (selectedContacts[j] != " " && selectedContacts[j] != "") {

                                addedContactsForSlot.add(myContactsPersonsList.get(Integer.parseInt(selectedContacts[j].replaceAll("\\s+", ""))));
                            }
                        }
                        if (!(addedContactsForSlot.isEmpty())) {
                            recipientsForSlotBtn.setText("Contacts added");
                            recipientsForSlotBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                            recipientsForSlotBtn.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));

                            contactsAdded = true;

                            Toast.makeText(CreateSlot.this, "Contacts Added", Toast.LENGTH_SHORT).show();

                            if (subjectSet) {
                                buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                                buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                            }

                        } else {
                            Toast.makeText(CreateSlot.this, "No Contacts Added", Toast.LENGTH_LONG).show();
                            contactsAdded = false;
                            recipientsForSlotBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.red));
                        }
                    }
                })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        }).show();
            }
        });

//        btnGetLocationGeoPoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(CreateSlot.this, MapsActivity.class), 1000);
//            }
//        });

        //TODO Google maps

        buttonSendSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addedContactsForSlot != null) {
                    if (!(addedContactsForSlot.isEmpty())) {

                        slotSubjectEditText.getText().toString();
                        if (editTextNumberAttendeesAvaliable.getText().toString().equals("")) {
                            numberAttendeesAvaliable = 0;
                        } else {
                            numberAttendeesAvaliable = Integer.parseInt(editTextNumberAttendeesAvaliable.getText().toString());
                        }
                        subject = slotSubjectEditText.getText().toString();
                        message = slotMessageEditText.getText().toString();
                        locationString = mAddressTextView.getText().toString();
                        //TODO Set Slot Ready to send with tick

                        String emptys = "";

                        if (subject.trim().equals("")
                                || locationString.equals("Please Set Place") || my_var == null) {

                            if (subject.trim().equals("")) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Title";
                                } else {
                                    emptys += ", Title";
                                }
                            }
                            if (my_var == null) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Place";
                                } else {
                                    emptys += ", Place";
                                }
                            }
                            Toast.makeText(getApplicationContext(), "Please fill: " + emptys, Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "Sending event", Toast.LENGTH_LONG).show();
                            new ParseURL().execute();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please invite contacts for event", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please invite contacts for event", Toast.LENGTH_LONG).show();
                }
            }
        });

        slotSubjectEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = slotSubjectEditText.getText().toString();
                    if ((!(string.equals("")))) {
                        slotSubjectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);

                        if (contactsAdded) {
                            buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                        }

                        slotSubjectEditText.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                        subjectSet = true;

                    } else {
                        slotSubjectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.red));
                        subjectSet = false;
                    }
                }
            }
        });

        editTextNumberAttendeesAvaliable.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = String.valueOf(editTextNumberAttendeesAvaliable.getText().toString());
                    if ((!(string.equals("")))) {
                        editTextNumberAttendeesAvaliable.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                        editTextNumberAttendeesAvaliable.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                    } else {
                        editTextNumberAttendeesAvaliable.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }
            }
        });
        slotMessageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = slotMessageEditText.getText().toString();
                    if ((!(string.equals("")))) {
                        slotMessageEditText.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                        slotMessageEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                    } else {
                        slotMessageEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }
            }
        });
        if (allDaySwitch != null) {
            allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        textViewEndTime.setVisibility(View.GONE);
                        textViewStartTime.setVisibility(View.GONE);
                        allDayEvent = true;
                    } else {
                        textViewEndTime.setVisibility(View.VISIBLE);
                        textViewStartTime.setVisibility(View.VISIBLE);
                        allDayEvent = false;
                    }
                }
            });
        }
//TODO ADDS NOTES FOR HOST FOR PIVATE USe
        if (aSwitch != null) {
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        sendSMS = true;
                    } else {
                        sendSMS = false;
                    }
                }
            });
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //Set startCalander year, month, day
        if (datePickerSelected == 1) {

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (startCalendar.after(endCalendar)) {
                endCalendar.set(startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE),
                        startCalendar.get(Calendar.SECOND));
                textViewEndDate.setText(getDateFormat(endCalendar));
                textViewEndTime.setText(getTimeFormat(endCalendar));
            }

            textViewStartDate.setText(getDateFormat(startCalendar));
        }
        //Set endCalander year, month, day
        else if (datePickerSelected == 2) {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (endCalendar.before(startCalendar)) {
                startCalendar.set(endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE),
                        endCalendar.get(Calendar.SECOND));
                textViewStartDate.setText(getDateFormat(startCalendar));
                textViewStartTime.setText(getTimeFormat(startCalendar));
            }
            textViewEndDate.setText(getDateFormat(endCalendar));
        }
    }

    private String getDateFormat(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("E , d MMM , yyyy");
        return sdf.format(c.getTime());
    }

    private String getTimeFormat(Calendar c) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa");
        return sdf.format(c.getTime());

    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {

        if (timeSelected == 1) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCalendar.set(Calendar.MINUTE, minute);

            if (startCalendar.after(endCalendar)) {
                endCalendar.set(startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE),
                        startCalendar.get(Calendar.SECOND));
                textViewEndDate.setText(getDateFormat(endCalendar));
                textViewEndTime.setText(getTimeFormat(endCalendar));
            }

            textViewStartTime.setText(getTimeFormat(startCalendar));
        } else if (timeSelected == 2) {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endCalendar.set(Calendar.MINUTE, minute);

            if (endCalendar.before(startCalendar)) {
                startCalendar.set(endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE),
                        endCalendar.get(Calendar.SECOND));
                textViewStartDate.setText(getDateFormat(startCalendar));
                textViewStartTime.setText(getTimeFormat(startCalendar));
            }
            textViewEndTime.setText(getTimeFormat(endCalendar));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            double geoLocation[];

            String location = data.getStringExtra("location");
            geoLocation = data.getDoubleArrayExtra("geolocation");

            eventLocation = new GeoPoint(geoLocation[0], geoLocation[1]);
            Map<String, Object> locationMap = new HashMap<>();
            locationMap.put("location", location);
            eventLocation.setMetadata(locationMap);

        }
    }

    private class ParseURL extends AsyncTask<Void, Integer, Void> {

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

            HashMap<String, Object> hashMapEvent = new HashMap<>();
            hashMapEvent.put("subject", subject);
            hashMapEvent.put("message", message);

            hashMapEvent.put("starttime", startCalendar.getTime());
            hashMapEvent.put("endtime", endCalendar.getTime());
            hashMapEvent.put("attendees", numberAttendeesAvaliable);
            hashMapEvent.put("phone", personLoggedIn.getPhone());
            hashMapEvent.put("host", personLoggedIn.getFullname());
            hashMapEvent.put("loggedinperson", personLoggedIn.getObjectId());
            hashMapEvent.put("alldayevent", allDayEvent); // TODO ADD THIS IN THE BACKENDLESS CONSOLE, and SERVER CODE
            int o = 0;
            for (Person pId : addedContactsForSlot) {

                hashMapEvent.put(String.valueOf(o), pId.getObjectId());
                o++;
            }

            hashMapEvent.put("size", o);

            LatLng latLngPlace = place.getLatLng();

            hashMapEvent.put("lat", latLngPlace.latitude);
            hashMapEvent.put("long", latLngPlace.longitude);

            hashMapEvent.put("location", place.getAddress());

            Backendless.Events.dispatch("CreateEvent", hashMapEvent, new AsyncCallback<Map>() {
                @Override
                public void handleResponse(Map map) {
                    Toast.makeText(getApplicationContext(), "Event Sent", Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(getApplicationContext(), "Error: Could not create event", Toast.LENGTH_LONG).show();
                }
            });

            //TODO reused code below here from multiple class Should make a class and method maybe

            // For all the contacts add to their pending response slot
            for (Person pId : addedContactsForSlot) {

                if (sendSMS) {
                    sendsmss(pId.getPhone(), message, subject, startCalendar.getTime().getTime());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            recipientsForSlotBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            slotSubjectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            slotMessageEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            addedContactsForSlot.clear();
            mAddressTextView.setText("Please Set Place");
            mAutocompleteTextView.setText("");
            slotSubjectEditText.setText("");
            slotSubjectEditText.setText("");
            slotMessageEditText.setText("");
        }
    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String message, String subject, Long startDate) {

        String fullnameLoggedin = personLoggedIn.getFullname();
        String dots = "";

        if (!message.trim().equals("")) {
            int lengthToSubString;
            int lengthMessage = message.length();
            if (lengthMessage <= 150) {
                lengthToSubString = lengthMessage;

            } else {
                lengthToSubString = 150;
                dots = "...";
            }
            String messageSubString = message.substring(0, lengthToSubString);
            messageSubString = "Amplified Scheduler: Invited Event from " + fullnameLoggedin +
                    ". " + subject + messageSubString + dots + " When: " + startDate;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
        } else {
            String messageSubString = "Amplified Scheduler: Invited Event. Host: " + fullnameLoggedin +
                    ". " + subject + " no message included " + "when: " + startDate;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
        }
    }

    private class GetContactsThread extends AsyncTask<Void, Integer, Void> {

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


            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[contacts]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            myContactPersons = Backendless.Data.of(Person.class).find(dataQuery);

            myContactsPersonsList = myContactPersons.getData();

            testArray = new String[myContactsPersonsList.size()];
            int i = 0;
            for (Person pers : myContactsPersonsList) {
                testArray[i] = pers.fullname;
                i++;
            }
            return null;
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            my_var = mPlaceArrayAdapter.getItem(position).toString();
            mAutocompleteTextView.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
            mAutocompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            place = places.get(0);
            CharSequence attributions = places.getAttributions();

            mAddressTextView.setText(Html.fromHtml("Selected Place: " + place.getAddress() + " " + place.getPhoneNumber()));
            if (attributions != null) {
                mAttTextView.setVisibility(View.VISIBLE);
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NavDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);


        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this free event making app: https://play.google.com/store/apps/details?id=com.lh.leonard.amplifiedscheduler");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
    }
}