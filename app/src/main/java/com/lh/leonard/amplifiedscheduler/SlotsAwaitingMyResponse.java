package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SlotsAwaitingMyResponse extends AppCompatActivity implements
        WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.MonthChangeListener {

    Person personLoggedIn;
    List<Slot> slot;
    List<Person> personsToSms;
    BackendlessCollection<Person> personsToSmsCollection;
    private ProgressBar progressBar;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    View v;
    ProgressDialog ringProgressDialog;
    AlertDialog dialog;
    RecyclerView rv;
    LinearLayoutManager llm;
    String eventRemoved;
    AgendaCalendarView mAgendaCalendarView;
    List<CalendarEvent> eventList;
    LinearLayout linearLayoutWeekView;
    LinearLayout linearLayoutCalendarView;
    Boolean weekview = true;
    private Toolbar toolbar;
    RelativeLayout RLProgressBar;
    private Menu optionsMenu;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);

        mAgendaCalendarView = (AgendaCalendarView) findViewById(R.id.agenda_calendar_view);
        RLProgressBar = (RelativeLayout) findViewById(R.id.RLProgressBar);

        linearLayoutCalendarView = (LinearLayout) findViewById(R.id.LLCalendarView);
        linearLayoutWeekView = (LinearLayout) findViewById(R.id.LLWeekView);

        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");

        AutoResizeTextView tvSocial = (AutoResizeTextView) findViewById(R.id.textViewSocial);
        AutoResizeTextView textViewCulturalEvents = (AutoResizeTextView) findViewById(R.id.textViewCulturalEvents);
        AutoResizeTextView textViewAcademicEvents = (AutoResizeTextView) findViewById(R.id.textViewAcademicEvents);
        AutoResizeTextView textViewWorkEvents = (AutoResizeTextView) findViewById(R.id.textViewWorkEvents);

        tvSocial.setTypeface(RobotoCondensedLightItalic);
        textViewCulturalEvents.setTypeface(RobotoCondensedLightItalic);
        textViewAcademicEvents.setTypeface(RobotoCondensedLightItalic);
        textViewWorkEvents.setTypeface(RobotoCondensedLightItalic);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        new ParseURL().execute();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        Intent slotDialogIntent = new Intent(this, SlotsPendingMyResponseDialog.class);
        int position = Integer.parseInt(String.valueOf(event.getId()));
        slotDialogIntent.putExtra("objectId", String.valueOf(slot.get(position).getObjectId()));
        startActivity(slotDialogIntent);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
