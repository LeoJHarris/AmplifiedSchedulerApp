package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSlot extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoResizeTextView mAddressTextView;
    private AutoResizeTextView mAttTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    // private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
    //   new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    Place place;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    CheckBox checkBoxAppointmentRequired;
    Boolean appointmentBoolean = false;
    static final int TIME_DIALOG_ID = 1111;
    private TextView output;
    private TextView output1;
    public Button btnClickSetStartTime;
    public Button btnClickSetEndTime;
    private int hour;
    private int minute;

    Boolean dateSet = false;
    Boolean subjectSet = false;
    Boolean contactsAdded = false;
    Boolean startTimeSet = false;

    String justStartTime;
    String justEndTime;

    List<Person> myContactsPersonsList;
    BackendlessCollection<Person> myContactPersons;

    BackendlessCollection<Person> persons;

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();

    ArrayList<String> contactsIds = new ArrayList<>();

    Person personLoggedIn;
    LatLng latLng;
    int outputInt = 0;
    Button recipientsForSlotBtn;
    public Button buttonSendSlot;
    EditText editTextNumberAttendeesAvaliable;
    EditText slotSubjectEditText;
    EditText slotMessageEditText;
    TextView slotsDate;
    TextView slotStartTime;
    TextView slotEndTime;

    StringBuilder dateFormatSet = new StringBuilder();

    Button btnSlotDate;
    //  ImageButton btnGetLocationGeoPoint;

    CharSequence[] testArray;
    ArrayList<Integer> mSelectedItems;
    Drawable tickIconDraw;
    Drawable crossIconDraw;
    ArrayList<Person> addedContactsForSlot;

    GeoPoint eventLocation;

    String date;
    String startTime;
    String endTime;
    String subject;
    String message;
    String locationString;
    Integer numberAttendeesAvaliable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_slot);


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
        crossIconDraw = getResources().getDrawable(R.drawable.ic_cross);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        editTextNumberAttendeesAvaliable = (EditText) findViewById(R.id.numberPickerAttendees);
        recipientsForSlotBtn = (Button) findViewById(R.id.recipientsForSlot);
        slotsDate = (TextView) findViewById(R.id.textViewDate);
        btnSlotDate = (Button) findViewById(R.id.btnSlotDate);
        slotSubjectEditText = (EditText) findViewById(R.id.editSlotSubject);
        slotMessageEditText = (EditText) findViewById(R.id.editTextSlotMessage);
        slotStartTime = (TextView) findViewById(R.id.outputStartTime);
        slotEndTime = (TextView) findViewById(R.id.outputEndTime);
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        TextView textViewHeaderCreateSlot = (TextView) findViewById(R.id.textViewHeaderCreateSlot);
        TextView tvSpaces = (TextView) findViewById(R.id.tvSpaces);
        // CheckBox checkBoxAppointmentRequired = (CheckBox) findViewById(R.id.checkBoxAppointmentRequired);
        buttonSendSlot = (Button) findViewById(R.id.buttonSendSlot);
        //checkBoxString = (CheckBox) findViewById(R.id.checkBoxAppointmentRequired);
        Button btnSlotDate = (Button) findViewById(R.id.btnSlotDate);
        Button btnClickSetStartTime = (Button) findViewById(R.id.btnClickSetStartTime);
        btnClickSetEndTime = (Button) findViewById(R.id.btnClickSetEndTime);
        // btnGetLocationGeoPoint = (ImageButton) findViewById(R.id.btnGetLocationGeoPoint);

        recipientsForSlotBtn.setTypeface(regularFont);
        slotsDate.setTypeface(regularFont);
        // checkBoxAppointmentRequired.setTypeface(regularFont);
        btnSlotDate.setTypeface(regularFont);
        slotSubjectEditText.setTypeface(regularFont);
        slotMessageEditText.setTypeface(regularFont);
        mAddressTextView.setTypeface(regularFont);
        mAttTextView.setTypeface(regularFont);
        slotStartTime.setTypeface(regularFont);
        slotEndTime.setTypeface(regularFont);
        mAutocompleteTextView.setTypeface(regularFont);
        textViewHeaderCreateSlot.setTypeface(regularFont);
        tvSpaces.setTypeface(regularFont);
        //checkBoxString.setTypeface(regularFont);
        buttonSendSlot.setTypeface(regularFont);
        btnClickSetStartTime.setTypeface(regularFont);
        btnClickSetEndTime.setTypeface(regularFont);

        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        dateView = (TextView) findViewById(R.id.textViewDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);


        //TODO check if contacts were added, back button removes list


        //month = calendar.get(Calendar.MONTH);
        //day = calendar.get(Calendar.DAY_OF_MONTH);
        //showDate(year, month + 1, day);

        output = (TextView) findViewById(R.id.outputStartTime);
        output1 = (TextView) findViewById(R.id.outputEndTime);

        /********* display current time on screen Start ********/

        final Calendar c = Calendar.getInstance();
        // Current Hour
        hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        minute = c.get(Calendar.MINUTE);

        // set current time into output textview
        updateTime(hour, minute);

        new GetContactsThread().execute();

        recipientsForSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSlot.this);

                // set the dialog title
                builder.setTitle("Add contacts for Schedule")

                        // specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive call backs when items are selected
                        // R.array.choices were set in the resources res/values/strings.xml
                        .setMultiChoiceItems(testArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

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

                        })

                                // Set the action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                // user clicked OK, so save the mSelectedItems results somewhere
                                // here we are trying to retrieve the selected items indices
                                String selectedIndex = "";
                                for (Integer i : mSelectedItems) {
                                    selectedIndex += i + ", ";
                                }

                                addedContactsForSlot = new ArrayList<Person>();

                                String[] selectedContacts = selectedIndex.split(",");

                                for (int j = 0; j < selectedContacts.length - 1; j++) {

                                    if (selectedContacts[j] != " ") {
                                        addedContactsForSlot.add(myContactsPersonsList.get(Integer.parseInt(selectedContacts[j])));
                                    }
                                }
                                if (!(addedContactsForSlot.isEmpty())) {
                                    recipientsForSlotBtn.setText("Contact(s) added");
                                    recipientsForSlotBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                                    recipientsForSlotBtn.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));

                                    contactsAdded = true;

                                    Toast.makeText(CreateSlot.this, "Contact(s) Added", Toast.LENGTH_SHORT).show();

                                    if (dateSet && subjectSet && startTimeSet) {
                                        buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                                        buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                                    }

                                } else {
                                    Toast.makeText(CreateSlot.this, "No Contacts Added", Toast.LENGTH_LONG).show();
                                    contactsAdded = false;
                                    recipientsForSlotBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                    buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, crossIconDraw, null);
                                    buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.red));
                                }
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })

                        .show();
            }
        });

