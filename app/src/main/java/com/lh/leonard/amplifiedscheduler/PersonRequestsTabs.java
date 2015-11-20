package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp1 on 21-01-2015.
 */
public class PersonRequestsTabs extends Fragment {

    List<Person> person;
    String removedFullname;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    BackendlessCollection<Person> personRequestsBackendlessCollection;

    RecyclerView rvRequest;
    LinearLayoutManager llm;
    View v;
    int val;
    SearchView searchView;
    ContactsAdapter adapterRequest;
    List<Person> personsRequestsList;
    private ProgressBar progressBarRequesting;
    AutoResizeTextView textViewTextNoRequestingUsers;
    ProgressDialog ringProgressDialog;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.person_requests_tab2, container, false);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);


        rvRequest = (RecyclerView) v.findViewById(R.id.rvRequests);
        llm = new LinearLayoutManager(v.getContext());

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        progressBarRequesting = (ProgressBar) v.findViewById(R.id.progressBarPersonRequestTab);
        searchView = (SearchView) v.findViewById(R.id.searchViewContactRequest);
        textViewTextNoRequestingUsers = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoRequestingUsers);
        textViewTextNoRequestingUsers.setTypeface(RobotoCondensedLightItalic);

        searchView.setQueryHint("Search requesting contacts");

        new ParseURL().execute();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              public boolean onQueryTextChange(String text) {
                                                  if (TextUtils.isEmpty(text)) {
                                                      if (adapterRequest != null) {
                                                          adapterRequest.getFilter().filter("");
                                                      }
                                                  } else {
                                                      adapterRequest.getFilter().filter(text.toString());
                                                  }
                                                  return true;
                                              }

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  return false;
                                              }
                                          }
        );

        return v;
    }


    private class ParseURL extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchView.setVisibility(View.GONE);
            rvRequest.setVisibility(View.GONE);
            textViewTextNoRequestingUsers.setVisibility(View.GONE);
            progressBarRequesting.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[personsRequestingMe]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            personRequestsBackendlessCollection = Backendless.Data.of(Person.class).find(dataQuery);

            personsRequestsList = personRequestsBackendlessCollection.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (isAdded()) {

                if (!personsRequestsList.isEmpty()) {
                    rvRequest.setHasFixedSize(true);

                    rvRequest.setLayoutManager(llm);

                    Resources r = getResources();

                    adapterRequest = new ContactsAdapter(personsRequestsList, r);

                    rvRequest.setAdapter(adapterRequest);

                    rvRequest.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rvRequest, new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, final int position) {


                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Accept Contact Request")
                                    .setMessage("Do you want to  accept " + personsRequestsList.get(position).getFullname() + " as a contact")
                                    .setIcon(R.drawable.ic_questionmark)
                                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Adding " + personsRequestsList.get(position).getFullname() + " to your contacts ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new YesRequest(position).execute();
                                        }
                                    })
                                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Removing " + personsRequestsList.get(position).getFullname() + " from your contact requests ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new NoRequest(val, position).execute();

                                        }
                                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            // ...

                            //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                            //TODO Yes: get the ownerObjectId and remove from database
                        }
                    }
                    ));

                    progressBarRequesting.setVisibility(View.GONE);
                    rvRequest.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    progressBarRequesting.setVisibility(View.GONE);
                    textViewTextNoRequestingUsers.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private class YesRequest extends AsyncTask<Void, Integer, Void> {

        int position;

        public YesRequest(int position) {
            this.position = position;
        }

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

            if (personsRequestsList.get(position) != null) {

                //Accept contact Request
                Map<String, String> args = new HashMap<>();

                args.put("id", "acceptContactRequest");

                args.put("loggedinperson", personLoggedIn.getObjectId());

                args.put("otherperson", personsRequestsList.get(position).getObjectId());

                Backendless.Events.dispatch("ManageContact", args);

            }

            List<String> relationsForLoggedInPerson = new ArrayList<String>();
            relationsForLoggedInPerson.add("personsImRequesting");
            relationsForLoggedInPerson.add("personsRequestingMe");
            relationsForLoggedInPerson.add("contacts");

            personLoggedIn = Backendless.Persistence.of(Person.class).findById(personLoggedIn.getObjectId(), relationsForLoggedInPerson);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvRequest.setAdapter(null);

            if (!personsRequestsList.isEmpty()) {

                rvRequest.setHasFixedSize(true);

                rvRequest.setLayoutManager(llm);

                //   rvRequest.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapterRequest = new ContactsAdapter(personsRequestsList, r);

                rvRequest.setAdapter(adapterRequest);
            } else {

                searchView.setVisibility(View.GONE);
                rvRequest.setVisibility(View.GONE);
                progressBarRequesting.setVisibility(View.GONE);
                textViewTextNoRequestingUsers.setVisibility(View.VISIBLE);
            }
            ringProgressDialog.dismiss();
            Toast.makeText(v.getContext(), removedFullname + " added as a contact", Toast.LENGTH_SHORT).show();
        }
    }

    private class NoRequest extends AsyncTask<Void, Integer, Void> {

        int value;
        int positionInList;

        public NoRequest(int value, int positionInList) {
            this.value = value;
            this.positionInList = positionInList;
        }

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


            if (personsRequestsList.get(positionInList) != null) {

                //Decline contact Request
                Map<String, String> args = new HashMap<>();

                removedFullname = personsRequestsList.get(positionInList).getFullname();

                args.put("id", "declineRequest");

                args.put("loggedinperson", personLoggedIn.getObjectId());

                args.put("otherperson", personsRequestsList.get(positionInList).getObjectId());

                personsRequestsList.remove(positionInList);

                Backendless.Events.dispatch("ManageContact", args);

            }
            List<String> relationsForLoggedInPerson = new ArrayList<String>();
            relationsForLoggedInPerson.add("personsImRequesting");
            relationsForLoggedInPerson.add("personsRequestingMe");
            relationsForLoggedInPerson.add("contacts");

            personLoggedIn = Backendless.Persistence.of(Person.class).findById(personLoggedIn.getObjectId(), relationsForLoggedInPerson);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvRequest.setAdapter(null);

            if (!personsRequestsList.isEmpty()) {

                rvRequest.setHasFixedSize(true);

                rvRequest.setLayoutManager(llm);

                // rvRequest.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapterRequest = new ContactsAdapter(personsRequestsList, r);

                rvRequest.setAdapter(adapterRequest);
            } else {
                searchView.setVisibility(View.GONE);
                rvRequest.setVisibility(View.GONE);
                textViewTextNoRequestingUsers.setVisibility(View.VISIBLE);
            }
            ringProgressDialog.dismiss();
            Toast.makeText(v.getContext(), removedFullname + " rejected as contact", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {

        new ParseURL().execute();


        super.onResume();
    }
}