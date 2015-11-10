package com.lh.leonard.amplifiedscheduler;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    Person personLoggedIn;
    BackendlessCollection<Person> persons;
    AutoResizeTextView textViewNotificationNumberHome;
    //  Typeface fontHomeName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        textViewNotificationNumberHome = (AutoResizeTextView) v.findViewById(R.id.textViewNotificationNumberHome);

        textViewNotificationNumberHome.setText("Fetching  notifications");

        getActivity().setTitle("Home");

        Backendless.Data.mapTableToClass("Person", Person.class);

        personLoggedIn = (Person) userLoggedIn.getProperty("persons");


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
            imageViewMainLogo.getLayoutParams().height = 160;
            welcomeLabel.setTextSize(22);
            welcomeLabel.setPadding(0, 7, 0, 15);

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


        // final AutoResizeTextView textViewAppStatement = (AutoResizeTextView) v.findViewById(R.id.textViewAppStatement);

        //  final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        // final Typeface fontWelcome = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Amatic-Bold.ttf");

        // fontHomeName = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SEASRN__.ttf");

        //  textViewNotificationNumberHome.setTypeface(regularFont);

        // welcomeLabel.setTypeface(regularFont);
        //    textViewAppStatement.setTypeface(fontWelcome);

        welcomeLabel.setText("Welcome " + personLoggedIn.getFullname() + "!");

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

            if (isAdded()) {

                ArrayList<String> relationProps = new ArrayList<>();
                // relationProps.add("unseenSlots");
                relationProps.add("personsRequestingMe");
                // relationProps.add("goingToSlot");
                // relationProps.add("myCreatedSlot");
                relationProps.add("pendingResponseSlot");
                Backendless.Data.of(Person.class).loadRelations(personLoggedIn, relationProps);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            int personsRequestingMe = personLoggedIn.getPersonsRequestingMe().size();
            int invitedEvents = personLoggedIn.getPendingResponseSlot().size();

            if (personsRequestingMe >= 1 || invitedEvents >= 1) {

                textViewNotificationNumberHome.setText(String.valueOf((personsRequestingMe + invitedEvents) + " Notifications"));
                textViewNotificationNumberHome.setTextColor(Color.RED);

//                textViewNotificationNumberHome.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Fragment unseenSlotsFragment = new UnseenSlotsFragment();
//
//                        FragmentManager fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.frame_container, unseenSlotsFragment).addToBackStack("home").commit();
//                    }
//                });

                textViewNotificationNumberHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent n = new Intent(getActivity(), NavDrawerActivity.class);
                        n.putExtra("refresh", true);
                        startActivity(n);
                    }
                });

            } else {
                textViewNotificationNumberHome.setText("No new notifications - tap to refresh");
                textViewNotificationNumberHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent n = new Intent(getActivity(), NavDrawerActivity.class);
                        n.putExtra("refresh", false);
                        startActivity(n);
                    }
                });
            }
            // Toast.makeText(getContext(), "Check", Toast.LENGTH_SHORT).show();
            //  new ParseURL().execute();
        }
    }

}
