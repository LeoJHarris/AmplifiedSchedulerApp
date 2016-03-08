package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyPlansDialog extends Activity {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    AutoResizeTextView textViewMyPlanSubject;
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
    Button buttonMyPlanCancelSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plans_dialog);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        final Typeface RobotoBlack = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewNote = (AutoResizeTextView) findViewById(R.id.textViewMyPlanNote);
        textViewMyPlanSubject = (AutoResizeTextView) findViewById(R.id.textViewMyPlanSubject);
        textViewDateAndTime = (AutoResizeTextView) findViewById(R.id.textViewMyPlanDateAndTime);
        textViewLocation = (AutoResizeTextView) findViewById(R.id.textViewMyPlanLocation);
        buttonMyPlanCancelSlot = (Button) findViewById(R.id.buttonMyPlanCancelSlot);

        buttonMyPlanCancelSlot.setTypeface(RobotoCondensedLight);
        textViewMyPlanSubject.setTypeface(RobotoCondensedLight);
        textViewDateAndTime.setTypeface(RobotoCondensedLight);
        textViewLocation.setTypeface(RobotoCondensedLight);
        textViewNote.setTypeface(RobotoCondensedLight);

        person = (Person) userLoggedIn.getProperty("persons");

        new LoadMyContacts().execute();

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

        buttonMyPlanCancelSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Cancel")
                        .setMessage("Discard this event")
                        .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                                ringProgressDialog = ProgressDialog.show(MyPlansDialog.this, "Please wait ...", "Cancelling Event: " + event.getSubject() + " ...", true);
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

            return (String) event.getLocation().getMetadata("address");

        }

        @Override
        protected void onPostExecute(String addresses) {

            if (event.getSubject() != null) {
                textViewMyPlanSubject.setText(event.getSubject());
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
                textViewNote.setText(event.getNote());
            }

            if (event.getLocation() != null) {
                content = new SpannableString("Where: " + (String) event.getLocation().getMetadata("address"));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                textViewLocation.setText(content); //TODO Button to get Location else just Text
            }
            progressBar = (ProgressBar) findViewById(R.id.progressBarMyPlanDialog);
            progressBar.setVisibility(View.GONE);
            textViewMyPlanSubject.setVisibility(View.VISIBLE);
            textViewLocation.setVisibility(View.VISIBLE);
            textViewDateAndTime.setVisibility(View.VISIBLE);
            buttonMyPlanCancelSlot.setVisibility(View.VISIBLE);
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
    public void onBackPressed() {

        Intent intent = new Intent(this, MyPlans.class);
        startActivity(intent);
        finish();
    }
}