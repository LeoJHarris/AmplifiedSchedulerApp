package com.lh.leonard.amplifiedscheduler;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.kobakei.ratethisapp.RateThisApp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.jodamob.android.calendar.CalendarDataFactory;
import de.jodamob.android.calendar.CalenderWidget;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Bitmap mIcon_val;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    BackendlessCollection<Person> persons;
    ArrayAdapter<String> adapter;
    String my_var;
    ProgressDialog ringProgressDialog;
    AlertDialog alert = null;
    View v;
    AutoResizeTextView textViewMyEvents;
    AutoResizeTextView textViewGoingToEvents;
    AutoResizeTextView textViewInvitedEvent;
    AutoResizeTextView textViewMyEventsDate;
    AutoResizeTextView textViewGoingToEventsDate;
    AutoResizeTextView textViewInvitedEventDate;
    AutoResizeTextView textViewLatestEvents;
    AutoResizeTextView textViewMyPlans;
    AutoResizeTextView textViewMyPlansDate;
    ImageView imageViewInvitedEvents;
    ImageView imageViewMyEvents;
    ImageView imageViewGoingToEvents;
    Drawable drawableTime;
    ImageView imageViewMyPlans;
    RelativeLayout contentHome;
    RelativeLayout progressBar;
    List<Schedule> schedulesForStyledCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Plan", Plan.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        // Monitor launch times and interval from installation
        RateThisApp.onStart(getActivity());
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(getActivity());

        AutoResizeTextView textViewLoggedIn = (AutoResizeTextView) v.findViewById(R.id.textViewLoggedIn);

        textViewMyEvents = (AutoResizeTextView) v.findViewById(R.id.textViewMyEvents);
        textViewGoingToEvents = (AutoResizeTextView) v.findViewById(R.id.textViewGoingToEvents);
        textViewInvitedEvent = (AutoResizeTextView) v.findViewById(R.id.textViewInvitedEvent);
        textViewMyEventsDate = (AutoResizeTextView) v.findViewById(R.id.textViewMyEventsDate);
        textViewGoingToEventsDate = (AutoResizeTextView) v.findViewById(R.id.textViewGoingToEventsDate);
        textViewInvitedEventDate = (AutoResizeTextView) v.findViewById(R.id.textViewInvitedEventDate);
        textViewLatestEvents = (AutoResizeTextView) v.findViewById(R.id.textViewLatestEvents);
        imageViewInvitedEvents = (ImageView) v.findViewById(R.id.imageViewInvitedEvents);
        imageViewMyEvents = (ImageView) v.findViewById(R.id.imageViewMyEvents);
        imageViewGoingToEvents = (ImageView) v.findViewById(R.id.imageViewGoingToEvents);
        textViewMyPlans = (AutoResizeTextView) v.findViewById(R.id.textViewMyPlans);
        textViewMyPlansDate = (AutoResizeTextView) v.findViewById(R.id.textViewMyPlansDate);
        imageViewMyPlans = (ImageView) v.findViewById(R.id.imageViewMyPlans);
        progressBar = (RelativeLayout) v.findViewById(R.id.progressBar);

        contentHome = (RelativeLayout) v.findViewById(R.id.contentHome);

        Drawable drawableUserNonFacebook = ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_logged);
        Drawable drawableUserFacebook = ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_facebook);

        drawableTime = ContextCompat.getDrawable(getActivity(), R.drawable.ic_time);

        if (personLoggedIn.getSocial() != null) {
            if (personLoggedIn.getSocial().equals("Facebook")) {
                textViewLoggedIn.setCompoundDrawablesWithIntrinsicBounds(drawableUserFacebook, null, null, null);
            } else {
                textViewLoggedIn.setCompoundDrawablesWithIntrinsicBounds(drawableUserNonFacebook, null, null, null);
            }
        } else {
            textViewLoggedIn.setCompoundDrawablesWithIntrinsicBounds(drawableUserNonFacebook, null, null, null);
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //Fame
        if (width == 320 && height == 480) {

        }
        // 2.7" QVGA
        else if (width == 240 && height == 320) {

        }

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewMyPlansDate.setTypeface(RobotoCondensedLight);
        textViewLatestEvents.setTypeface(RobotoCondensedBold);
        textViewGoingToEventsDate.setTypeface(RobotoCondensedLightItalic);
        textViewMyEventsDate.setTypeface(RobotoCondensedLightItalic);
        textViewInvitedEventDate.setTypeface(RobotoCondensedLightItalic);
        textViewMyEvents.setTypeface(RobotoCondensedLight);
        textViewGoingToEvents.setTypeface(RobotoCondensedLight);
        textViewInvitedEvent.setTypeface(RobotoCondensedLight);
        textViewLoggedIn.setTypeface(RobotoCondensedLight);
        textViewMyEventsDate.setTypeface(RobotoCondensedLight);
        textViewMyEvents.setTypeface(RobotoCondensedLight);

        textViewLoggedIn.setText(personLoggedIn.getFullname());

        if (personLoggedIn.getCountry() == null || personLoggedIn.getCountry().equals("")) {
            phoneCountryDialog();
        } else if (personLoggedIn.getPhone() == null || personLoggedIn.getPhone().equals("")) {
            phoneDialog();
        }

        new ParseURL().execute();
        return v;
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

            if (isAdded()) {

                ArrayList<String> relationProps = new ArrayList<>();
                relationProps.add("personsRequestingMe");
                relationProps.add("contacts");
                relationProps.add("personsImRequesting");
                relationProps.add("goingToSlot");
                relationProps.add("myCreatedSlot");
                relationProps.add("myPlans");
                relationProps.add("pendingResponseSlot");
                Backendless.Data.of(Person.class).loadRelations(personLoggedIn, relationProps);

                Collections.sort(personLoggedIn.getMyCreatedSlot(), new Comparator<Slot>() {
                    public int compare(Slot e1, Slot e2) {
                        if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                            return 0;
                        return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                    }
                });
                Collections.sort(personLoggedIn.getPendingResponseSlot(), new Comparator<Slot>() {
                    public int compare(Slot e1, Slot e2) {
                        if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                            return 0;
                        return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                    }
                });
                Collections.sort(personLoggedIn.getGoingToSlot(), new Comparator<Slot>() {
                    public int compare(Slot e1, Slot e2) {
                        if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                            return 0;
                        return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                    }
                });
                Collections.sort(personLoggedIn.getMyPlans(), new Comparator<Plan>() {
                    public int compare(Plan e1, Plan e2) {
                        if (e1.getStartCalendar().getTime() == null || e2.getStartCalendar().getTime() == null)
                            return 0;
                        return e1.getStartCalendar().getTime().compareTo(e2.getStartCalendar().getTime());
                    }
                });

                schedulesForStyledCalendar = new ArrayList<>();
                schedulesForStyledCalendar.addAll(personLoggedIn.getMyCreatedSlot());
                schedulesForStyledCalendar.addAll(personLoggedIn.getGoingToSlot());
                schedulesForStyledCalendar.addAll(personLoggedIn.getMyPlans());

//                URL newurl = null;
//                try {
//                    newurl = new URL(personLoggedIn.getPicture());
//
//                     mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
//
//
//                    // imageViewMyEvents.setIma();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Calendar now = Calendar.getInstance();

            CalenderWidget widget = (CalenderWidget) v.findViewById(R.id.calendar);
            widget.set(CalendarDataFactory.getInstance(Locale.getDefault()).create(now.getTime(), 4),
                    new StyledCalendarBuilder(schedulesForStyledCalendar));

            int personsRequestingMe = personLoggedIn.getPersonsRequestingMe().size();
            int invitedEvents = personLoggedIn.getPendingResponseSlot().size();

            if (!personLoggedIn.getMyCreatedSlot().isEmpty()) {
                textViewMyEvents.setText(personLoggedIn.getMyCreatedSlot().get(0).getSubject());
                textViewMyEventsDate.setText(personLoggedIn.getMyCreatedSlot().get(0).getStartCalendar().getTime().toString());
                imageViewMyEvents.setImageDrawable(drawableTime);

               // imageViewMyEvents.setImageBitmap(mIcon_val);
            }
            if (!personLoggedIn.getGoingToSlot().isEmpty()) {
                textViewGoingToEvents.setText(personLoggedIn.getGoingToSlot().get(0).getSubject());
                textViewGoingToEventsDate.setText(personLoggedIn.getGoingToSlot().get(0).getStartCalendar().getTime().toString());
                imageViewGoingToEvents.setImageDrawable(drawableTime);
            }
            if (!personLoggedIn.getPendingResponseSlot().isEmpty()) {
                textViewInvitedEvent.setText(personLoggedIn.getPendingResponseSlot().get(0).getSubject());
                textViewInvitedEventDate.setText(personLoggedIn.getPendingResponseSlot().get(0).getStartCalendar().getTime().toString());
                imageViewInvitedEvents.setImageDrawable(drawableTime);
            }
            if (!personLoggedIn.getMyPlans().isEmpty()) {
                textViewMyPlans.setText(personLoggedIn.getMyPlans().get(0).getSubject());
                textViewMyPlansDate.setText(personLoggedIn.getMyPlans().get(0).getStartCalendar().getTime().toString());
                imageViewMyPlans.setImageDrawable(drawableTime);
            }
            if (personsRequestingMe >= 1 || invitedEvents >= 1) {

                // Notification

            } else {

            }
            progressBar.setVisibility(View.GONE);
            contentHome.setVisibility(View.VISIBLE);
        }
    }

    public void phoneDialog() {

        final EditText phone = new EditText(getActivity());

        phone.setHint("Phone");

        phone.setInputType(InputType.TYPE_CLASS_PHONE);

        final AlertDialog.Builder phoneAlert = new AlertDialog.Builder(getActivity())
                .setTitle("Final Details")
                .setMessage("Last step required, please provide your phone")
                .setView(phone)
                .setPositiveButton("DONE", null)
                .setNeutralButton("LOGOUT", null);
        phoneAlert.setCancelable(false);
        alert = phoneAlert.create(); //.show().setCancelable(false);

        alert.show();

        final Button neutralButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                        "Logging out " + personLoggedIn.getFname() + " " + personLoggedIn.getLname() + " ...", true);
                ringProgressDialog.setCancelable(false);
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    public void handleResponse(Void response) {
                        Intent logOutIntent = new Intent(getActivity(), MainActivity.class);
                        logOutIntent.putExtra("loggedoutperson", personLoggedIn.getFname() + "," + personLoggedIn.getLname());
                        startActivity(logOutIntent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getActivity(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent logOutIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(logOutIntent);
                    }
                });
            }
        });
        final Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View v) {
                                                  if ((!phone.getText().toString().equals(""))) {
                                                      new Commit(phone.getText().toString(), 2).execute();
                                                      alert.dismiss();
                                                      Toast.makeText(getActivity(), "Registration complete", Toast.LENGTH_SHORT).show();

                                                  } else {
                                                      phone.setFocusable(true);
                                                      Toast.makeText(getActivity(), "Please enter phone", Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          }

        );
    }

    public void phoneCountryDialog() {

        // Get a reference to the AutoCompleteTextView in the layout
        final AutoCompleteTextView textViewCountry = new AutoCompleteTextView(getActivity());
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
        // Create the adapter and set it to the AutoCompleteTextView
        adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, countries);
        textViewCountry.setAdapter(adapter);

        final AlertDialog.Builder countrySelectAlert = new AlertDialog.Builder(getActivity())
                .setTitle("Additional Details")
                .setMessage("Your nearly there, please provide your Country")
                .setView(textViewCountry)
                .setPositiveButton("NEXT", null)
                .setNeutralButton("LOGOUT", null);
        countrySelectAlert.setCancelable(false);
        alert = countrySelectAlert.create(); //.show().setCancelable(false);

        alert.show();

        final Button neutralButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                        "Logging out " + personLoggedIn.getFname() + " " + personLoggedIn.getLname() + " ...", true);
                ringProgressDialog.setCancelable(false);
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    public void handleResponse(Void response) {
                        Intent logOutIntent = new Intent(getActivity(), MainActivity.class);
                        logOutIntent.putExtra("loggedoutperson", personLoggedIn.getFname() + "," + personLoggedIn.getLname());
                        startActivity(logOutIntent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getActivity(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent logOutIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(logOutIntent);
                    }
                });
            }
        });
        final Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_var != null) {
                    new Commit(textViewCountry.getText().toString(), 1).execute();
                    alert.dismiss();
                    phoneDialog();
                } else {
                    textViewCountry.setFocusable(true);
                    Toast.makeText(getActivity(), "Please enter country", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listeners(textViewCountry);
    }

    public void listeners(AutoCompleteTextView textViewCountry) {
        textViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my_var = adapter.getItem(position).toString();
            }
        });

        textViewCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_var = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private class Commit extends AsyncTask<Void, Integer, Void> {

        String value;
        Integer intVal = 0;

        public Commit(String value, Integer i) {

            this.value = value;
            this.intVal = i;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (intVal == 1) {
                personLoggedIn.setCountry(value);
            } else if (intVal == 2) {
                personLoggedIn.setPhone(value);
            }
            Backendless.Data.of(Person.class).save(personLoggedIn);
            return null;
        }
    }
}