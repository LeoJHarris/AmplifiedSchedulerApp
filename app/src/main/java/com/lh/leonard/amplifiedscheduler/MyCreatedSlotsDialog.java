package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class MyCreatedSlotsDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    List<Slot> slotsList;
    AutoResizeTextView textViewSubject;
    AutoResizeTextView textViewMessage;
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    AutoResizeTextView textViewMyeventSpacesAvaliable;
    String objectId;
    // Button buttonCancelSlot;
    List<Person> personsToSms;
    BackendlessCollection<Person> personsToSmsCollection;
    Button buttonMySlotParticipantsSlot;
    Person person;
    BackendlessCollection<Slot> slots;
    SpannableString content;
    ProgressBar progressBar;
    String eventRemoved;
    Slot event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_created_slots_dialog);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewSubject = (AutoResizeTextView) findViewById(R.id.textViewMySlotSubject);
        textViewMessage = (AutoResizeTextView) findViewById(R.id.textViewMySlotMessage);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewMySlotDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewMySlotLocation);
        textViewMyeventSpacesAvaliable = (AutoResizeTextView) findViewById(R.id.textViewMyEventSpacesAvaliable);
        //buttonCancelSlot = (Button) findViewById(R.id.buttonMySlotCancelSlot);
        buttonMySlotParticipantsSlot = (Button) findViewById(R.id.buttonMySlotParticipantsSlot);

        textViewSubject.setTypeface(RobotoCondensedLight);
        textViewMessage.setTypeface(RobotoCondensedLight);
        textViewDateAndTime.setTypeface(RobotoCondensedLight);
        textViewLocation.setTypeface(RobotoCondensedLight);
        textViewMyeventSpacesAvaliable.setTypeface(RobotoCondensedLight);
        // buttonCancelSlot.setTypeface(regularFont);
        buttonMySlotParticipantsSlot.setTypeface(RobotoCondensedLight);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        person = (Person) userLoggedIn.getProperty("persons");

        new LoadMyContacts().execute();

//        buttonCancelSlot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new Cancelevent().execute();
//            }
//        });

        buttonMySlotParticipantsSlot.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                Intent participantsIntent = new Intent(MyCreatedSlotsDialog.this, ParticipantsActivity.class);

                                                                participantsIntent.putExtra("eventid", event.getObjectId());

                                                                startActivity(participantsIntent);
                                                            }
                                                        }
        );

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mapIntent = new Intent(MyCreatedSlotsDialog.this, JustMapActivity.class);

                mapIntent.putExtra("lat", event.getLocation().getLatitude());
                mapIntent.putExtra("long", event.getLocation().getLongitude());
                mapIntent.putExtra("subject", event.getSubject());
                startActivity(mapIntent);
            }
        });
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

            event = Backendless.Data.of(Slot.class).findById(objectId);


            return (String) event.getLocation().getMetadata("address");

        }

        @Override
        protected void onPostExecute(String addresses) {


            if (event.getSubject() != null) {
                textViewSubject.setText(event.getSubject());
            }

            if (event.getMessage() != null) {
                textViewMessage.setText("Message: " + event.getMessage());
            }

//            if (event.getDateofslot() != null) {
//                if (event.getStart() != null) {
//
//                    if (event.getEnd() == null) {
//                        textViewDateAndTime.setText("When: " + event.getDateofslot() + " @ " + event.getStart());
//
//                    } else {
//                        textViewDateAndTime.setText("When: " + event.getDateofslot() + " @ " + event.getStart() + " - " + event.getEnd());
//                    }
//                }
//            }

//            if (person.fname != null) {
//                textViewOrganiser = (TextView) findViewById(R.id.textViewMySlotOrganiser);
//                textViewOrganiser.setText(person.getFname() + " " + person.getLname() + " created this event/slot");
//            }

            if (event.getMaxattendees() != 0) {


                Integer spacesAvaliable = event.getMaxattendees();
                Integer going = event.getAttendees().size();
                {
                    Integer spacesLeft = spacesAvaliable - event.getAttendees().size();
                    textViewMyeventSpacesAvaliable.setText(going + " going, waiting response from " + (spacesAvaliable - going));

                }

            }// else {
//                //textViewMyeventSpacesAvaliable.setText("Unlimited Spaces");
//            }

            if (event.getLocation() != null) {
                textViewLocation.setText((String) event.getLocation().getMetadata("address"));
            }
            progressBar = (ProgressBar) findViewById(R.id.progressBarMyCreatedSlotsDialog);
            progressBar.setVisibility(View.GONE);

            textViewMyeventSpacesAvaliable.setVisibility(View.VISIBLE);
            textViewSubject.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewDateAndTime.setVisibility(View.VISIBLE);
            // buttonCancelSlot.setVisibility(View.VISIBLE);
            buttonMySlotParticipantsSlot.setVisibility(View.VISIBLE);
        }
    }

    private class GetAttendees extends AsyncTask<Void, Integer, Void> {

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


            List<String> relations = new ArrayList<String>();
            relations.add("attendees");
            //   relations.add("invitedpersons");
            event = Backendless.Data.of(Slot.class).findById(event.getObjectId(), relations);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

    private class Cancelevent extends AsyncTask<Void, Integer, Void> {

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
            whereClause.append("Slot[attendees]");
            whereClause.append(".objectId='").append(event.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            personsToSmsCollection = Backendless.Data.of(Person.class).find(dataQuery);
            personsToSms = personsToSmsCollection.getData();

            String fullnamePersonLoggedIn = person.getFullname();
            // String dateofslot = event.getDateofslot();
            String subject = event.getSubject();
            String placeofSlot = event.getPlace();

            for (Person pId : personsToSms) {

                //   sendsmss(pId.getPhone(), fullnamePersonLoggedIn, subject, dateofslot, placeofSlot);
            }


            // Deleting process

            List<String> relations = new ArrayList<String>();
            relations.add("myCreatedSlot");
            Person person1 = Backendless.Data.of(Person.class).findById(person.getObjectId(), relations);

            int pos = 0;

            for (int i = 0; i < person1.myCreatedSlot.size(); i++) {

                if (person1.myCreatedSlot.get(i).getObjectId().equals(event.getObjectId())) {
                    pos = i;
                    break;
                }
            }

            eventRemoved = event.getSubject();
            //  Backendless.Geo.removePoint(event.getLocation());

            Long result = Backendless.Persistence.of(Slot.class).remove(event); // TODO toast "'result' events removed"

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            finish();
        }
    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String from, String subject, String date, String place) {

        String messageSubString = "Automated TXT - Amplified Scheduler: event" + subject + " on the " + date + " at " + place + " was cancelled by " + from;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
    }
}