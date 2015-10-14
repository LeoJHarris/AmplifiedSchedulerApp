package com.lh.leonard.eventhub101;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import android.widget.TableRow;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class FindContactsFragment extends Fragment {

    List<Person> person;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    String nameQuerySearch;
    List<Person> personsFoundQuery;
    private ProgressBar progressBarFindContacts;
    TableRow tableRowProgressFindContacts;
    TableRow tableRowRecyclerViewFindContacts;
    RecyclerView rv;
    LinearLayoutManager llm;
    View v;
    SearchView searchViewFindContacts;
    int val;
    ContactsAdapter adapter;
    Boolean alreadyRequesting = false;
    Boolean alreadyContact = false;
    int statusOnPerson = 0;
    String dialogMessage;
    ProgressDialog ringProgressDialog;
    String postMessage;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.find_contacts_tab2, container, false);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);

        searchViewFindContacts = (SearchView) v.findViewById(R.id.searchViewFindContacts);
        tableRowProgressFindContacts = (TableRow) v.findViewById(R.id.tableRowProgressFindContacts);
        tableRowRecyclerViewFindContacts = (TableRow) v.findViewById(R.id.tableRowRecyclerViewFindContacts);
        progressBarFindContacts = (ProgressBar) v.findViewById(R.id.progressBarFindContacts);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        llm = new LinearLayoutManager(v.getContext());

        searchViewFindContacts.setQueryHint("Search Users");


        searchViewFindContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                          public boolean onQueryTextChange(String text) {

                                                              if (!text.equals("")) {

                                                                  nameQuerySearch = text;
                                                                  new ParseURL().execute();
                                                              }
                                                              if (adapter != null) {
                                                                  if (TextUtils.isEmpty(text)) {
                                                                      adapter.getFilter().filter("");
                                                                  } else {
                                                                      adapter.getFilter().filter(text.toString());
                                                                  }
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
            progressBarFindContacts.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Backendless.Data.mapTableToClass("Person", Person.class);
            String whereClause = "lname LIKE '" + nameQuerySearch + "%' OR fname LIKE '" + nameQuerySearch + "%'";
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);
            List<String> relations1 = new ArrayList<String>();

            QueryOptions q = new QueryOptions();
            q.addRelated("personsImRequesting");
            q.addRelated("personsRequestingMe");
            q.addRelated("contacts");
            dataQuery.setQueryOptions(q);
            BackendlessCollection<Person> result = Backendless.Persistence.of(Person.class).find(dataQuery);

            personsFoundQuery = result.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (isAdded()) {

                rv.setHasFixedSize(true);

                rv.setLayoutManager(llm);

                //   rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapter = new ContactsAdapter(personsFoundQuery, r);

                rv.setAdapter(adapter);

                rv.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, final int position) {

                        String title = "Send Contact Request?";
                        String message = "Do you want to send contact request to ";
                        String messageToAppend = "";
                        dialogMessage = "Sending contact request to " + personsFoundQuery.get(position).getFullname() + " ...";
                        postMessage = "Contact request sent to " + personsFoundQuery.get(position).getFullname();

                        int SIZE;
                        // personsRequestingMe size is smaller
                        if (personsFoundQuery.get(position).personsRequestingMe.size() < personsFoundQuery.get(position).personsImRequesting.size()) {
                            if (personsFoundQuery.get(position).personsImRequesting.size() < personsFoundQuery.get(position).contacts.size()) {
                                SIZE = personsFoundQuery.get(position).contacts.size();

                            } else {
                                SIZE = personsFoundQuery.get(position).personsImRequesting.size();
                            }
                        } else if (personsFoundQuery.get(position).personsRequestingMe.size() < personsFoundQuery.get(position).contacts.size()) {
                            SIZE = personsFoundQuery.get(position).contacts.size();

                        } else {
                            SIZE = personsFoundQuery.get(position).personsRequestingMe.size();
                        }
                        for (int p = 0; 0 < SIZE; p++) {
                            // 1 -- don't want to request the other person? or do
                            if (p < personsFoundQuery.get(position).personsRequestingMe.size()) {
                                if (personsFoundQuery.get(position).personsRequestingMe.get(p).objectId.equals(personLoggedIn.getObjectId())) // check
                                {
                                    statusOnPerson = 1;
                                    title = "Remove Requesting Contact?";
                                    message = "Do you want to cancel your contact request to ";
                                    messageToAppend = "";
                                    dialogMessage = "Cancelling contact request to " + personsFoundQuery.get(position).getFullname() + " ...";
                                    postMessage = "Cancelled contact request to " + personsFoundQuery.get(position).getFullname();
                                    break;
                                }
                            }
                            // 1 -- Accept his contact request
                            if (p < personsFoundQuery.get(position).personsImRequesting.size()) {

                                if (personsFoundQuery.get(position).personsImRequesting.get(p).objectId.equals(personLoggedIn.getObjectId())) {
                                    statusOnPerson = 2;
                                    title = "Accept Request?";
                                    message = "Do you want to accept ";
                                    messageToAppend = " as a contact?";
                                    dialogMessage = "Adding " + personsFoundQuery.get(position).getFullname() + " to your contact ...";
                                    postMessage = personsFoundQuery.get(position).getFullname() + " has been added to your contacts";
                                    break;
                                }
                            }
                            if (p < personsFoundQuery.get(position).contacts.size()) {
                                // already contacts remove?
                                if (personsFoundQuery.get(position).contacts.get(p).objectId.equals(personLoggedIn.getObjectId())) {
                                    statusOnPerson = 3;
                                    title = "Remove Contact?";
                                    message = "Do you want to remove ";
                                    messageToAppend = " as a contact?";
                                    postMessage = personsFoundQuery.get(position).getFullname() + " has been removed from your contacts";
                                    break;
                                }
                            }
                        }
                        if (statusOnPerson != 2) {


                            if (alertDialog != null) {

                                if (!alertDialog.isShowing()) {
                                    alertDialog = new AlertDialog.Builder(v.getContext())
                                            .setTitle(title)
                                            .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
                                            .setIcon(R.drawable.ic_questionmark)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {


                                                    dialog.dismiss();
                                                    ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                    ringProgressDialog.setCancelable(false);
                                                    new AddContact(position).execute();


                                                    //TODO should set personLoggedInWithRequesting person
                                                    //TODO for this activity, although may need to reload each tme

                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }
                            else{
                                alertDialog = new AlertDialog.Builder(v.getContext())
                                        .setTitle(title)
                                        .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
                                        .setIcon(R.drawable.ic_questionmark)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {


                                                dialog.dismiss();
                                                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                ringProgressDialog.setCancelable(false);
                                                new AddContact(position).execute();


                                                //TODO should set personLoggedInWithRequesting person
                                                //TODO for this activity, although may need to reload each tme

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        }).show();

                            }
                        } else {

                            if (alertDialog != null) {

                                if (!alertDialog.isShowing()) {

                                    alertDialog = new AlertDialog.Builder(v.getContext())
                                            .setTitle(title)
                                            .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
                                            .setIcon(R.drawable.ic_questionmark)
                                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    statusOnPerson = 2;
                                                    dialog.dismiss();
                                                    ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                    ringProgressDialog.setCancelable(false);
                                                    new AddContact(position).execute();

                                                    //TODO should set personLoggedInWithRequesting person
                                                    //TODO for this activity, although may need to reload each tme

                                                }
                                            })
                                            .setNegativeButton("Reject", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    statusOnPerson = 4;
                                                    dialog.dismiss();
                                                    ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                    ringProgressDialog.setCancelable(false);
                                                    new AddContact(position).execute();

                                                }
                                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.dismiss();

                                                }
                                            }).show();

                                }
                            } else {
                                alertDialog = new AlertDialog.Builder(v.getContext())
                                        .setTitle(title)
                                        .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
                                        .setIcon(R.drawable.ic_questionmark)
                                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                statusOnPerson = 2;
                                                dialog.dismiss();
                                                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                ringProgressDialog.setCancelable(false);
                                                new AddContact(position).execute();

                                                //TODO should set personLoggedInWithRequesting person
                                                //TODO for this activity, although may need to reload each tme

                                            }
                                        })
                                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                statusOnPerson = 4;
                                                dialog.dismiss();
                                                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMessage, true);
                                                ringProgressDialog.setCancelable(false);
                                                new AddContact(position).execute();

                                            }
                                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();

                                            }
                                        }).show();
                            }
                        }
                    }


                    @Override
                    public void onItemLongClick(View view, int position) {
                        // ...

                        //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                        //TODO Yes: get the ownerObjectId and remove from database
                    }
                }

                ));

                tableRowProgressFindContacts.setVisibility(View.GONE);
                progressBarFindContacts.setVisibility(View.GONE);
                tableRowRecyclerViewFindContacts.setVisibility(View.VISIBLE);
                rv.setVisibility(View.VISIBLE);
            }
        }
    }


    private class AddContact extends AsyncTask<Void, Integer, Void> {

        int position;

        public AddContact(int position) {
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
            if (isAdded()) {

                // Just add for requesting
                if (statusOnPerson == 0) {


                    Person p = Backendless.Data.of(Person.class).findById(personLoggedIn);

                    Person p2 = Backendless.Data.of(Person.class).findById(personsFoundQuery.get(position));

                    //   p.setObjectId(personLoggedIn.objectId);
                    // p2.setObjectId(personsFoundQuery.get(position).getObjectId());

                    p.addPersonImRequesting(p2);
                    p2.addPersonRequestingMe(p);

                    personLoggedIn = Backendless.Data.of(Person.class).save(p);
                    Backendless.Data.of(Person.class).save(p2);

                }
                // remove other im requesting
                else if (statusOnPerson == 1) {

                    List<String> relations = new ArrayList<String>();
                    relations.add("personsImRequesting");
                    Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

                    int pos = 0;

                    for (int i = 0; i < person.personsImRequesting.size(); i++) {

                        if (person.personsImRequesting.get(i).getObjectId().equals(personsFoundQuery.get(position).getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person.personsImRequesting.remove(pos);
                    Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

                    // Remove from other
                    List<String> relations1 = new ArrayList<String>();
                    relations1.add("personsRequestingMe");
                    Person person1 = Backendless.Data.of(Person.class).findById(personsFoundQuery.get(position).getObjectId(), relations1);

                    for (int i = 0; i < person1.personsRequestingMe.size(); i++) {

                        if (person1.personsRequestingMe.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person1.personsRequestingMe.remove(pos);
                    Person updatedPersonOther = Backendless.Data.of(Person.class).save(person1);

                }
                // Accept his contact request
                else if (statusOnPerson == 2) {
                    List<String> relations = new ArrayList<String>();
                    relations.add("personsRequestingMe");
                    Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

                    int pos = 0;

                    for (int i = 0; i < person.personsRequestingMe.size(); i++) {

                        if (person.personsRequestingMe.get(i).getObjectId().equals(personsFoundQuery.get(position).getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person.personsRequestingMe.remove(pos);
                    Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

                    // Remove from other
                    List<String> relations1 = new ArrayList<String>();
                    relations1.add("personsImRequesting");
                    Person person1 = Backendless.Data.of(Person.class).findById(personsFoundQuery.get(position).getObjectId(), relations1);

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

                }
                // already contacts remove?
                else if (statusOnPerson == 3) {
                    List<String> relations = new ArrayList<String>();
                    relations.add("contacts");
                    Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

                    int pos = 0;

                    for (int i = 0; i < person.contacts.size(); i++) {

                        if (person.contacts.get(i).getObjectId().equals(personsFoundQuery.get(position).getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person.contacts.remove(pos);
                    Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

                    // Remove from other
                    List<String> relations1 = new ArrayList<String>();
                    relations1.add("contacts");
                    Person person1 = Backendless.Data.of(Person.class).findById(personsFoundQuery.get(position).getObjectId(), relations1);

                    for (int i = 0; i < person1.contacts.size(); i++) {

                        if (person1.contacts.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person1.contacts.remove(pos);
                    Person updatedPersonOther = Backendless.Data.of(Person.class).save(person1);

                } else if (statusOnPerson == 4) {

                    List<String> relations = new ArrayList<String>();
                    relations.add("personsRequestingMe");
                    Person person4 = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

                    int pos = 0;

                    for (int i = 0; i < person4.personsRequestingMe.size(); i++) {

                        if (person4.personsRequestingMe.get(i).getObjectId().equals(personsFoundQuery.get(position).getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person4.personsRequestingMe.remove(pos);
                    Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person4);

                    // Remove from other
                    List<String> relations1 = new ArrayList<String>();
                    relations1.add("personsImRequesting");
                    Person person3 = Backendless.Data.of(Person.class).findById(personsFoundQuery.get(position).getObjectId(), relations1);

                    for (int i = 0; i < person3.personsImRequesting.size(); i++) {

                        if (person3.personsImRequesting.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                            pos = i;
                            break;
                        }
                    }

                    person3.personsImRequesting.remove(pos);
                    Person updatedPersonOther = Backendless.Data.of(Person.class).save(person3);

                }


                Backendless.Data.mapTableToClass("Person", Person.class);
                String whereClause = "lname LIKE '" + nameQuerySearch + "%' OR fname LIKE '" + nameQuerySearch + "%'";
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                dataQuery.setWhereClause(whereClause);
                List<String> relations1 = new ArrayList<String>();

                QueryOptions q = new QueryOptions();
                q.addRelated("personsImRequesting");
                q.addRelated("personsRequestingMe");
                q.addRelated("contacts");
                dataQuery.setQueryOptions(q);
                BackendlessCollection<Person> result = Backendless.Persistence.of(Person.class).find(dataQuery);

                personsFoundQuery = result.getData();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rv.setAdapter(null);

            rv.setHasFixedSize(true);

            rv.setLayoutManager(llm);

            rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

            Resources r = getResources();

            adapter = new ContactsAdapter(personsFoundQuery, r);

            rv.setAdapter(adapter);

            ringProgressDialog.dismiss();

            Toast.makeText(v.getContext(), postMessage, Toast.LENGTH_SHORT).show();
        }
    }
}