//        btnGetLocationGeoPoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(CreateSlot.this, MapsActivity.class), 1000);
//            }
//        });

        //TODO Google maps

        /********* display current time on screen End ********/

        // Add Button Click Listener
        addButtonClickListener();

        buttonSendSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addedContactsForSlot != null) {
                    if (!(addedContactsForSlot.isEmpty())) {

                        slotSubjectEditText.getText().toString();
                        date = slotsDate.getText().toString();
                        if (editTextNumberAttendeesAvaliable.getText().toString().equals("")) {
                            numberAttendeesAvaliable = 0;
                        } else {
                            numberAttendeesAvaliable = Integer.parseInt(editTextNumberAttendeesAvaliable.getText().toString());
                        }
                        startTime = slotStartTime.getText().toString();
                        endTime = slotEndTime.getText().toString();
                        subject = slotSubjectEditText.getText().toString();
                        message = slotMessageEditText.getText().toString();
                        locationString = mAddressTextView.getText().toString();
                        //TODO Set Slot Ready to send with tick

                        String emptys = "";

                        if (subject.trim().equals("") || date.equals("Set Date") || startTime.equals("Set Start Time") || locationString.equals("Please Set Place")) {

                            if (subject.trim().equals("")) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Subject";
                                } else {
                                    emptys += ", Subject";
                                }
                            }
                            if (locationString.equals("Please Set Place")) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Schedule Place";
                                } else {
                                    emptys += ", Schedule Place";
                                }
                            }

                            if (startTime.equals("Set Start Time")) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Start Time";
                                } else {
                                    emptys += ", Start Time";
                                }
                            }
                            if (date.equals("Set Date")) {
                                if ((emptys.trim().equals(""))) {
                                    emptys += "Date";
                                } else {
                                    emptys += ", Date";
                                }
                            }
