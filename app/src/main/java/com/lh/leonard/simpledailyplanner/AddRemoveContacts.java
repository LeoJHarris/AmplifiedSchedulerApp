package com.lh.leonard.simpledailyplanner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

public class AddRemoveContacts extends Activity {

    List<Person> persons;
    Person personLoggedIn;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    List<Person> myContactsList;
    BackendlessCollection<Person> myContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_contacts);

        Backendless.Data.mapTableToClass("Person", Person.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Contact", Contact.class);
        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Persistence.mapTableToClass("Contact", Contact.class);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");

        new ParseURL().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            Backendless.Data.mapTableToClass("Slot", Slot.class);
            Backendless.Data.mapTableToClass("Person", Person.class);

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[contacts]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            myContacts = Backendless.Data.of(Person.class).find(dataQuery);

            myContactsList = myContacts.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            // progressBar.setVisibility(View.INVISIBLE);

            RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            rv.setLayoutManager(llm);

            Resources r = getResources();

            ContactsAdapter adapter = new ContactsAdapter(myContactsList, r);

            rv.setAdapter(adapter);

            rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {


                    Intent slotDialogIntent = new Intent(AddRemoveContacts.this, MyCreatedSlotsDialog.class);

                    // slotDialogIntent.putExtra("slotId", slots.get(position).getObjectId()); // TODO send the slot to the dialog intent.

                    slotDialogIntent.putExtra("slotRef", position);

                    startActivity(slotDialogIntent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    // ...

                    //TODO: Dialog show, remove slot. Remove from list clear adapter, give adapter now list
                    //TODO Yes: get the ownerObjectId and remove from database


                }
            }
            ));
        }
    }
}
