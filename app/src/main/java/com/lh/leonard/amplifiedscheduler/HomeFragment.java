package com.lh.leonard.amplifiedscheduler;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    BackendlessCollection<Person> persons;
    ArrayAdapter<String> adapter;
    String my_var;
    ProgressDialog ringProgressDialog;
    AlertDialog alert = null;
    ImageView imageViewNotification;
    View v;
    AutoResizeTextView textViewMyEvents;
    AutoResizeTextView textViewGoingToEvents;
    AutoResizeTextView textViewInvitedEvent;
    AutoResizeTextView textViewMyEventsDate;
    AutoResizeTextView textViewGoingToEventsDate;
    AutoResizeTextView textViewInvitedEventDate;
    AutoResizeTextView textViewLatestEvents;
    ImageView imageViewInvitedEvents;
    ImageView imageViewMyEvents;
    ImageView imageViewGoingToEvents;
    Drawable drawableTime;
    RelativeLayout RLProgressBar;
    LinearLayout llEventsForm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

        Backendless.Data.mapTableToClass("Person", Person.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        // Monitor launch times and interval from installation
        RateThisApp.onStart(getActivity());
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(getActivity());

        imageViewNotification = (ImageView) v.findViewById(R.id.imageViewNotification);

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
        ImageView imageViewWhichUser = (ImageView) v.findViewById(R.id.imageViewWhichUser);
        RLProgressBar = (RelativeLayout) v.findViewById(R.id.RLProgressBar);
        llEventsForm = (LinearLayout) v.findViewById(R.id.llEventsForm);

        Drawable drawableUserNonFacebook = ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_logged);
        Drawable drawableUserFacebook = ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_facebook);

        drawableTime = ContextCompat.getDrawable(getActivity(), R.drawable.ic_time);

        if (personLoggedIn.getSocial() != null) {
            if (personLoggedIn.getSocial().equals("Facebook")) {
                imageViewWhichUser.setImageDrawable(drawableUserFacebook);
            } else {
                imageViewWhichUser.setImageDrawable(drawableUserNonFacebook);
            }
        } else {
            imageViewWhichUser.setImageDrawable(drawableUserNonFacebook);
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //Fame
        if (width == 320 && height == 480) {
            imageViewNotification.requestLayout();
            imageViewNotification.getLayoutParams().height = 50;
            textViewLoggedIn.setTextSize(22);
            textViewLoggedIn.setPadding(0, 20, 0, 35);
            textViewMyEvents.setTextSize(16);
        }
        // 2.7" QVGA
        else if (width == 240 && height == 320) {
            imageViewNotification.requestLayout();
            imageViewNotification.getLayoutParams().height = 500;
            textViewLoggedIn.setTextSize(18);
            textViewLoggedIn.setPadding(0, 7, 0, 10);
            textViewMyEvents.setTextSize(12);
        }

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        textViewLatestEvents.setTypeface(RobotoCondensedBold);
        textViewGoingToEventsDate.setTypeface(RobotoCondensedLightItalic);
        textViewMyEventsDate.setTypeface(RobotoCondensedLightItalic);
        textViewInvitedEventDate.setTypeface(RobotoCondensedLightItalic);
        textViewMyEvents.setTypeface(RobotoCondensedBold);
        textViewGoingToEvents.setTypeface(RobotoCondensedBold);
        textViewInvitedEvent.setTypeface(RobotoCondensedBold);
        textViewLoggedIn.setTypeface(RobotoCondensedBold);

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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            int personsRequestingMe = personLoggedIn.getPersonsRequestingMe().size();
            int invitedEvents = personLoggedIn.getPendingResponseSlot().size();

            if (!personLoggedIn.getMyCreatedSlot().isEmpty()) {
                textViewMyEvents.setText(personLoggedIn.getMyCreatedSlot().get(0).getSubject());
                textViewMyEventsDate.setText(personLoggedIn.getMyCreatedSlot().get(0).getStartCalendar().getTime().toString());
                imageViewMyEvents.setImageDrawable(drawableTime);
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
            if (personsRequestingMe >= 1 || invitedEvents >= 1) {
                Drawable drawableNotification = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_noification);
                imageViewNotification.setImageDrawable(drawableNotification);
            } else {
                Drawable drawableNoNotification = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_no_noification);
                imageViewNotification.setImageDrawable(drawableNoNotification);
            }
            RLProgressBar.setVisibility(View.GONE);
            llEventsForm.setVisibility(View.VISIBLE);
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