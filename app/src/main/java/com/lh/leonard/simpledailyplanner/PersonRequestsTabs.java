package com.lh.leonard.simpledailyplanner;

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
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class PersonRequestsTabs extends Fragment {

    List<Person> person;
    String removedFullname;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    BackendlessCollection<Person> personRequestsBackendlessCollection;

    List<Person> personsFoundQuery;

    RecyclerView rvRequest;
    LinearLayoutManager llm;
    View v;
    int val;
    SearchView searchView;
    ContactsAdapter adapterRequest;
    List<Person> personsRequestsList;
    private ProgressBar progressBarRequesting;
    AutoResizeTextView textViewTextNoRequestingUsers;
    Boolean added = false;
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

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        searchView = (SearchView) v.findViewById(R.id.searchViewContactRequest);
        textViewTextNoRequestingUsers = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoRequestingUsers);
        textViewTextNoRequestingUsers.setTypeface(regularFont);

        searchView.setQueryHint("Search Requesting Contacts");

        new ParseURL().execute();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              public boolean onQueryTextChange(String text) {
                                                  if (TextUtils.isEmpty(text)) {
                                                      adapterRequest.getFilter().filter("");
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
            progressBarRequesting = (ProgressBar) v.findViewById(R.id.progressBarPersonRequestTab);
            progressBarRequesting.setVisibility(View.VISIBLE);
            super.onPreExecute();
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

                                            added = true;
                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Adding " + personsRequestsList.get(position).getFullname() + " to your contact ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new YesRequest(position).execute();
                                        }
                                    })
                                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            added = false;
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

                //Remove from me

                List<String> relations = new ArrayList<String>();
                relations.add("personsRequestingMe");
                Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);
                System.out.println("Loaded object. Name - " + person.getFname() + ", relations - " + person.personsRequestingMe.size());

                int pos = 0;

                for (int i = 0; i < person.personsRequestingMe.size(); i++) {

                    if (person.personsRequestingMe.get(i).getObjectId().equals(personsRequestsList.get(position).getObjectId())) {
                        pos = i;
                        break;
                    }
                }

                person.personsRequestingMe.remove(pos);
                Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

                // Remove from other
                List<String> relations1 = new ArrayList<String>();
                relations1.add("personsImRequesting");
                Person person1 = Backendless.Data.of(Person.class).findById(personsRequestsList.get(position).getObjectId(), relations1);

                for (int i = 0; i < person1.personsImRequesting.size(); i++) {

                    if (person1.personsImRequesting.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                        pos = i;
                        break;
                    }
                }

                person1.personsImRequesting.remove(pos);
                Person updatedPersonOther = Backendless.Data.of(Person.class).save(person1);

                updatedPersonLoggedIn.addContact(updatedPersonOther);
                personLoggedIn = Backendless.Data.of(Person.class).save(updatedPersonLoggedIn);

                updatedPersonOther.addContact(updatedPersonLoggedIn);
                Backendless.Data.of(Person.class).save(updatedPersonLoggedIn);
                removedFullname = personsRequestsList.get(position).getFullname();
                personsRequestsList.remove(position);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvRequest.setAdapter(null);

            if (!personsRequestsList.isEmpty()) {

                rvRequest.setHasFixedSize(true);

                rvRequest.setLayoutManager(llm);

                rvRequest.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapterRequest = new ContactsAdapter(personsFoundQuery, r);

                rvRequest.setAdapter(adapterRequest);
                ringProgressDialog.dismiss();
            } else {
                ringProgressDialog.dismiss();
                searchView.setVisibility(View.GONE);
                rvRequest.setVisibility(View.GONE);
                progressBarRequesting.setVisibility(View.GONE);
                textViewTextNoRequestingUsers.setVisibility(View.VISIBLE);
            }
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

                //Remove from me

                List<String> relations = new ArrayList<String>();
                relations.add("personsRequestingMe");
                Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);
                System.out.println("Loaded object. Name - " + person.getFname() + ", relations - " + person.personsRequestingMe.size());

                int pos = 0;

                for (int i = 0; i < person.personsRequestingMe.size(); i++) {

                    if (person.personsRequestingMe.get(i).getObjectId().equals(personsRequestsList.get(positionInList).getObjectId())) {
                        pos = i;
                        break;
                    }
                }

                person.personsRequestingMe.remove(pos);
                Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);
                System.out.println("Received updated object. Name - " + updatedPersonLoggedIn.fname + ", relations - " + updatedPersonLoggedIn.personsRequestingMe.size());

                // Remove from other
                List<String> relations1 = new ArrayList<String>();
                relations1.add("personsImRequesting");
                Person person1 = Backendless.Data.of(Person.class).findById(personsRequestsList.get(positionInList).getObjectId(), relations1);
                System.out.println("Loaded object. Name - " + person1.getFname() + ", relations - " + person1.personsImRequesting.size());


                for (int i = 0; i < person1.personsImRequesting.size(); i++) {

                    if (person1.personsImRequesting.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                        pos = i;
                        break;
                    }
                }

                person1.personsImRequesting.remove(pos);
                Person updatedPersonOther = Backendless.Data.of(Person.class).save(person1);
                personsRequestsList.remove(positionInList);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvRequest.setAdapter(null);

            if (!personsRequestsList.isEmpty()) {

                rvRequest.setHasFixedSize(true);

                rvRequest.setLayoutManager(llm);

                rvRequest.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapterRequest = new ContactsAdapter(personsFoundQuery, r);

                rvRequest.setAdapter(adapterRequest);
                ringProgressDialog.dismiss();
            } else {
                ringProgressDialog.dismiss();
                searchView.setVisibility(View.GONE);
                rvRequest.setVisibility(View.GONE);
                progressBarRequesting.setVisibility(View.GONE);
                textViewTextNoRequestingUsers.setVisibility(View.VISIBLE);
            }
            Toast.makeText(v.getContext(), removedFullname + " rejected as contact", Toast.LENGTH_SHORT).show();
        }
    }
}