//                            if (endTime.trim().equals("Please Set End Time")) {
//                                if ((emptys.trim().equals(""))) {
//                                    emptys += "End Time";
//                                } else {
//                                    emptys += ", End Time";
//                                }
//                            }
//                            if (message.trim().equals("")) {
//                                if ((emptys.trim().equals(""))) {
//                                    emptys += "Message";
//                                } else {
//                                    emptys += ", Message";
//                                }
//                            }
                            Toast.makeText(getApplicationContext(), "Please input: " + emptys, Toast.LENGTH_SHORT).show();
                        } else {


                            new ParseURL().execute();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Add contacts for schedule", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Add contacts for schedule", Toast.LENGTH_LONG).show();
                }
            }
        });

        slotSubjectEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = slotSubjectEditText.getText().toString();
                    if ((!(string.equals("")))) {
                        slotSubjectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);

                        if (dateSet && contactsAdded && startTimeSet) {
                            buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                        }

                        slotSubjectEditText.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                        subjectSet = true;

                    } else {
                        slotSubjectEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, crossIconDraw, null);
                        buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.red));
                        subjectSet = false;
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
        mAutocompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String string = mAutocompleteTextView.getText().toString();
                    if ((!(string.equals("")))) {
                        mAutocompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
                        mAutocompleteTextView.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                    } else {
                        mAutocompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            double geoLocation[] = new double[2];

            String location = data.getStringExtra("location");
            geoLocation = data.getDoubleArrayExtra("geolocation");


            eventLocation = new GeoPoint(geoLocation[0], geoLocation[1]);
            Map<String, Object> locationMap = new HashMap<>();
            locationMap.put("location", location);
            eventLocation.setMetadata(locationMap);
        }

    }

