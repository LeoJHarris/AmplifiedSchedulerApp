package com.lh.leonard.simpledailyplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;

public class UserLoggedIn extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {


    //TODO ASYNC TASK FOR CONTACTS DIALOG WHEN THE ACTIVITY STARTS< MAKE IT A BUTTON CLICK EVENT FOR ADD CONTACTS


    BackendlessUser personLoggedIn = Backendless.UserService.CurrentUser();
    Person person;
    BackendlessCollection<Person> persons;

    private Button btn_listviewdialog = null;
    private EditText txt_item = null;
    //private String contactsNames[];
    private ArrayList<Person> array_sort;
    int textlength = 0;
    private AlertDialog myalertDialog = null;

    BackendlessCollection<Person> myContactPersons;
    ArrayList<Person> myContactsPersonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_logged_in);

        Backendless.Data.mapTableToClass("Contact", Contact.class);
        Backendless.Data.mapTableToClass("Person", Person.class);

        new ParseURL().execute();

        person = (Person) personLoggedIn.getProperty("persons");


//TODO WORKs!
//        QueryOptions queryOptions = new QueryOptions();
//        queryOptions.setRelated(Arrays.asList("contacts"));
//        BackendlessDataQuery query = new BackendlessDataQuery(queryOptions);
//        Backendless.Data.of(Person.class).find(query, new LoadingCallback<BackendlessCollection<Person>>(getApplication(), "", true) {
//
//            @Override
//            public void handleResponse(BackendlessCollection<Person> personBackendlessCollection) {
//                persons = personBackendlessCollection;
//              //  System.out.println("PPPP " + (persons.toString()));
//                List<Person> p= persons.getData();
//                System.out.println("PPPP " + (p.toString()));
//                System.out.println(p.get(0).getFname());
//
//            }
//        });


        final TextView welcomeLabel = (TextView) findViewById(R.id.textViewWelcomeLabel);

        welcomeLabel.setText("Welcome! " + person.getFname() + " " + person.getLname());

        final Button buttonAccountDetails = (Button) findViewById(R.id.buttonAccountDetails);
        final Button buttonSlotsAwaitingMyResponse = (Button) findViewById(R.id.buttonSlotsAwaitingMyResponse);
        final Button buttonSlotsImGoingTo = (Button) findViewById(R.id.buttonSlotsImGoingTo);
        final Button buttonMyCreatedSlots = (Button) findViewById(R.id.buttonMyCreatedSlots);
        final Button buttonLogout = (Button) findViewById(R.id.buttonSignOut);
        final Button buttonFindContacts = (Button) findViewById(R.id.buttonFindContacts);
        final Button buttonCreateSlot = (Button) findViewById(R.id.buttonCreateSlot);


        buttonAccountDetails.setOnClickListener(this);


        //TODO set all colors for button in values, so one change changes everything


        buttonFindContacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent updateAccountIntent = new Intent(UserLoggedIn.this, AddRemoveContactsTabbed.class);
                startActivity(updateAccountIntent);

            }
        });

        buttonCreateSlot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent updateAccountIntent = new Intent(UserLoggedIn.this, CreateSlot.class);
                startActivity(updateAccountIntent);

            }
        });


        // buttonAccountDetails.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //  Intent updateAccountIntent = new Intent(UserLoggedIn.this, UpdateAccount.class);
        //   startActivity(updateAccountIntent);

        //      }
        //   });

        buttonSlotsAwaitingMyResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent slotsAwaitingMyResponseIntent = new Intent(UserLoggedIn.this, SlotsAwaitingMyResponse.class);
                startActivity(slotsAwaitingMyResponseIntent);
            }
        });

        buttonSlotsImGoingTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent slotsImGoingToIntent = new Intent(UserLoggedIn.this, SlotsImGoingTo.class);
                startActivity(slotsImGoingToIntent);
            }
        });

        buttonMyCreatedSlots.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myCreatedSlotsIntent = new Intent(UserLoggedIn.this, MyCreatedSlots.class);
                startActivity(myCreatedSlotsIntent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    public void handleResponse(Void response) {
                        Intent logOutIntent = new Intent(UserLoggedIn.this, MainActivity.class);
                        // logOutIntent.putExtra("loggedOutPerson", person.getFname() + "," + person.getLname());
                        startActivity(logOutIntent);
                    }

                    public void handleFault(BackendlessFault fault) {
                        // something went wrong and logout failed, to get the error code call fault.getCode()
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View arg0) {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(UserLoggedIn.this);

        final EditText editText = new EditText(UserLoggedIn.this);
        final ListView listview = new ListView(UserLoggedIn.this);
        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable., 0, 0, 0);
        array_sort = (ArrayList<Person>) myContactsPersonsList.clone();
        LinearLayout layout = new LinearLayout(UserLoggedIn.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        CustomAlertAdapter arrayAdapter = new CustomAlertAdapter(UserLoggedIn.this, array_sort);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                textlength = editText.getText().length();
                array_sort.clear();
                for (int i = 0; i < myContactsPersonsList.size(); i++) {
                    if (textlength <= myContactsPersonsList.get(i).getFullname().length()) {

                        if (myContactsPersonsList.get(i).getFullname().toLowerCase().contains(editText.getText().toString().toLowerCase().trim())) {
                            array_sort.add(myContactsPersonsList.get(i));
                        }
                    }
                }
                listview.setAdapter(new CustomAlertAdapter(UserLoggedIn.this, array_sort));
            }
        });
        myDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myalertDialog = myDialog.show();

    }


    //Onclick for contacts
    @Override
    public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {

        myalertDialog.dismiss();
        String strName = myContactsPersonsList.get(position).getFullname();

        Toast.makeText(getApplicationContext(), strName, Toast.LENGTH_LONG).show();


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

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[contacts]");
            whereClause.append(".objectId='").append(person.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            myContactPersons = Backendless.Data.of(Person.class).find(dataQuery);

            myContactsPersonsList = (ArrayList) myContactPersons.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}