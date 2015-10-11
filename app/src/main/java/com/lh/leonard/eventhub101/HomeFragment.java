package com.lh.leonard.eventhub101;


import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    BackendlessCollection<Person> persons;
    AutoResizeTextView textViewNotificationNumberHome;
    Typeface fontHomeName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Home");

        textViewNotificationNumberHome = (AutoResizeTextView) v.findViewById(R.id.textViewNotificationNumberHome);

        Backendless.Data.mapTableToClass("Contact", Contact.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        final AutoResizeTextView welcomeLabel = (AutoResizeTextView) v.findViewById(R.id.textViewWelcomeLabel);

        final AutoResizeTextView textViewNotificationNumberHome = (AutoResizeTextView) v.findViewById(R.id.textViewNotificationNumberHome);

        final AutoResizeTextView homeLogo = (AutoResizeTextView) v.findViewById(R.id.textViewHomeLogo);

        final AutoResizeTextView textViewAppStatement = (AutoResizeTextView) v.findViewById(R.id.textViewAppStatement);

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        final Typeface fontWelcome = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Amatic-Bold.ttf");

        fontHomeName = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SEASRN__.ttf");

        textViewNotificationNumberHome.setTypeface(regularFont);

        homeLogo.setTypeface(fontWelcome);
        welcomeLabel.setTypeface(regularFont);
        textViewAppStatement.setTypeface(fontWelcome);

        welcomeLabel.setText("Welcome! " + personLoggedIn.getFname() + " " + personLoggedIn.getLname());

        new ParseURL().execute();

        return v;
    }


    private class ParseURL extends AsyncTask<Void, Integer, Void> {

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
        protected Void doInBackground(Void... params) {


            ArrayList<String> relationProps = new ArrayList<String>();
            relationProps.add("unseenSlots");
            Backendless.Data.of(Person.class).loadRelations(personLoggedIn, relationProps);

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            if (personLoggedIn.numberUnseenSlots() >= 1) {

                textViewNotificationNumberHome.setText(String.valueOf(personLoggedIn.numberUnseenSlots()) + " New Event Invites (under construction)");
                textViewNotificationNumberHome.setTextColor(Color.RED);

                textViewNotificationNumberHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Fragment unseenSlotsFragment = new UnseenSlotsFragment();

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_container, unseenSlotsFragment).addToBackStack("home").commit();
                    }
                });

            } else {
                textViewNotificationNumberHome.setText("No New Notifications (under construction)");
            }
        }
    }
}
