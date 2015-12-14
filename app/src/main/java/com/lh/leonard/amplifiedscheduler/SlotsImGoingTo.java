package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

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
import java.util.List;
import java.util.Locale;

public class SlotsImGoingTo extends AppCompatActivity {

    Person personLoggedIn;
    List<Slot> slot;
    List<Person> personsToSms;
    BackendlessCollection<Person> personsToSmsCollection;
    private ProgressBar progressBar;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    SearchView searchViewSlots;
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
    Calendar minDate;
    Calendar maxDate;
    private Toolbar toolbar;
    RelativeLayout RLProgressBar;
    Boolean alldayevent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);

        mAgendaCalendarView = (AgendaCalendarView) findViewById(R.id.agenda_calendar_view);
        RLProgressBar = (RelativeLayout) findViewById(R.id.RLProgressBar);
        // minimum and maximum date of our calendar
        // 1 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -1);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        new ParseURL().execute();
        searchViewSlots = (SearchView) findViewById(R.id.searchViewSlots);
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
            whereClause.append("Person[GoingToSlot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();


            eventList = new ArrayList<>();

            getEventsFromList(slot);

//           for(int j = 0; j < slot.size(); j++) {
//              //  if (slot.get(j).parseDateString().before(date)) {
//
//                    Backendless.Geo.removePoint(slot.get(j).getLocation());
//                    Backendless.Persistence.of(Slot.class).remove(slot.get(j));
//                    slot.remove(j);
//              //  }
//            }

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

                        Intent slotDialogIntent = new Intent(SlotsImGoingTo.this, SlotsImGoingToDialog.class);

                        int position = Integer.parseInt(String.valueOf(event.getId()));
                        slotDialogIntent.putExtra("origin", 1);
                        slotDialogIntent.putExtra("objectId", String.valueOf(slot.get(position).getObjectId()));

                        startActivity(slotDialogIntent);

                    }
                }
            };

            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), mPickerController);
            progressBar.setVisibility(View.GONE);
            RLProgressBar.setVisibility(View.GONE);
            mAgendaCalendarView.setVisibility(View.VISIBLE);
        }
    }


    private void getEventsFromList(List<Slot> eventListSlots) {


        for (int i = 0; i < eventListSlots.size(); i++) {


            Boolean allDay = false;

            if (eventListSlots.get(i).isAllDayEvent()) {
                allDay = true;
            }

            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            startTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getStartCalendar().get(Calendar.DAY_OF_YEAR));

            // End time
            endTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getStartCalendar().get(Calendar.DAY_OF_YEAR));

            // System.out.println(startTime.toString());

            String location = (String) eventListSlots.get(i).getLocation().getMetadata("address");

            CalendarEvent event = new CalendarEvent(eventListSlots.get(i).getSubject(),
                    eventListSlots.get(i).getMessage(), location,
                    ContextCompat.getColor(this, R.color.orangecalendar), startTime, endTime, allDay);

            Long l = Long.parseLong(String.valueOf(i));

            event.setId(l);

            eventList.add(event);

            //CalendarEvent event1 = new CalendarEvent()
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NavDrawerActivity.class);
        startActivity(intent);
        finish();
    }
}