//    public void onCheckboxClicked(View view) {
//        // Is the view now checked?
//        boolean checked = ((CheckBox) view).isChecked();
//
//        // Check which checkbox was clicked
//        switch (view.getId()) {
//            case R.id.checkBoxAppointmentRequired:
//                if (checked) {
//                    appointmentBoolean = true;
//
//                } else {
//                    appointmentBoolean = false;
//                }
//                break;
//        }
//    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        switch (id) {
            case TIME_DIALOG_ID:

                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);
        }
        return null;
    }

    //TODO Java Docs: Times Date pickers

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {

        dateFormatSet.setLength(0);
        dateView.setText("Date: " + dateFormatSet.append(day).append("/")
                .append(month).append("/").append(year));

        dateSet = true;
        btnSlotDate.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);

        if (subjectSet && contactsAdded) {
            buttonSendSlot.getResources().getColorStateList(R.color.deepdarkgreen);
            buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_slot, menu);
        return true;
    }

    public void addButtonClickListener() {

        btnClickSetStartTime = (Button) findViewById(R.id.btnClickSetStartTime);

        btnClickSetStartTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                outputInt = 1;
                showDialog(TIME_DIALOG_ID);
            }

        });


        btnClickSetEndTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                outputInt = 2;
                showDialog(TIME_DIALOG_ID);
            }

        });
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;

            updateTime(hour, minute);
        }

    };

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();


        if (outputInt == 1) {
            justStartTime = aTime;
            output.setText("Start Time: " + aTime);

            if (dateSet && contactsAdded && subjectSet) {
                buttonSendSlot.setTextColor(getResources().getColorStateList(R.color.deepdarkgreen));
                buttonSendSlot.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
            }

            startTimeSet = true;
            btnClickSetStartTime.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);

        } else if (outputInt == 2) {

            justEndTime = aTime;
            output1.setText("End Time: " + aTime);
            btnClickSetEndTime.setCompoundDrawablesWithIntrinsicBounds(null, null, tickIconDraw, null);
        }
    }

    private class ParseURL extends AsyncTask<Void, Integer, Void> {

        ProgressDialog ringProgressDialog;

        @Override
        protected void onPreExecute() {
            ringProgressDialog = ProgressDialog.show(CreateSlot.this, "Please wait ...", "Sending schedule ...", true);
            ringProgressDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            // String startDateTime = dateFormatSet.toString() + " " + justStartTime; // TODO REmove date var, dont need to get the text from textview now
            //  String endDateTime = dateFormatSet.toString() + " " + justEndTime;


            Slot slot = new Slot();
            slot.setSubject(subject);
            slot.setMessage(message);
            slot.setMaxattendees(numberAttendeesAvaliable);
            // slot.setAppointmentOnly(appointmentBoolean);
            slot.setDateofslot(dateFormatSet.toString());
            slot.setStart(justStartTime);
            slot.setEnd(justEndTime);
            slot.setMaxattendees(numberAttendeesAvaliable);
            slot.setPhone(personLoggedIn.getPhone());

            LatLng latLngPlace = place.getLatLng();

            GeoPoint geoPlace = new GeoPoint(latLngPlace.latitude, latLngPlace.longitude);

            Map<String, Object> locationMap = new HashMap<>();
            locationMap.put("location", place.getAddress());
            geoPlace.setMetadata(locationMap);

            // slot.setPlace(place.getAddress().toString());
            slot.setOwnername(personLoggedIn.getFullname());

            // eventLocation has the data from the map activity
            //  if (eventLocation != null) {

            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put("slot", slot);
            geoPlace.setMetadata(metaMap);


            slot.setLocation(geoPlace);
            //  }
            Slot savedSlot = Backendless.Data.save(slot);


            //TODO reused code below here from multiple class Should make a class and method maybe

            ArrayList<String> relationProps = new ArrayList();
            relationProps.add("mycreatedslots");

            Backendless.Data.of(Person.class).loadRelations(personLoggedIn, relationProps);

            personLoggedIn.addSlotToMyCreatedSlot(savedSlot);

            Backendless.Data.save(personLoggedIn);

            // For all the contacts add to their pending response slot
            for (Person pId : addedContactsForSlot) {

                Person personContact = Backendless.Data.of(Person.class).findById(pId.objectId);
                ArrayList<String> contactRelationProps = new ArrayList();
                relationProps.add("pendingresponseslots");
                relationProps.add("unseenSlots");

                sendsmss(pId.getPhone(), message, subject, dateFormatSet.toString(), justStartTime);

                Backendless.Data.of(Person.class).loadRelations(personContact, contactRelationProps);
                personContact.addSlotToPendingResponseSlot(savedSlot);
                personContact.addToUnseenEvents(savedSlot);
                personContact.addToUnseenEvents(savedSlot);

                Backendless.Data.save(personContact);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            slotSubjectEditText.setText("");
            slotsDate.setText("Set Date");
            slotStartTime.setText("Set Start Time");
            slotEndTime.setText("Set End Time");
            slotSubjectEditText.setText("");
            slotMessageEditText.setText("");

            ringProgressDialog.dismiss();

            Toast.makeText(CreateSlot.this, "Schedule Sent", Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String message, String subject, String date, String time) {

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
            messageSubString = "Amplified Scheduler: Invited Schedule. Host: " + fullnameLoggedin +
                    ". " + subject + ". Message: " + messageSubString + dots + " When: " + date + " at " + time;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
        } else {
            String messageSubString = "Amplified Scheduler: Invited Schedule. Host: " + fullnameLoggedin +
                    ". " + subject + " no message included " + " when: " + date + " at " + time;
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

            // where we will store or remove selected items
            mSelectedItems = new ArrayList<Integer>();

            testArray = new String[myContactsPersonsList.size()];
            int i = 0;
            for (Person pers : myContactsPersonsList) {
                testArray[i] = pers.fullname;
                i++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
}