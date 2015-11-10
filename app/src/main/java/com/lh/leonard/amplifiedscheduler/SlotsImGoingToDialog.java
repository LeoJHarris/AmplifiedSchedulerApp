package com.lh.leonard.amplifiedscheduler;

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

public class SlotsImGoingToDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Boolean removed;

    List<Slot> slotsList;
    Slot slotSelected;
    Person person;
    AutoResizeTextView textViewSubject;
    AutoResizeTextView textViewMessage;
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    AutoResizeTextView textViewMyEventSpacesAvaliable;
    AutoResizeTextView organizer;
    // Button buttonCantGo;
    Integer position;
    BackendlessCollection<Slot> slots;
    SpannableString content;
    ProgressBar progressBar;
    Button buttonMySlotParticipantsSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots_im_going_to_dialog); // TODO Layout for Other dialogs

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewSubject = (AutoResizeTextView) findViewById(R.id.textViewGoingToSlotSubject);

        textViewMessage = (AutoResizeTextView) findViewById(R.id.textViewGoingToSlotMessage);
        organizer = (AutoResizeTextView) findViewById(R.id.textViewGoingToSlotOrganizer);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewGoingToSlotDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewGoingToSlotLocation);
        textViewMyEventSpacesAvaliable = (AutoResizeTextView) findViewById(R.id.textViewMyEventSpacesAvaliable);
        //  buttonCantGo = (Button) findViewById(R.id.buttonGoingToSlotCantGo);
        buttonMySlotParticipantsSlot = (Button) findViewById(R.id.buttonMySlotParticipantsSlot);

        organizer.setTypeface(RobotoCondensedLight);
        textViewSubject.setTypeface(RobotoCondensedLight);
        textViewMessage.setTypeface(RobotoCondensedLight);
        textViewDateAndTime.setTypeface(RobotoCondensedLight);
        textViewLocation.setTypeface(RobotoCondensedLight);
        textViewMyEventSpacesAvaliable.setTypeface(RobotoCondensedLight);
        // buttonCantGo.setTypeface(regularFont);
        buttonMySlotParticipantsSlot.setTypeface(RobotoCondensedLight);

        if(userLoggedIn.getProperty("persons") != null) {
            person = (Person) userLoggedIn.getProperty("persons");
        }
        else{
            Backendless.Data.mapTableToClass("Slot", Slot.class);
            Backendless.Data.mapTableToClass("Person", Person.class);
            Backendless.Persistence.mapTableToClass("Slot", Slot.class);
            Backendless.Persistence.mapTableToClass("Person", Person.class);
            person = (Person) userLoggedIn.getProperty("persons");
        }
        new LoadSlotsImGoingTo().execute();

//        buttonCantGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                new AlertDialog.Builder(v.getContext())
//                        .setTitle("Remove Slot?")
//                        .setMessage("Do you want to remove this slot?")
//                        .setIcon(R.drawable.ic_questionmark)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int whichButton) {
//
//                                new RemoveGoingToSlot().execute();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                            }
//                        }).show();
//            }
//        });
        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(SlotsImGoingToDialog.this, JustMapActivity.class);

                mapIntent.putExtra("lat", slotSelected.getLocation().getLatitude());
                mapIntent.putExtra("long", slotSelected.getLocation().getLongitude());
                startActivity(mapIntent);
            }
        });

        buttonMySlotParticipantsSlot.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                Intent participantsIntent = new Intent(SlotsImGoingToDialog.this, ParticipantsActivity.class);

                                                                participantsIntent.putExtra("eventid", slotSelected.getObjectId());

                                                                startActivity(participantsIntent);
                                                            }
                                                        }
        );
    }

    private class LoadSlotsImGoingTo extends AsyncTask<Void, Integer, List<Address>> {

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
            whereClause.append("Person[goingToSlot]");
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
                        textViewDateAndTime.setText("When: " + slotSelected.getDateofslot() + " @ " + slotSelected.getStart());

                    } else {
                        textViewDateAndTime.setText("When: " + slotSelected.getDateofslot() + " @ " + slotSelected.getStart() + " - " + slotSelected.getEnd());
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
                    break;

                }
            }

            if (person.fname != null) {
                organizer.setText(slotSelected.getOwnername()+ " created this event");
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
            progressBar = (ProgressBar) findViewById(R.id.progressBarSlotsGoingToDialog);
            progressBar.setVisibility(View.GONE);

            textViewMyEventSpacesAvaliable.setVisibility(View.VISIBLE);
            textViewSubject.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewDateAndTime.setVisibility(View.VISIBLE);
            // buttonCancelSlot.setVisibility(View.VISIBLE);
            //buttonMySlotParticipantsSlot.setVisibility(View.VISIBLE);
            buttonMySlotParticipantsSlot.setVisibility(View.VISIBLE);

        }
    }

    private class RemoveGoingToSlot extends AsyncTask<Void, Integer, Void> {

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

            ArrayList<String> relationProps = new ArrayList<String>();
            relationProps.add("goingToSlot");
            Backendless.Data.of(Person.class).loadRelations(person, relationProps);

            removed = person.removeGoingToSlot(position);
            Backendless.Data.of(Person.class).save(person);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (removed) {
                Toast.makeText(getApplicationContext(), "event removed", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }


}
