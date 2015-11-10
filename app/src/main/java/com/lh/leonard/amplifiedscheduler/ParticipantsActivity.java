package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

public class ParticipantsActivity extends Activity {

    Contact contact;
    Person person;
    List<Person> slot;
    private ProgressBar progressBar;
    BackendlessCollection<Slot> persons;
    BackendlessCollection<Person> slots;
    SearchView searchViewSlots;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    ContactsAdapter adapter;
    AutoResizeTextView textViewTextNoSlotAvaliable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slots_display);

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

       // final Typeface regularFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/GoodDog.otf");

        textViewTextNoSlotAvaliable = (AutoResizeTextView) findViewById(R.id.textViewTextNoSlotAvaliable);

        this.setTitle("People going"); // TODO SHOULD ADD THE SUBJECT IN TITLE

        SearchView searchView = (SearchView) findViewById(R.id.searchViewSlots);

        searchView.setQueryHint("Search people going");
       // textViewTextNoSlotAvaliable.setTypeface(regularFont);

        person = (Person) userLoggedIn.getProperty("persons");

        new ParseURL().execute();

        searchViewSlots = (SearchView) findViewById(R.id.searchViewSlots);

        searchViewSlots.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                   public boolean onQueryTextChange(String text) {

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
    }

    private class ParseURL extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Bundle data = getIntent().getExtras();
            String id = data.getString("eventid");


            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Slot[attendees]");
            whereClause.append(".objectId='").append(id).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Person.class).find(dataQuery);
            slot = slots.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // progressBar.setVisibility(View.INVISIBLE);


            if (!slot.isEmpty()) {

                RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

                rv.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(llm);

                Resources r = getResources();

                adapter = new ContactsAdapter(slot, r);

                rv.setAdapter(adapter);

                rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {


                        //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                        //TODO Yes: get the ownerObjectId and remove from database
                    }
                }
                ));
                progressBar.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.GONE);
                textViewTextNoSlotAvaliable.setText("No one is currently going to this schedule");
                textViewTextNoSlotAvaliable.setVisibility(View.VISIBLE);
            }
        }
    }
}