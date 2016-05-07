package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class RecipientsForSlot extends Activity {

    TableRow tableRowRecyclerView;
    TableRow tableRowProgress;

    List<Person> addPersonsForSlot = new ArrayList<>(); // TODO Cant Probably get rid of, using ids instead
    Person personLoggedIn;
    List<Person> myContactsPersonsList;
    BackendlessUser loggedInUser = Backendless.UserService.CurrentUser();
    List<Slot> slot;
    BackendlessCollection<Person> myContactPersons;
    private ProgressBar progressBar;
    ArrayList<String> contactPersonsId = new ArrayList<>();
    Resources r;
    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients_for_slot);

        personLoggedIn = (Person) loggedInUser.getProperty("persons");
        r = getResources();
        doneButton = (Button) findViewById(R.id.buttonDoneSaveContactsForSlot);
        tableRowRecyclerView = (TableRow) findViewById(R.id.tableRowRecyclerView);
        tableRowProgress = (TableRow) findViewById(R.id.tableRowProgress);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intentGotContactsForSlot = new Intent(RecipientsForSlot.this, CreateSlot.class);

                if (!contactPersonsId.isEmpty()) {
                    intentGotContactsForSlot.putStringArrayListExtra("contactIds", contactPersonsId);
                }
                startActivity(intentGotContactsForSlot);

            }
        });

        new ParseURL().execute();
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

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[contacts]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            myContactPersons = Backendless.Data.of(Person.class).find(dataQuery);

            myContactsPersonsList = myContactPersons.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

            rv.setLayoutManager(llm);

            llm.setOrientation(LinearLayoutManager.VERTICAL);

            ContactsAdapter adapter = new ContactsAdapter(myContactsPersonsList, 0,getApplicationContext());

            rv.setAdapter(adapter);

            rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {

                    if (view != null) {

                        if (!(addPersonsForSlot.isEmpty() && contactPersonsId.isEmpty())) {
                            if ((addPersonsForSlot.contains(myContactsPersonsList.get(position)))) {
                                addPersonsForSlot.remove(position);

                                contactPersonsId.remove(position);
                                view.setBackgroundResource(0);
                                Toast.makeText(getApplicationContext(), myContactsPersonsList.get(position).getFname() + " " +
                                        myContactsPersonsList.get(position).getLname() + " removed from schedule", Toast.LENGTH_SHORT).show();

                            } else {
                                addPersonsForSlot.add(myContactsPersonsList.get(position));
                                contactPersonsId.add(myContactsPersonsList.get(position).getObjectId());
                                view.setBackgroundColor(Color.RED);
                                Toast.makeText(getApplicationContext(), myContactsPersonsList.get(position).getFname() + " " +
                                        myContactsPersonsList.get(position).getLname() + " added for schedule", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            addPersonsForSlot.add(myContactsPersonsList.get(position));
                            contactPersonsId.add(myContactsPersonsList.get(position).getObjectId());
                            view.setBackgroundColor(Color.RED);
                            Toast.makeText(getApplicationContext(), myContactsPersonsList.get(position).getFname() + " " +
                                    myContactsPersonsList.get(position).getLname() + " added for schedule", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show(); // TODO WHATS THIS???
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
            tableRowProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            tableRowRecyclerView.setVisibility(View.VISIBLE);
            rv.setVisibility(View.VISIBLE);
        }
    }
}