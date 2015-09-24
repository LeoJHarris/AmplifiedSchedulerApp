package com.lh.leonard.eventhub101;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCreatedSlotsDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    List<Slot> slotsList;
    Slot slotSelected;
    AutoResizeTextView textViewSubject;
    AutoResizeTextView textViewMessage;
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    AutoResizeTextView textViewMyEventSpacesAvaliable;
    Integer position;
    Button buttonCancelSlot;
    Button buttonMySlotParticipantsSlot;
    Person person;
    BackendlessCollection<Slot> slots;
    SpannableString content;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_created_slots_dialog);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        textViewSubject = (AutoResizeTextView) findViewById(R.id.textViewMySlotSubject);
        textViewMessage = (AutoResizeTextView) findViewById(R.id.textViewMySlotMessage);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewMySlotDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewMySlotLocation);
        textViewMyEventSpacesAvaliable = (AutoResizeTextView) findViewById(R.id.textViewMyEventSpacesAvaliable);
        buttonCancelSlot = (Button) findViewById(R.id.buttonMySlotCancelSlot);
        buttonMySlotParticipantsSlot = (Button) findViewById(R.id.buttonMySlotParticipantsSlot);

        textViewSubject.setTypeface(regularFont);
        textViewMessage.setTypeface(regularFont);
        textViewDateAndTime.setTypeface(regularFont);
        textViewLocation.setTypeface(regularFont);
        textViewMyEventSpacesAvaliable.setTypeface(regularFont);
        buttonCancelSlot.setTypeface(regularFont);
        buttonMySlotParticipantsSlot.setTypeface(regularFont);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        person = (Person) userLoggedIn.getProperty("persons");

        new LoadMyContacts().execute();

        buttonCancelSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Please long hold on events themselves to remove events on previous page", Toast.LENGTH_LONG).show();
            }
        });

        buttonMySlotParticipantsSlot.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                Intent participantsIntent = new Intent(MyCreatedSlotsDialog.this, ParticipantsActivity.class);

                                                                participantsIntent.putExtra("eventid", slotSelected.getObjectId());

                                                                startActivity(participantsIntent);
                                                            }
                                                        }
        );

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mapIntent = new Intent(MyCreatedSlotsDialog.this, JustMapActivity.class);

                mapIntent.putExtra("lat", slotSelected.getLocation().getLatitude());
                mapIntent.putExtra("long", slotSelected.getLocation().getLongitude());
                mapIntent.putExtra("subject", slotSelected.getSubject());
                startActivity(mapIntent);
            }
        });
    }

    private class LoadMyContacts extends AsyncTask<Void, Integer, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Address> doInBackground(Void... params) {

            Bundle data = getIntent().getExtras();
            position = data.getInt("slotRef");

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[mycreatedslot]");
            whereClause.append(".objectId='").append(person.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);

            slotsList = slots.getData();

            slotSelected = slotsList.get(position);

            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocation(slotSelected.getLocation().getLatitude(), slotSelected.getLocation().getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {


            if (slotSelected.getSubject() != null) {
                textViewSubject.setText(slotSelected.getSubject());
            }

            if (slotSelected.getMessage() != null) {
                textViewMessage.setText("Message: " + slotSelected.getMessage());
            }

            if (slotSelected.getDateofslot() != null) {
                if (slotSelected.getStart() != null) {

                    if (slotSelected.getEnd() == null) {
                        textViewDateAndTime.setText("When: " + slotSelected.getDateofslot() + ", " + slotSelected.getStart());

                    } else {
                        textViewDateAndTime.setText("When: " + slotSelected.getDateofslot() + ", " + slotSelected.getStart() + " - " + slotSelected.getEnd());
                    }
                }
            }

            if (slotSelected.getLocation() != null) {

                for (int i = 0; i < addresses.size(); i++) {
                    Address address = (Address) addresses.get(i);
                    String addressText = String.format("%s, %s",
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                            address.getCountryName());

                    content = new SpannableString("Where: " + addressText);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    textViewLocation.setText(content); //TODO Button to get Location else just Text

                }
            }

//            if (person.fname != null) {
//                textViewOrganiser = (TextView) findViewById(R.id.textViewMySlotOrganiser);
//                textViewOrganiser.setText(person.getFname() + " " + person.getLname() + " created this event/slot");
//            }

            if (slotSelected.getMaxattendees() != 0) {


                Integer spacesAvaliable = slotSelected.getMaxattendees();
                Integer going = slotSelected.getAttendees().size();
                {
                    Integer spacesLeft = spacesAvaliable - slotSelected.getAttendees().size();
                    textViewMyEventSpacesAvaliable.setText(going + " going, waiting response from " + (spacesAvaliable - going));

                }

            } else {
                textViewMyEventSpacesAvaliable.setText("Unlimited Spaces");
            }

            progressBar = (ProgressBar) findViewById(R.id.progressBarMyCreatedSlotsDialog);
            progressBar.setVisibility(View.GONE);

            textViewMyEventSpacesAvaliable.setVisibility(View.VISIBLE);
            textViewSubject.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewDateAndTime.setVisibility(View.VISIBLE);
            buttonCancelSlot.setVisibility(View.VISIBLE);
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
            slotSelected = Backendless.Data.of(Slot.class).findById(slotSelected.getObjectId(), relations);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

}