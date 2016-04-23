package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCreatedSlotsDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    AutoResizeTextView textViewSubject;
    AutoResizeTextView textViewMessage;
    AutoResizeTextView textViewDateAndTime;
    AutoResizeTextView textViewLocation;
    AutoResizeTextView textViewMyeventSpacesAvaliable;
    String objectId;
    Button buttonCancelSlot;
    Button buttonMySlotParticipantsSlot;
    Person person;
    BackendlessCollection<Slot> slots;
    SpannableString content;
    ProgressBar progressBar;
    String eventRemoved;
    Slot event;
    AlertDialog dialog;
    ProgressDialog ringProgressDialog;
    AutoResizeTextView textViewMyNote;
    int origin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_created_slots_dialog);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");


        textViewSubject = (AutoResizeTextView) findViewById(R.id.textViewMySlotSubject);
        textViewMessage = (AutoResizeTextView) findViewById(R.id.textViewMySlotMessage);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewMySlotDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewMySlotLocation);
        textViewMyeventSpacesAvaliable = (AutoResizeTextView) findViewById(R.id.textViewMyEventSpacesAvaliable);
        buttonCancelSlot = (Button) findViewById(R.id.buttonMySlotCancelSlot);
        buttonMySlotParticipantsSlot = (Button) findViewById(R.id.buttonMySlotParticipantsSlot);
        textViewMyNote = (AutoResizeTextView) findViewById(R.id.textViewMyNote);

        textViewMyNote.setTypeface(RobotoCondensedLight);
        textViewSubject.setTypeface(RobotoCondensedLight);
        textViewMessage.setTypeface(RobotoCondensedLight);
        textViewDateAndTime.setTypeface(RobotoCondensedLight);
        textViewLocation.setTypeface(RobotoCondensedLight);
        textViewMyeventSpacesAvaliable.setTypeface(RobotoCondensedLight);
        buttonCancelSlot.setTypeface(RobotoCondensedLight);
        buttonMySlotParticipantsSlot.setTypeface(RobotoCondensedLight);

        person = (Person) userLoggedIn.getProperty("persons");

        new LoadMyContacts().execute();

        buttonCancelSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Cancel")
                        .setMessage("Discard this event")
                        .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                                ringProgressDialog = ProgressDialog.show(MyCreatedSlotsDialog.this, "Please wait ...", "Cancelling Event: " + event.getSubject() + " ...", true);
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
        });

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
            event = Backendless.Data.of(Slot.class).findById(objectId);

            return (String) event.getLocation().getMetadata("address");
        }

        @Override
        protected void onPostExecute(String addresses) {

            if (event.getSubject() != null) {
                textViewSubject.setText(event.getSubject());
            }

            if (event.getMessage() != null) {
                if (event.getMessage().equals("")) {
                    textViewMessage.setText("Message: No message avaliable");
                } else {
                    textViewMessage.setText("Message: " + event.getMessage());
                }
            }

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
            if (event.getNote() != null) {
                if (event.getMessage().equals("")) {
                    textViewMyNote.setText("Private note: No private note avaliable");
                } else {
                    textViewMyNote.setText("Private note: " + event.getNote());
                }
            }
            if (event.getMaxattendees() != 0) {

                String message = "";
                Integer spacesAvaliable = event.getMaxattendees();
                Integer going = event.getAttendees().size();
                Integer spacesLeft = (spacesAvaliable - going);
                {
                    if (spacesLeft > 1 || spacesLeft == 0) {
                        message = spacesLeft + " spaces remaining";
                    } else if (spacesLeft == 1) {
                        message = spacesLeft + " space remaining";
                    }
                    textViewMyeventSpacesAvaliable.setText(going + " going, " + message);
                }
            }

            if (event.getLocation() != null) {
                content = new SpannableString("Where: " + event.getLocation().getMetadata("address"));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                textViewLocation.setText(content); //TODO Button to get Location else just Text
            }
            progressBar = (ProgressBar) findViewById(R.id.progressBarMyCreatedSlotsDialog);
            progressBar.setVisibility(View.GONE);

            textViewMyeventSpacesAvaliable.setVisibility(View.VISIBLE);
            textViewSubject.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewMyNote.setVisibility(View.VISIBLE);
            textViewDateAndTime.setVisibility(View.VISIBLE);
            buttonCancelSlot.setVisibility(View.VISIBLE);
            buttonMySlotParticipantsSlot.setVisibility(View.VISIBLE);
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


            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Slot[attendees]");
            whereClause.append(".objectId='").append(event.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            eventRemoved = event.getSubject();

            Map<String, String> args = new HashMap<>();
            args.put("id", "deleteevent");

            args.put("event", event.getObjectId());

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

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String from, String subject, String date, String place) {

        String messageSubString = "Automated TXT - Amplified Scheduler: " + subject + " on the " + date + " at " + place + " was cancelled by ";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
    }

    @Override
    public void onBackPressed() {

            Intent intent = new Intent(this, MyCreatedSlots.class);
            startActivity(intent);
            finish();
    }
}