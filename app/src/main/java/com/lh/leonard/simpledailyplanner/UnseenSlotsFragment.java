package com.lh.leonard.simpledailyplanner;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

public class UnseenSlotsFragment extends Fragment {

    //  ArrayList<Slot> listSlots = new ArrayList<>();


    Contact contact;
    Person person;

    List<Slot> slot;
    // private ProgressBar progressBar;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    View v;

    // private ProgressBar progressBar;


    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_unseen_slots, container, false);


        // Backendless.Persistence.mapTableToClass("Person", Person.class);
        person = (Person) userLoggedIn.getProperty("persons");

        // Backendless.Persistence.mapTableToClass("Slot", Slot.class);

        new ParseURL().execute();

        return v;
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

            // use a sample above where PhoneBook is created with multiple contacts,
// then use the savedPhoneBook instance:
            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Person[unseenSlots]");
            whereClause.append(".objectId='").append(person.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);

            slot = slots.getData();

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            // progressBar.setVisibility(View.INVISIBLE);

            RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);

            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
            rv.setLayoutManager(llm);

            Resources r = getResources();

            RVAdapter adapter = new RVAdapter(slot, r);

            rv.setAdapter(adapter);

            rv.addOnItemTouchListener(new RecyclerItemClickListener(v.getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {


                    Intent slotDialogIntent = new Intent(getActivity(), MyCreatedSlotsDialog.class);

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

