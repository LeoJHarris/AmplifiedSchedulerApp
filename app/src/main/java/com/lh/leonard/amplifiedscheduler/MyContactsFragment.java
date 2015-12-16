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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContactsFragment extends Fragment {

    private ProgressBar progressBarRequesting;
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
    LinearLayoutManager llm;
    String removedFullName;
    ProgressDialog ringProgressDialog;
    Resources r = getResources();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_contacts_tab1, container, false);

        Backendless.Data.mapTableToClass("Person", Person.class);

        r = getResources();

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        rvMyContacts = (RecyclerView) v.findViewById(R.id.rvMyContactsFragment);
        progressBarMyContacts = (ProgressBar) v.findViewById(R.id.progressBarMyContacts);
        textViewTextNoContacts = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoContacts);
        textViewTextNoContacts.setTypeface(RobotoCondensedLightItalic);
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
            searchView.setVisibility(View.GONE);
            rvMyContacts.setVisibility(View.GONE);
            textViewTextNoContacts.setVisibility(View.GONE);
            progressBarMyContacts.setVisibility(View.VISIBLE);
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

                    adapter = new ContactsAdapter(myContactsList, 0, r);
                    rvMyContacts.setAdapter(adapter);
                    rvMyContacts.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rvMyContacts, new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, final int position) {

                            dialog = new AlertDialog.Builder(v.getContext())
                                    .setTitle("Remove Contact?")
                                    .setMessage("Do you want to remove " + myContactsList.get(position).getFullname() + " as a contact")
                                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Removing " + myContactsList.get(position).getFullname() + " from your contacts ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new RemoveContact(position).execute();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

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
                    progressBarMyContacts.setVisibility(View.GONE);
                    rvMyContacts.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    dialog = null;
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

            Map<String, String> args = new HashMap<>();

            removedFullName = myContactsList.get(positionInList).getFullname();

            args.put("id", "removeContact");

            args.put("loggedinperson", personLoggedIn.getObjectId());

            args.put("otherperson", myContactsList.get(positionInList).getObjectId());

            myContactsList.remove(positionInList);

            Backendless.Events.dispatch("ManageContact", args);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rvMyContacts.setAdapter(null);

            if (!myContactsList.isEmpty()) {

                rvMyContacts.setHasFixedSize(true);

                rvMyContacts.setLayoutManager(llm);

                //  rvMyContacts.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));


                adapter = new ContactsAdapter(myContactsList, 0, r);

                rvMyContacts.setAdapter(adapter);

            } else {
                searchView.setVisibility(View.GONE);
                rvMyContacts.setVisibility(View.GONE);
                textViewTextNoContacts.setVisibility(View.VISIBLE);
            }
            ringProgressDialog.dismiss();
            Toast.makeText(getContext(), removedFullName + " was removed from contacts", Toast.LENGTH_SHORT).show();
        }
    }

    private class Test extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchView.setVisibility(View.GONE);
            rvMyContacts.setVisibility(View.GONE);
            textViewTextNoContacts.setVisibility(View.GONE);
            progressBarMyContacts.setVisibility(View.VISIBLE);
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

                    adapter = new ContactsAdapter(myContactsList, 0, r);
                    rvMyContacts.setAdapter(adapter);

                    progressBarMyContacts.setVisibility(View.GONE);
                    rvMyContacts.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    dialog = null;
                } else {
                    progressBarMyContacts.setVisibility(View.GONE);
                    textViewTextNoContacts.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onResume() {

        new Test().execute();
        super.onResume();
    }
}


