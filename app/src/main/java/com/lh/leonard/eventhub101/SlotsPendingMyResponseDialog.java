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

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlotsPendingMyResponseDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    List<Slot> slotsList;
    Slot slotSelected;
    AutoResizeTextView textViewSubject;
    AutoResizeTextView textViewMessage;
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    AutoResizeTextView textViewMyEventSpacesAvaliable;
    AutoResizeTextView textViewOrganiser;
    Integer position;
    Button buttonCantGo;
    Button buttonGoing;
    Person person;
    BackendlessCollection<Slot> slots;
    SpannableString content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots_pending_my_response_dialog);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);

        final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        textViewSubject = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotSubject);
        textViewMessage = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotMessage);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotLocation);
        textViewMyEventSpacesAvaliable = (AutoResizeTextView) findViewById(R.id.textViewMyEventSpacesAvaliable);
        textViewOrganiser = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotOrganizer);
        buttonGoing = (Button) findViewById(R.id.buttonRequestSlotGoing);
        buttonCantGo = (Button) findViewById(R.id.buttonRequestSlotCantGo);

        textViewSubject.setTypeface(regularFont);
        textViewMessage.setTypeface(regularFont);
        textViewDateAndTime.setTypeface(regularFont);
        textViewLocation.setTypeface(regularFont);
        textViewMyEventSpacesAvaliable.setTypeface(regularFont);
        textViewOrganiser.setTypeface(regularFont);
        buttonCantGo.setTypeface(regularFont);
        buttonGoing.setTypeface(regularFont);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        person = (Person) userLoggedIn.getProperty("persons");

        new LoadMyContacts().execute();

        buttonCantGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonGoing.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               new AddToMyEvents().execute();

                                           }
                                       }
        );

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(SlotsPendingMyResponseDialog.this, JustMapActivity.class);

                mapIntent.putExtra("lat", slotSelected.getLocation().getLatitude());
                mapIntent.putExtra("long", slotSelected.getLocation().getLongitude());
                startActivity(mapIntent);
            }
        });
    }

    private class LoadMyContacts extends AsyncTask<Void, Integer, List<Address>> {

        @Override
        protected void onPreExecute() {
            //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //   progressBar.setVisibility(View.VISIBLE);
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
            whereClause.append("Person[pendingResponseSlot]");
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

            if (slotSelected.getOwnername() != null) {
                textViewOrganiser = (AutoResizeTextView) findViewById(R.id.textViewRequestSlotOrganizer);
                textViewOrganiser.setText(slotSelected.getOwnername() + " created this event");
            }

            if (slotSelected.getMaxattendees() != 0) {


                Integer spacesAvaliable = slotSelected.getMaxattendees();
                Integer going = slotSelected.getAttendees().size();
                {
                    textViewMyEventSpacesAvaliable.setText(going + " going, waiting response from " + (spacesAvaliable - going));
                }

            } else {
                textViewMyEventSpacesAvaliable.setText("Unlimited Spaces");
            }
        }
    }

    private class AddToMyEvents extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //   progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<String> relationPropsPerson = new ArrayList<String>();
            relationPropsPerson.add("goingToSlot");
            Backendless.Persistence.of(Person.class).loadRelations(person, relationPropsPerson);

            // person.getGoingToSlot().add(slotSelected);
            //  Backendless.Persistence.save(person);


            ArrayList<String> relationProps = new ArrayList<String>();
            relationProps.add("attendees");
            Backendless.Persistence.of(Slot.class).loadRelations(slotSelected, relationProps);


            slotSelected.getAttendees().add(person);

            Backendless.Persistence.of(Slot.class).save(slotSelected);

            //Still have to remove from my list requesting

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {


        }
    }
}