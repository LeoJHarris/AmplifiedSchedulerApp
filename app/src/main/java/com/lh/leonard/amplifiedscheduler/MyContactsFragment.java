package com.lh.leonard.amplifiedscheduler;

/**
 * Created by Leonard on 3/08/2015.
 */

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

public class MyContactsFragment extends Fragment {

    List<Person> persons;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    List<Person> myContactsList;
    BackendlessCollection<Person> myContacts;
    ContactsAdapter adapter;
    private ProgressBar progressBarMyContacts;
    SearchView searchView;
    View v;
    String fullnameRemovedPerson;
    Boolean removed;
    AutoResizeTextView textViewTextNoContacts;
    RecyclerView rvMyContacts;
    AlertDialog dialog;
    ProgressDialog ringProgressDialog;
    LinearLayoutManager llm;
    String removedFullName;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_contacts_tab1, container, false);

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Contact", Contact.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Contact", Contact.class);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        rvMyContacts = (RecyclerView) v.findViewById(R.id.rvMyContactsFragment);
        progressBarMyContacts = (ProgressBar) v.findViewById(R.id.progressBarMyContacts);
        textViewTextNoContacts = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoContacts);
        textViewTextNoContacts.setTypeface(regularFont);
        searchView = (SearchView) v.findViewById(R.id.searchViewMyContacts);
        searchView.setQueryHint("Search Contacts");

        new ParseURL().execute();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              public boolean onQueryTextChange(String text) {

                                                  if (adapter != null) {
                                                      if (TextUtils.isEmpty(text)) {
                                                          adapter.getFilter().filter("");
                                                      } else {
                                                          //  rvMyContacts.setVisibility(View.GONE);
                                                          //  progressBarMyContacts.setVisibility(View.VISIBLE);
                                                          adapter.getFilter().filter(text.toString());
                                                          //   progressBarMyContacts.setVisibility(View.GONE);
                                                          //   rvMyContacts.setVisibility(View.VISIBLE);
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
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Backendless.Persistence.mapTableToClass("Slot", Slot.class);
            Backendless.Persistence.mapTableToClass("Person", Person.class);

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[contacts]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());
            myContacts = Backendless.Persistence.of(Person.class).find(dataQuery);
            myContactsList = myContacts.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (isAdded()) {

                if (!myContactsList.isEmpty()) {

                    rvMyContacts.setHasFixedSize(true);
                    llm = new LinearLayoutManager(v.getContext());
                    rvMyContacts.setLayoutManager(llm);

                    Resources r = getResources();
                    adapter = new ContactsAdapter(myContactsList, r);
                    rvMyContacts.setAdapter(adapter);
                    rvMyContacts.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rvMyContacts, new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, final int position) {

                            dialog = new AlertDialog.Builder(v.getContext())
                                    .setTitle("Remove Contact?")
                                    .setMessage("Do you want to remove " + myContactsList.get(position).getFullname() + " as a contact")
                                    .setIcon(R.drawable.ic_questionmark)
                                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Removing " + myContactsList.get(position).getFullname() + " from your contacts ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new RemoveContact(position).execute();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
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
                    progressBarMyContacts.setVisibility(View.GONE);
                    rvMyContacts.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    progressBarMyContacts.setVisibility(View.GONE);
                    textViewTextNoContacts.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class RemoveContact extends AsyncTask<Void, Integer, Void> {

        int positionInList;

        public RemoveContact(int positionInList) {

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


            List<String> relations = new ArrayList<String>();
            relations.add("contacts");
            Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

            int pos = 0;

            for (int i = 0; i < person.contacts.size(); i++) {

                if (person.contacts.get(i).getObjectId().equals(myContactsList.get(positionInList).getObjectId())) {
                    pos = i;
                    break;
                }
            }

            person.contacts.remove(pos);
            Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

            // Remove from other
            List<String> relations1 = new ArrayList<String>();
            relations1.add("contacts");
            Person person1 = Backendless.Data.of(Person.class).findById(myContactsList.get(positionInList).getObjectId(), relations1);

            for (int i = 0; i < person1.contacts.size(); i++) {

                if (person1.contacts.get(i).getObjectId().equals(personLoggedIn.getObjectId())) {
                    pos = i;
                    break;
                }
            }
            removedFullName = person1.contacts.get(pos).getFullname();
            person1.contacts.remove(pos);
            Person updatedPersonOther = Backendless.Data.of(Person.class).save(person1);
            myContactsList.remove(positionInList);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvMyContacts.setAdapter(null);

            if (!myContactsList.isEmpty()) {

                rvMyContacts.setHasFixedSize(true);

                rvMyContacts.setLayoutManager(llm);

                rvMyContacts.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapter = new ContactsAdapter(myContactsList, r);

                rvMyContacts.setAdapter(adapter);
                ringProgressDialog.dismiss();
            } else {
                searchView.setVisibility(View.GONE);
                rvMyContacts.setVisibility(View.GONE);
                ringProgressDialog.dismiss();
                textViewTextNoContacts.setVisibility(View.VISIBLE);
            }
            Toast.makeText(v.getContext(), removedFullName + " removed as contact", Toast.LENGTH_SHORT).show();
        }
    }
}



