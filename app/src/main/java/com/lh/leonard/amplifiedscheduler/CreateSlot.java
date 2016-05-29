package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.codetroopers.betterpickers.timezonepicker.TimeZoneInfo;
import com.codetroopers.betterpickers.timezonepicker.TimeZonePickerDialogFragment;
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
import java.util.TimeZone;

public class CreateSlot extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, CalendarDatePickerDialogFragment.OnDateSetListener,
        GoogleApiClient.ConnectionCallbacks, TimeZonePickerDialogFragment.OnTimeZoneSetListener,
        TimePickerDialogFragment.TimePickerDialogHandler {

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoResizeTextView mAttTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;

    Calendar now;
    Boolean allDayEvent = false;
    Place place;
    private AutoResizeTextView textViewStartTime;
    private Menu optionsMenu;
    int datePickerSelected = 0;
    Boolean subjectSet = false;
    Boolean contactsAdded = false;
    AutoResizeTextView timeZone;
    LinearLayout llTimeZone;
    String eventCategory;


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
    EditText editTextNote;
    CheckBox allDaySwitch;
    Calendar startCalendar;
    Calendar endCalendar;
    AutoResizeTextView textViewEndTime;
    //  ImageButton btnGetLocationGeoPoint;

    LinearLayout llnumberPickerAttendees, llrecipientsForSlot, lleditTextSlotMessage;

    private String mRrule;
    Switch switchPlannerEvent;

    CharSequence[] testArray;
    ArrayList<Integer> mSelectedItems;
    ArrayList<Person> addedContactsForSlot;
    AutoResizeTextView labelAllDay;
    GeoPoint eventLocation;
    private Toolbar toolbar;
    String subject;
    String note;
    String message;
    Integer numberAttendeesAvaliable = 0;
    String my_var;
    AutoResizeTextView textViewEndDate;
    AutoResizeTextView textViewStartDate;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_ZONE_PICKER = "timeZonePickerDialogFragment";
    Boolean event = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_slot);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        if (userLoggedIn.getProperty("persons") != null) {
            personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        } else {

        }

        new GetContactsThread().execute();

        mGoogleApiClient = new GoogleApiClient.Builder(CreateSlot.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAttTextView = (AutoResizeTextView) findViewById(R.id.att);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                null, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

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
        timeZone = (AutoResizeTextView) findViewById(R.id.editTextTimeZone);
        llTimeZone = (LinearLayout) findViewById(R.id.llTimeZone);
        editTextNote = (EditText) findViewById(R.id.editTextNotes);
        labelAllDay = (AutoResizeTextView) findViewById(R.id.labelAllDay);
        switchPlannerEvent = (Switch) findViewById(R.id.switchPlannerEvent);
        llrecipientsForSlot = (LinearLayout) findViewById(R.id.llrecipientsForSlot);
        lleditTextSlotMessage = (LinearLayout) findViewById(R.id.lleditTextSlotMessage);

        llnumberPickerAttendees = (LinearLayout) findViewById(R.id.llnumberPickerAttendees);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewEndTime.setTypeface(RobotoCondensedLight);
        editTextNumberAttendeesAvaliable.setTypeface(RobotoCondensedLight);
        timeZone.setTypeface(RobotoCondensedLight);
        textViewStartDate.setTypeface(RobotoCondensedLight);
        labelAllDay.setTypeface(RobotoCondensedLight);
        editTextNote.setTypeface(RobotoCondensedLight);
        textViewEndDate.setTypeface(RobotoCondensedLight);
        recipientsForSlotBtn.setTypeface(RobotoCondensedLight);
        textViewStartTime.setTypeface(RobotoCondensedLight);
        slotSubjectEditText.setTypeface(RobotoCondensedLight);
        slotMessageEditText.setTypeface(RobotoCondensedLight);
        mAttTextView.setTypeface(RobotoCondensedLight);
        mAutocompleteTextView.setTypeface(RobotoCondensedLight);
        buttonSendSlot.setTypeface(RobotoCondensedLight);

        allDaySwitch.setTypeface(RobotoCondensedLight);

        switchPlannerEvent.setTypeface(RobotoCondensedLight);

        switchPlannerEvent.setChecked(true);
        setDates();

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

        timeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                Bundle b = new Bundle();

                Time t = new Time();
                t.setToNow();
                now = Calendar.getInstance();
                b.putLong(TimeZonePickerDialogFragment.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
                b.putString(TimeZonePickerDialogFragment.BUNDLE_TIME_ZONE, getTimeZoneFormat(now));
                // may be more efficient to serialize and pass in EventRecurrence
                b.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);

                TimeZonePickerDialogFragment tzpd = (TimeZonePickerDialogFragment) fm
                        .findFragmentByTag(FRAG_TAG_TIME_ZONE_PICKER);
                if (tzpd != null) {
                    tzpd.dismiss();
                }
                tzpd = new TimeZonePickerDialogFragment();
                tzpd.setArguments(b);
                tzpd.setOnTimeZoneSetListener(CreateSlot.this);
                tzpd.show(fm, FRAG_TAG_TIME_ZONE_PICKER);
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

                mSelectedItems = new ArrayList<>();

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
                            contactsAdded = true;
                            Toast.makeText(CreateSlot.this, "Contacts Added", Toast.LENGTH_SHORT).show();

                            if (subjectSet && my_var != null) {
                                buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_event_ready), null, null, null);
                            }
                        } else {
                            Toast.makeText(CreateSlot.this, "No Contacts Added", Toast.LENGTH_LONG).show();
                            contactsAdded = false;
                            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_send_event), null, null, null);
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

                //event
                if (event) {
                    if (addedContactsForSlot != null) {
                        if (!(addedContactsForSlot.isEmpty())) {
                            if (editTextNumberAttendeesAvaliable.getText().toString().equals("")) {
                                numberAttendeesAvaliable = 0;
                            } else {
                                numberAttendeesAvaliable = Integer.parseInt(editTextNumberAttendeesAvaliable.getText().toString());
                            }
                            subject = slotSubjectEditText.getText().toString();
                            note = editTextNote.getText().toString();
                            message = slotMessageEditText.getText().toString();
                            //TODO Set Slot Ready to send with tick

                            String emptys = "";

                            if (subject.trim().equals("") || my_var == null ) {

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
                // Planner
                else {

                    subject = slotSubjectEditText.getText().toString();
                    note = editTextNote.getText().toString();

                    String emptys = "";

                    if (subject.trim().equals("") || my_var == null) {

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
                        Toast.makeText(getApplicationContext(), "Creating plan", Toast.LENGTH_LONG).show();
                        new ParseURL().execute();
                    }
                }
            }
        });

        mAutocompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (my_var == null) {
                }
            }
        });

        slotSubjectEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = slotSubjectEditText.getText().toString();
                    if ((!(string.equals("")))) {
                        subjectSet = true;
                        if (contactsAdded && my_var != null) {
                            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_event_ready), null, null, null);
                        }
                    } else {
                        subjectSet = false;
                        buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_send_event), null, null, null);
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
                        llTimeZone.setVisibility(View.GONE);
                        allDayEvent = true;
                    } else {
                        textViewEndTime.setVisibility(View.VISIBLE);
                        textViewStartTime.setVisibility(View.VISIBLE);
                        llTimeZone.setVisibility(View.VISIBLE);
                        allDayEvent = false;
                    }
                }
            });
        }

        if (switchPlannerEvent != null) {
            switchPlannerEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (!isChecked) {
                        lleditTextSlotMessage.setVisibility(View.GONE);
                        llnumberPickerAttendees.setVisibility(View.GONE);
                        llrecipientsForSlot.setVisibility(View.GONE);

                        editTextNumberAttendeesAvaliable.setVisibility(View.GONE);
                        switchPlannerEvent.setText("Personal Plan");
                        buttonSendSlot.setText("CREATE PLAN");
                        event = false;
                    } else {
                        lleditTextSlotMessage.setVisibility(View.VISIBLE);
                        llnumberPickerAttendees.setVisibility(View.VISIBLE);
                        llrecipientsForSlot.setVisibility(View.VISIBLE);

                        switchPlannerEvent.setText("Event");
                        editTextNumberAttendeesAvaliable.setVisibility(View.VISIBLE);
                        event = true;
                        buttonSendSlot.setText("SEND EVENT");
                    }
                }
            });
        }

        Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.event_category,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                endCalendar.add(Calendar.HOUR_OF_DAY, 1);
                textViewEndDate.setText(getDateFormat(endCalendar));
                textViewEndTime.setText(getTimeFormat(endCalendar));
            }

            textViewStartDate.setText(getDateFormat(startCalendar));
        }
        //End date set, so set new
        else if (datePickerSelected == 2) {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //set new enddate
            if (endCalendar.before(startCalendar)) {
                startCalendar.set(endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE),
                        endCalendar.get(Calendar.SECOND));
                startCalendar.add(Calendar.HOUR_OF_DAY, -1);
                textViewStartTime.setText(getTimeFormat(startCalendar));
                textViewStartDate.setText(getDateFormat(startCalendar));
            }
            textViewEndDate.setText(getDateFormat(endCalendar));
        }
    }

    public void setDates() {
        now = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        //tz.getDisplayName(false, TimeZone.SHORT)
        timeZone.setText(tz.getDisplayName() + " " + tz.getID());

        /********* display current time on screen Start ********/
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR_OF_DAY, 1);
        startCalendar.setTimeZone(tz);
        endCalendar.setTimeZone(tz);
        //SET CURRENT TIME AND DATES AND ADD 1 TO END CAL
        textViewStartDate.setText(getDateFormat(startCalendar));

        textViewEndDate.setText(getDateFormat(endCalendar));

        textViewStartTime.setText(getTimeFormat(startCalendar));

        textViewEndTime.setText(getTimeFormat(endCalendar));
    }

    private String getTimeZoneFormat(Calendar c) {

        TimeZone timeZone = c.getTimeZone();
        return timeZone.getID();
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

            if (startCalendar.after(endCalendar) || startCalendar.equals(endCalendar)) {

                endCalendar.set(startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE),
                        startCalendar.get(Calendar.SECOND));
                endCalendar.add(Calendar.HOUR_OF_DAY, 1);
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
                startCalendar.add(Calendar.HOUR_OF_DAY, -1);
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

    @Override
    public void onTimeZoneSet(TimeZoneInfo tzi) {
        timeZone.setText(tzi.mDisplayName + " " + tzi.mTzId + " " + tzi.getGmtDisplayName(CreateSlot.this));
        endCalendar.setTimeZone(TimeZone.getTimeZone(tzi.mTzId));
        startCalendar.setTimeZone(TimeZone.getTimeZone(tzi.mTzId));
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

            if (event) {
                HashMap<String, Object> hashMapEvent = new HashMap<>();
                hashMapEvent.put("subject", subject);
                hashMapEvent.put("message", message);
                hashMapEvent.put("note", note);
                hashMapEvent.put("starttime", startCalendar.getTime());
                hashMapEvent.put("endtime", endCalendar.getTime());
                hashMapEvent.put("attendees", numberAttendeesAvaliable);
                hashMapEvent.put("host", personLoggedIn.getFullname());
                hashMapEvent.put("loggedinperson", personLoggedIn.getObjectId());
                hashMapEvent.put("alldayevent", allDayEvent);
                hashMapEvent.put("category", eventCategory);
                hashMapEvent.put("id", "event");

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


                DeliveryOptions deliveryOptions = new DeliveryOptions();
                // deliveryOptions.setRepeatExpiresAt(); how long before to remind
                PublishOptions publishOptions = new PublishOptions();
                publishOptions.putHeader("android-ticker-text", "Event invite received");
                publishOptions.putHeader("android-content-title", "Amplified Scheduler");
                publishOptions.putHeader("android-content-text", personLoggedIn.fname +
                        " has invited you to " + subject);
                for (Person pId : addedContactsForSlot) {
                    deliveryOptions.addPushSinglecast(pId.getDeviceId());
                }

                Backendless.Messaging.publish("this is a  message!", publishOptions, deliveryOptions);


            } else {
                HashMap<String, Object> hashMapEvent = new HashMap<>();
                hashMapEvent.put("subject", subject);
                hashMapEvent.put("note", note);
                hashMapEvent.put("starttime", startCalendar.getTime());
                hashMapEvent.put("endtime", endCalendar.getTime());
                hashMapEvent.put("alldayevent", allDayEvent);
                hashMapEvent.put("category", eventCategory);
                hashMapEvent.put("loggedinperson", personLoggedIn.getObjectId());

                LatLng latLngPlace = place.getLatLng();

                hashMapEvent.put("lat", latLngPlace.latitude);
                hashMapEvent.put("long", latLngPlace.longitude);
                hashMapEvent.put("id", "plan");
                hashMapEvent.put("location", place.getAddress());

                Backendless.Events.dispatch("CreateEvent", hashMapEvent, new AsyncCallback<Map>() {
                    @Override
                    public void handleResponse(Map map) {
                        Toast.makeText(getApplicationContext(), "Plan Saved", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getApplicationContext(), "Error: Could not create plan", Toast.LENGTH_LONG).show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (event) {
                addedContactsForSlot.clear();
            }

            editTextNumberAttendeesAvaliable.setText("");
            mAutocompleteTextView.setText("");
            slotSubjectEditText.setText("");
            slotMessageEditText.setText("");
            editTextNote.setText("");
            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_send_event), null, null, null);
            setDates();
            contactsAdded = false;
            subjectSet = false;
            my_var = null;

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

            if (subjectSet && contactsAdded) {
                buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_event_ready), null, null, null);
            }
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
        inflater.inflate(R.menu.menu_other, menu);

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