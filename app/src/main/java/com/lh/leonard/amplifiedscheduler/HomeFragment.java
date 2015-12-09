package com.lh.leonard.amplifiedscheduler;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    BackendlessCollection<Person> persons;
    AutoResizeTextView textViewNotificationNumberHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        textViewNotificationNumberHome = (AutoResizeTextView) v.findViewById(R.id.textViewNotificationNumberHome);

        textViewNotificationNumberHome.setText("Fetching  notifications");

        Backendless.Data.mapTableToClass("Person", Person.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");


        // Monitor launch times and interval from installation
        RateThisApp.onStart(getActivity());
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(getActivity());


        AutoResizeTextView welcomeLabel = (AutoResizeTextView) v.findViewById(R.id.textViewWelcomeLabel);

        AutoResizeTextView textViewNotificationNumberHome = (AutoResizeTextView) v.findViewById(R.id.textViewNotificationNumberHome);
        ImageView imageViewMainLogo = (ImageView) v.findViewById(R.id.imageViewMainLogo);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //Fame
        if (width == 320 && height == 480) {
            imageViewMainLogo.requestLayout();
            imageViewMainLogo.getLayoutParams().height = 140;
            welcomeLabel.setTextSize(22);
            welcomeLabel.setPadding(0, 20, 0, 35);
            textViewNotificationNumberHome.setTextSize(22);
        }
        // 2.7" QVGA
        else if (width == 240 && height == 320) {
            imageViewMainLogo.requestLayout();
            imageViewMainLogo.getLayoutParams().height = 100;
            welcomeLabel.setTextSize(18);
            welcomeLabel.setPadding(0, 7, 0, 10);
            textViewNotificationNumberHome.setTextSize(18);
        }

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        // final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "RobotoCondensed-Light.ttf");

        textViewNotificationNumberHome.setTypeface(RobotoBlack);

        welcomeLabel.setTypeface(RobotoCondensedLightItalic);

        welcomeLabel.setText("Welcome " + personLoggedIn.getFullname() + "!");

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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            int personsRequestingMe = personLoggedIn.getPersonsRequestingMe().size();
            int invitedEvents = personLoggedIn.getPendingResponseSlot().size();

            personLoggedIn.getPendingResponseSlot().size();

            if (personsRequestingMe >= 1 || invitedEvents >= 1) {

                textViewNotificationNumberHome.setText(String.valueOf((personsRequestingMe + invitedEvents) + " Notifications"));
                textViewNotificationNumberHome.setTextColor(Color.RED);

            } else {
                textViewNotificationNumberHome.setText("No new notifications");
            }
        }
    }
}