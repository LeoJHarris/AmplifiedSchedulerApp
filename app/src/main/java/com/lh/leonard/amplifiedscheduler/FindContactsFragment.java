package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hp1 on 21-01-2015.
 */
public class FindContactsFragment extends Fragment {

    Timer timer;
    RelativeLayout RLProgressBar;
    List<Person> person;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    String nameQuerySearch;
    List<Person> personsFoundQuery;
    private ProgressBar progressBarFindContacts;
    RecyclerView rv;
    LinearLayoutManager llm;
    View v;
    SearchView searchViewFindContacts;
    ContactsAdapter adapter;
    String dialogMessage;
    ProgressDialog ringProgressDialog;
    String postMessage;
    AlertDialog alertDialog;
    Boolean refreshed = false;
    AutoResizeTextView editHintSearchContacts;
    HashMap<Integer, Integer> hashMapSTATUS = new HashMap<>();
    Drawable drawableRequesting;
    Drawable drawableContacts;
    Drawable drawableActionRequired;
    Resources r;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.find_contacts_tab2, container, false);

        r = getResources();

        drawableRequesting = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_friend_requested);
        drawableActionRequired = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_actionrequiredcontactspng);
        drawableContacts = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_currentcontact);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        final Typeface RobotoBlack = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Black.ttf");
        final Typeface RobotoCondensedLightItalic = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        final Typeface RobotoCondensedLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        final Typeface RobotoCondensedBold = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);

        editHintSearchContacts = (AutoResizeTextView) v.findViewById(R.id.editHintSearchContacts);
        searchViewFindContacts = (SearchView) v.findViewById(R.id.searchViewFindContacts);
        progressBarFindContacts = (ProgressBar) v.findViewById(R.id.progressBarFindContacts);
        RLProgressBar = (RelativeLayout) v.findViewById(R.id.RLProgressBar);

        editHintSearchContacts.setTypeface(RobotoCondensedLightItalic);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        llm = new LinearLayoutManager(v.getContext());

        searchViewFindContacts.setQueryHint("Search Users");

        searchViewFindContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                          public boolean onQueryTextChange(final String text) {

                                                              refreshed = false;

                                                              if (!text.equals("")) {

                                                                  editHintSearchContacts.setVisibility(View.GONE);
                                                                  rv.setVisibility(View.GONE);
                                                                  progressBarFindContacts.setVisibility(View.VISIBLE);
                                                                  RLProgressBar.setVisibility(View.VISIBLE);

                                                                  nameQuerySearch = text;
                                                                  if (timer != null) {
                                                                      timer.cancel();
                                                                  }
                                                                  timer = new Timer();
                                                                  callAsynchronousTask();
                                                              } else {
                                                                  editHintSearchContacts.setText("Search users by name or email address");
                                                                  editHintSearchContacts.setVisibility(View.VISIBLE);
                                                                  progressBarFindContacts.setVisibility(View.GONE);
                                                                  RLProgressBar.setVisibility(View.GONE);
                                                                  rv.setAdapter(null);
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

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new ParseURL().execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 3000); //execute in every 50000 ms
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

            String whereClause = (isValidEmail(nameQuerySearch)) ?
                    "email = '" + nameQuerySearch + "' AND '" + personLoggedIn.getEmail() + "' != email" :
                    "(lname LIKE '" + nameQuerySearch + "%' OR fname LIKE '" +
                            nameQuerySearch + "%') AND " + "objectId NOT LIKE '" + personLoggedIn.getObjectId() + "'";
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);

            QueryOptions q = new QueryOptions();
            q.addRelated("personsImRequesting");
            q.addRelated("personsRequestingMe");
            q.addRelated("contacts");
            dataQuery.setQueryOptions(q);
            BackendlessCollection<Person> result = Backendless.Persistence.of(Person.class).find(dataQuery);

            personsFoundQuery = result.getData();

            if (refreshed) {
                personsFoundQuery.clear();
            }

            hashMapSTATUS.clear();

            int SIZE;
            for (int j = 0; j < personsFoundQuery.size(); j++) {
                // personsRequestingMe size is smaller
                if (personsFoundQuery.get(j).personsRequestingMe.size() < personsFoundQuery.get(j).personsImRequesting.size()) {
                    if (personsFoundQuery.get(j).personsImRequesting.size() < personsFoundQuery.get(j).contacts.size()) {
                        SIZE = personsFoundQuery.get(j).contacts.size();

                    } else {
                        SIZE = personsFoundQuery.get(j).personsImRequesting.size();
                    }
                } else if (personsFoundQuery.get(j).personsRequestingMe.size() < personsFoundQuery.get(j).contacts.size()) {
                    SIZE = personsFoundQuery.get(j).contacts.size();

                } else {
                    SIZE = personsFoundQuery.get(j).personsRequestingMe.size();
                }
                if (SIZE <= 0) {
                    hashMapSTATUS.put(j, 0);
                } else {
                    for (int p = 0; p < SIZE; p++) {

                        if (p < personsFoundQuery.get(j).personsRequestingMe.size()) {
                            if (personsFoundQuery.get(j).personsRequestingMe.get(p).objectId.equals(personLoggedIn.getObjectId())) // check
                            {
                                hashMapSTATUS.put(j, 1);
                                break;
                            }
                        }
                        // 1 -- Accept his contact request
                        if (p < personsFoundQuery.get(j).personsImRequesting.size()) {

                            if (personsFoundQuery.get(j).personsImRequesting.get(p).objectId.equals(personLoggedIn.getObjectId())) {
                                hashMapSTATUS.put(j, 2);
                                break;
                            }
                        }
                        if (p < personsFoundQuery.get(j).contacts.size()) {
                            // already contacts remove?
                            if (personsFoundQuery.get(j).contacts.get(p).objectId.equals(personLoggedIn.getObjectId())) {
                                hashMapSTATUS.put(j, 3);
                                break;
                            }
                        }
                        hashMapSTATUS.put(j, 0);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (isAdded()) {

                if (!personsFoundQuery.isEmpty() && !refreshed) {
                    rv.setHasFixedSize(true);

                    rv.setLayoutManager(llm);

                    //   rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                    adapter = new ContactsAdapter(personsFoundQuery, hashMapSTATUS);

                    rv.setAdapter(adapter);

                    rv.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, final int position) {

                            if (hashMapSTATUS != null) {

                                String title = "Send Contact Request";
                                String message = "Do you want to send contact request to ";
                                String messageToAppend = "";
                                dialogMessage = "Sending contact request to " + personsFoundQuery.get(position).getFullname() + " ...";
                                postMessage = "Contact request sent to " + personsFoundQuery.get(position).getFullname();

                                if (hashMapSTATUS.get(position) == 1) {
                                    title = "Remove Requesting Contact";
                                    message = "Do you want to cancel your contact request to ";
                                    messageToAppend = "";
                                    dialogMessage = "Cancelling contact request to " + personsFoundQuery.get(position).getFullname() + " ...";
                                    postMessage = "Cancelled contact request to " + personsFoundQuery.get(position).getFullname();
                                } else if (hashMapSTATUS.get(position) == 2) {
                                    title = "Accept Request";
                                    message = "Do you want to accept ";
                                    messageToAppend = " as a contact";
                                    dialogMessage = "Adding " + personsFoundQuery.get(position).getFullname() + " to your contact ...";
                                    postMessage = personsFoundQuery.get(position).getFullname() + " has been added to your contacts";
                                } else if (hashMapSTATUS.get(position) == 3) {
                                    title = "Remove Contact";
                                    message = "Remove ";
                                    messageToAppend = " as a contact";
                                    dialogMessage = "Removing " + personsFoundQuery.get(position).getFullname() + " as a contact ...";
                                    postMessage = personsFoundQuery.get(position).getFullname() + " has been removed from your contacts";
                                }

                                if (hashMapSTATUS.get(position) != 2) {

                                    if (alertDialog != null) {

                                        if (!alertDialog.isShowing()) {
                                            alertDialog = new AlertDialog.Builder(v.getContext())
                                                    .setTitle(title)
                                                    .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
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
                                        alertDialog = new AlertDialog.Builder(v.getContext())
                                                .setTitle(title)
                                                .setMessage(message + personsFoundQuery.get(position).getFullname() + messageToAppend)
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
                                                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int whichButton) {

                                                            hashMapSTATUS.put(position, 2);
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

                                                            hashMapSTATUS.put(position, 2);
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
                                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {

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

                                                        hashMapSTATUS.put(position, 4);
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
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            // ...

                            //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                            //TODO Yes: get the ownerObjectId and remove from database
                        }
                    }
                    ));

                    if (adapter != null) {
                        if (TextUtils.isEmpty(nameQuerySearch)) {
                            adapter.getFilter().filter("");
                        } else {
                            adapter.getFilter().filter(nameQuerySearch.toString());
                        }
                    }

                    progressBarFindContacts.setVisibility(View.GONE);
                    RLProgressBar.setVisibility(View.GONE);
                    editHintSearchContacts.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);

                } else {
                    progressBarFindContacts.setVisibility(View.GONE);
                    RLProgressBar.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);

                    if (refreshed) {
                        editHintSearchContacts.setText("Search users by name or email address");

                    } else {
                        editHintSearchContacts.setText("No users found");
                    }
                    editHintSearchContacts.setVisibility(View.VISIBLE);
                }
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

                // Send Contact invite
                if (hashMapSTATUS.get(position) == 0) {

                    Map<String, String> args = new HashMap<>();

                    args.put("id", "sendContactInvite");

                    args.put("loggedinperson", personLoggedIn.getObjectId());

                    args.put("otherperson", personsFoundQuery.get(position).getObjectId());

                    Backendless.Events.dispatch("ManageContact", args);

                    hashMapSTATUS.put(position, 1);
                }
                // Cancel contact request
                else if (hashMapSTATUS.get(position) == 1) {

                    Map<String, String> args = new HashMap<>();

                    args.put("id", "cancelContactInvite");

                    args.put("loggedinperson", personLoggedIn.getObjectId());

                    args.put("otherperson", personsFoundQuery.get(position).getObjectId());

                    Backendless.Events.dispatch("ManageContact", args);
                    hashMapSTATUS.put(position, 0);
                }
                // Accept his contact request
                else if (hashMapSTATUS.get(position) == 2) {
                    //Accept contact Request
                    Map<String, String> args = new HashMap<>();

                    args.put("id", "acceptContactRequest");

                    args.put("loggedinperson", personLoggedIn.getObjectId());

                    args.put("otherperson", personsFoundQuery.get(position).getObjectId());

                    Backendless.Events.dispatch("ManageContact", args);
                    hashMapSTATUS.put(position, 3);
                }
                // Remove contact
                else if (hashMapSTATUS.get(position) == 3) {
                    Map<String, String> args = new HashMap<>();

                    args.put("id", "removeContact");

                    args.put("loggedinperson", personLoggedIn.getObjectId());

                    args.put("otherperson", personsFoundQuery.get(position).getObjectId());

                    Backendless.Events.dispatch("ManageContact", args);

                    hashMapSTATUS.put(position, 0);

                } else if (hashMapSTATUS.get(position) == 4) {

                    //Decline contact Request
                    Map<String, String> args = new HashMap<>();

                    args.put("id", "declineRequest");

                    args.put("loggedinperson", personLoggedIn.getObjectId());

                    args.put("otherperson", personsFoundQuery.get(position).getObjectId());

                    Backendless.Events.dispatch("ManageContact", args);
                    hashMapSTATUS.put(position, 0);
                }

                Backendless.Data.mapTableToClass("Person", Person.class);


                String whereClause = (isValidEmail(nameQuerySearch)) ?
                        "email = '" + nameQuerySearch + "' AND '" + personLoggedIn.getEmail() + "' != email" :
                        "(lname LIKE '" + nameQuerySearch + "%' OR fname LIKE '" +
                                nameQuerySearch + "%') AND " + "objectId NOT LIKE '" + personLoggedIn.getObjectId() + "'";
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                dataQuery.setWhereClause(whereClause);

                QueryOptions q = new QueryOptions();
                q.addRelated("personsImRequesting");
                q.addRelated("personsRequestingMe");
                q.addRelated("contacts");
                dataQuery.setQueryOptions(q);
                BackendlessCollection<Person> result = Backendless.Data.of(Person.class).find(dataQuery);

                personsFoundQuery = result.getData();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rv.setAdapter(null);

            rv.setHasFixedSize(true);

            rv.setLayoutManager(llm);

            adapter = new ContactsAdapter(personsFoundQuery, hashMapSTATUS);

            rv.setAdapter(adapter);

            ringProgressDialog.dismiss();
            Toast.makeText(v.getContext(), postMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {

        hashMapSTATUS.clear();
        searchViewFindContacts.setQuery("", false);
        rv.setAdapter(null);
        progressBarFindContacts.setVisibility(View.GONE);
        editHintSearchContacts.setText("Search users by name or email address");
        editHintSearchContacts.setVisibility(View.VISIBLE);
        timer = null;
        refreshed = true;
        nameQuerySearch = "";
        if (personsFoundQuery != null) {
            personsFoundQuery.clear();
        }
        super.onResume();
    }

    private boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}