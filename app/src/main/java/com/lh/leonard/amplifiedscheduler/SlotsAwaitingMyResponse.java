package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

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
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    AlertDialog dialog;
    ProgressDialog ringProgressDialog;
    private ProgressBar progressBar;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    private Toolbar toolbar;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekview);

        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        personLoggedIn = (Person) userLoggedIn.getProperty("persons");


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
        new ParseURL().execute();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        Intent slotDialogIntent = new Intent(SlotsAwaitingMyResponse.this, SlotsPendingMyResponseDialog.class);

        int position = Integer.parseInt(String.valueOf(event.getId()));
        slotDialogIntent.putExtra("origin", 1);
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
                    weekViewEvent.setColor(getResources().getColor(R.color.red));
                    i++;
                    events.add(weekViewEvent);
                }
            }
        }
        return events;
    }

    private class ParseURL extends AsyncTask<Void, Integer, Void> {

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
            System.out.println("do in background");
            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[pendingResponseSlot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();
            Calendar now = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            now.setTimeZone(tz);

            for (int j = 0; j < slot.size(); j++) {

                if (slot.get(j).getMaxattendees() != 0) {
                    if (slot.get(j).attendees.size() >= slot.get(j).getMaxattendees()) {
                        slot.remove(j);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mWeekView.notifyDatasetChanged();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_week_view, menu);
        return true;
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

                return true;
            case R.id.action_switch:
                startActivity(new Intent(SlotsAwaitingMyResponse.this, SlotsAwaitingMyResponseCalendar.class));
        }

        return super.onOptionsItemSelected(item);
    }

//    private class NotGoingToEvent extends AsyncTask<Void, Integer, Void> {
//
//        public NotGoingToEvent(int positionInList) {
//
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//
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
//            Map<String, String> args = new HashMap<>();
//            args.put("id", "declineinviteevent");
//
//            args.put("objectIdPerson", personLoggedIn.getObjectId());
//
//            args.put("event", slot.get(positionInList).getObjectId());
//
//            Backendless.Events.dispatch("ManageEvent", args, new AsyncCallback<Map>() {
//                @Override
//                public void handleResponse(Map map) {
//                    dialog.dismiss();
//                    onBackPressed();
//
//                }
//
//                @Override
//                public void handleFault(BackendlessFault backendlessFault) {
//
//                    dialog.dismiss();
//                }
//            });
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//
//                ringProgressDialog.dismiss();
//                progressBar.setVisibility(View.GONE);
//
//            Toast.makeText(getApplicationContext(), eventRemoved + " was declined", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private class GoingToEvent extends AsyncTask<Void, Integer, Void> {
//
//        int position;
//
//        public GoingToEvent(int position) {
//            this.position = position;
//        }
//
//        @Override
//        protected void onPreExecute() {
//
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
//            List<String> relations = new ArrayList<String>();
//            relations.add("pendingResponseSlot");
//            Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);
//
//            List<String> relationsSlot = new ArrayList<String>();
//            relations.add("attendees");
//            Slot slotAddAttendee = Backendless.Data.of(Slot.class).findById(slot.get(position), relationsSlot);
//
//            int pos = 0;
//
//            for (int i = 0; i < person.pendingResponseSlot.size(); i++) {
//
//                if (person.pendingResponseSlot.get(i).getObjectId().equals(slot.get(position).getObjectId())) {
//                    pos = i;
//                    break;
//                }
//            }
//
//
//            // sendsmss(slot.get(position).getPhone(), "Automated TXT - Amplified Schedule" + person.getFullname() + "  has indicated he/she is going to your " + slot.get(position).getSubject() + " event on the " + slot.get(position).getDateofslot());
//
//            person.pendingResponseSlot.remove(pos);
//
//            eventRemoved = slot.get(position).getSubject();
//
//            Backendless.Data.of(Person.class).save(person);
//
//            Person p = Backendless.Data.of(Person.class).findById(personLoggedIn);
//
//            p.addSlotGoingToSlot(slot.get(position));
//
//            slotAddAttendee.addAttendee(person);
//
//            Backendless.Data.of(Slot.class).save(slotAddAttendee);
//
//            slot.remove(position);
//            personLoggedIn = Backendless.Data.of(Person.class).save(p);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//
//            rv.setAdapter(null);
//
//            if (!slot.isEmpty()) {
//
//                rv.setAdapter(null);
//
//                rv.setHasFixedSize(true);
//
//                rv.setLayoutManager(llm);
//
//                // rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
//
//                Resources r = getResources();
//
//                adapter = new RVAdapter(slot, r);
//
//                rv.setAdapter(adapter);
//
//                ringProgressDialog.dismiss();
//
//            } else {
//                ringProgressDialog.dismiss();
//                searchViewSlots.setVisibility(View.GONE);
//                rv.setVisibility(View.GONE);
//                progressBar.setVisibility(View.GONE);
//                textViewTextNoSlotAvaliable.setVisibility(View.VISIBLE);
//            }
//            Toast.makeText(getApplicationContext(), "Going to " + eventRemoved, Toast.LENGTH_SHORT).show();
//        }
//    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String message) {

        int lengthToSubString;
        int lengthMessage = message.length();
        if (lengthMessage < 300) {
            lengthToSubString = lengthMessage;
        } else {
            lengthToSubString = 300;
        }
        String messageSubString = message.substring(0, lengthToSubString);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
    }

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
