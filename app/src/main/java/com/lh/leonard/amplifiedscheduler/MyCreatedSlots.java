package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MyCreatedSlots extends AppCompatActivity {

    Person personLoggedIn;
    List<Slot> slot;
    List<Person> personsToSms;
    BackendlessCollection<Person> personsToSmsCollection;
    private ProgressBar progressBar;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    RVAdapter adapter;
    View v;
    ProgressDialog ringProgressDialog;
    AlertDialog dialog;
    RecyclerView rv;
    LinearLayoutManager llm;
    String eventRemoved;
    AgendaCalendarView mAgendaCalendarView;
    List<CalendarEvent> eventList;

    private Toolbar toolbar;
    RelativeLayout RLProgressBar;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);

        mAgendaCalendarView = (AgendaCalendarView) findViewById(R.id.agenda_calendar_view);
        RLProgressBar = (RelativeLayout) findViewById(R.id.RLProgressBar);

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        new ParseURL().execute();
    }


    private class ParseURL extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[mycreatedslot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();

            eventList = new ArrayList<>();

            getEventsFromList(slot);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            CalendarPickerController mPickerController = new CalendarPickerController() {
                @Override
                public void onDaySelected(DayItem dayItem) {
                }

                @Override
                public void onEventSelected(CalendarEvent event) {

                    if (!event.getTitle().equals("No events")) {

                        Intent slotDialogIntent = new Intent(MyCreatedSlots.this, MyCreatedSlotsDialog.class);

                        int position = Integer.parseInt(String.valueOf(event.getId()));

                        slotDialogIntent.putExtra("objectId", String.valueOf(slot.get(position).getObjectId()));

                        startActivity(slotDialogIntent);
                    }
                }
            };


            Calendar minDate;
            Calendar maxDate;
            // minimum and maximum date of our calendar
            // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
            minDate = Calendar.getInstance();
            maxDate = Calendar.getInstance();

            minDate.add(Calendar.MONTH, -1);
            minDate.set(Calendar.DAY_OF_MONTH, 1);
            maxDate.add(Calendar.YEAR, 1);

            progressBar.setVisibility(View.GONE);
            RLProgressBar.setVisibility(View.GONE);
            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), mPickerController);
            mAgendaCalendarView.setVisibility(View.VISIBLE);
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                // Complete with your code
               // new Refresh().execute();
                setRefreshActionButtonState(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void getEventsFromList(List<Slot> eventListSlots) {


        for (int i = 0; i < eventListSlots.size(); i++) {

            Calendar startTime =  Calendar.getInstance();
            Calendar endTime =  Calendar.getInstance();

            startTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getStartCalendar().get(Calendar.DAY_OF_YEAR));
            // End time
            endTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getStartCalendar().get(Calendar.DAY_OF_YEAR));

            String location = (String) eventListSlots.get(i).getLocation().getMetadata("address");

            CalendarEvent event = new CalendarEvent(eventListSlots.get(i).getSubject(),
                    eventListSlots.get(i).getMessage(), location,
                    ContextCompat.getColor(this, R.color.orangecalendar), startTime, endTime, false);

            event.setId(Long.parseLong(String.valueOf(i)));
            eventList.add(event);
        }
    }

//    private class Refresh extends AsyncTask<Void, Integer, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            StringBuilder whereClause = new StringBuilder();
//            whereClause.append("Person[mycreatedslot]");
//            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");
//
//            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
//            dataQuery.setWhereClause(whereClause.toString());
//
//            slots = Backendless.Data.of(Slot.class).find(dataQuery);
//            slot = slots.getData();
//
//
//            eventList = new ArrayList<>();
//
//            getEventsFromList(slot);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//
//            CalendarPickerController mPickerController = new CalendarPickerController() {
//                @Override
//                public void onDaySelected(DayItem dayItem) {
//                }
//
//                @Override
//                public void onEventSelected(CalendarEvent event) {
//
//                    if (!event.getTitle().equals("No events")) {
//
//                        Intent slotDialogIntent = new Intent(MyCreatedSlots.this, MyCreatedSlotsDialog.class);
//
//                        int position = Integer.parseInt(String.valueOf(event.getId()));
//
//
//                        slotDialogIntent.putExtra("objectId", String.valueOf(slot.get(position).getObjectId()));
//
//                        startActivity(slotDialogIntent);
//
//                    }
//                }
//            };
//
//
//            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), mPickerController);
//            Toast.makeText(getApplicationContext(), "Events Synced", Toast.LENGTH_LONG).show();
//            setRefreshActionButtonState(false);
//        }
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NavDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
}