//        dialog = new AlertDialog.Builder(getApplicationContext())
//                .setTitle("Go to event")
//                .setMessage("You about to go to " + slot.get(position).getSubject())
//                .setPositiveButton("GOING", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        dialog.dismiss();
//                        ringProgressDialog = ProgressDialog.show(getApplicationContext(), "Please wait ...",
//                                "Going to " + slot.get(position).getSubject() + " ...", true);
//                        ringProgressDialog.setCancelable(false);
//                        new GoingToEvent(position).execute();
//                    }
//                })
//                .setNegativeButton("NOT GOING", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//                        ringProgressDialog = ProgressDialog.show(getApplicationContext(), "Please wait ...",
//                                "Not going to" + slot.get(position).getSubject() + " ...", true);
//                        ringProgressDialog.setCancelable(false);
//                        new NotGoingToEvent(position).execute();
//                    }
//                }).setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();
        if (slot != null) {
            if (!slot.isEmpty()) {
                int i = 0;
                Iterator itr = slot.iterator();
                while (itr.hasNext()) {
                    Slot event = (Slot) itr.next();

                    WeekViewEvent weekViewEvent = new WeekViewEvent(Long.parseLong(String.valueOf(i)), event.getSubject(),
                            event.getStartCalendar().get(Calendar.YEAR), newMonth,
                            event.getStartCalendar().get(Calendar.DAY_OF_YEAR),
                            event.getStartCalendar().get(Calendar.HOUR_OF_DAY),
                            event.getStartCalendar().get(Calendar.MINUTE),
                            event.getStartCalendar().get(Calendar.YEAR), newMonth,
                            event.getEndCalendar().get(Calendar.DAY_OF_YEAR),
                            event.getEndCalendar().get(Calendar.HOUR_OF_DAY),
                            event.getStartCalendar().get(Calendar.MINUTE));
                    if (event.getLocation().getMetadata("category").equals("Social Event")) {
                        weekViewEvent.setColor(getResources().getColor(R.color.green));
                    } else if (event.getLocation().getMetadata("category").equals("Work Event")) {
                        weekViewEvent.setColor(getResources().getColor(R.color.orange));
                    } else if (event.getLocation().getMetadata("category").equals("Cultural Event")) {
                        weekViewEvent.setColor(getResources().getColor(R.color.wallet_holo_blue_light));
                    } else if (event.getLocation().getMetadata("category").equals("Academic Event")) {
                        weekViewEvent.setColor(getResources().getColor(R.color.purple));
                    }
                    i++;
                    events.add(weekViewEvent);
                }
            }
        }
        return events;
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
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
            whereClause.append("Person[pendingResponseSlot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();

            eventList = new ArrayList<>();

            Calendar now = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            now.setTimeZone(tz);

            getEventsFromList(slot);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mWeekView.notifyDatasetChanged();

            CalendarPickerController mPickerController = new CalendarPickerController() {
                @Override
                public void onDaySelected(DayItem dayItem) {
                }

                @Override
                public void onEventSelected(CalendarEvent event) {

                    if (!event.getTitle().equals("No events")) {

                        Intent slotDialogIntent = new Intent(SlotsAwaitingMyResponse.this, SlotsPendingMyResponseDialog.class);

                        int position = Integer.parseInt(String.valueOf(event.getId()));
                        slotDialogIntent.putExtra("origin", 2);
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

            minDate.add(Calendar.MONTH, -2);
            minDate.set(Calendar.DAY_OF_MONTH, 1);
            maxDate.add(Calendar.YEAR, 1);


            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), mPickerController);
            progressBar.setVisibility(View.GONE);
            RLProgressBar.setVisibility(View.GONE);
            linearLayoutWeekView.setVisibility(View.VISIBLE);
            mWeekView.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id) {
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
            case R.id.action_switch:
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        if (weekview) {
            inflater.inflate(R.menu.menu_week_view, menu);
            linearLayoutCalendarView.setVisibility(View.GONE);
            linearLayoutWeekView.setVisibility(View.VISIBLE);
        } else {
            inflater.inflate(R.menu.menu_events, menu);
            linearLayoutWeekView.setVisibility(View.GONE);
            linearLayoutCalendarView.setVisibility(View.VISIBLE);
        }
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

    private void getEventsFromList(List<Slot> eventListSlots) {

        for (int i = 0; i < eventListSlots.size(); i++) {

            Boolean allDay = false;

            if (eventListSlots.get(i).isAllDayEvent()) {
                allDay = true;
            }

            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            startTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getStartCalendar().get(Calendar.DAY_OF_YEAR));

            endTime.set(Calendar.DAY_OF_YEAR, eventListSlots.get(i).getEndCalendar().get(Calendar.DAY_OF_YEAR));


            int color = ContextCompat.getColor(this, R.color.green);

            if (eventListSlots.get(i).getLocation().getMetadata("category").equals("Social Event")) {
                color = ContextCompat.getColor(this, R.color.green);
            } else if (eventListSlots.get(i).getLocation().getMetadata("category").equals("Work Event")) {
                color = ContextCompat.getColor(this, R.color.orange);
            } else if (eventListSlots.get(i).getLocation().getMetadata("category").equals("Cultural Event")) {
                color = ContextCompat.getColor(this, R.color.wallet_holo_blue_light);
            } else if (eventListSlots.get(i).getLocation().getMetadata("category").equals("Academic Event")) {
                color = ContextCompat.getColor(this, R.color.purple);
            }

            String location = (String) eventListSlots.get(i).getLocation().getMetadata("address");
            CalendarEvent event = new CalendarEvent(eventListSlots.get(i).getSubject(),
                    eventListSlots.get(i).getMessage(), location,
                    color, startTime, endTime, allDay);

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
    public void invalidateOptionsMenu() {

        weekview = (weekview) ? false : true;

        super.invalidateOptionsMenu();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
}