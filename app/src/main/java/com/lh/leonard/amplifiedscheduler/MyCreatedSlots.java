package com.lh.leonard.amplifiedscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class MyCreatedSlots extends Fragment {

    Contact contact;
    Person personLoggedIn;
    List<Slot> slot;
    List<Person> personsToSms;
    BackendlessCollection<Person> personsToSmsCollection;
    private ProgressBar progressBar;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    SearchView searchViewSlots;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    RVAdapter adapter;
    AutoResizeTextView textViewTextNoSlotAvaliable;
    View v;
    ProgressDialog ringProgressDialog;
    AlertDialog dialog;
    RecyclerView rv;
    LinearLayoutManager llm;
    String eventRemoved;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.slots_display, container, false);

        getActivity().setTitle("My Schedules");

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Slot", Slot.class);
        Backendless.Data.mapTableToClass("Person", Person.class);
        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");
        textViewTextNoSlotAvaliable = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoSlotAvaliable);
        textViewTextNoSlotAvaliable.setTypeface(regularFont);
        personLoggedIn = (Person) userLoggedIn.getProperty("persons");
        new ParseURL().execute();
        searchViewSlots = (SearchView) v.findViewById(R.id.searchViewSlots);

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
        return v;
    }

    private class ParseURL extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
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
            whereClause.append("Person[mycreatedslot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // progressBar.setVisibility(View.INVISIBLE);
            if (isAdded()) {

                if (!slot.isEmpty()) {

                    rv = (RecyclerView) v.findViewById(R.id.rv);

                    rv.setHasFixedSize(true);
                    llm = new LinearLayoutManager(v.getContext());
                    rv.setLayoutManager(llm);

                    Resources r = getResources();

                    adapter = new RVAdapter(slot, r);

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
                        public void onItemLongClick(View view, final int position) {


                            dialog = new AlertDialog.Builder(v.getContext())
                                    .setTitle("Cancel Schedule?")
                                    .setMessage("Do you want cancel your " + slot.get(position).getSubject() + " schedule?")
                                    .setIcon(R.drawable.ic_questionmark)
                                    .setPositiveButton("Yup, Cancel", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Cancelling Schedule: " + slot.get(position).getSubject() + " ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new CancelEvent(position).execute();
                                        }
                                    })
                                    .setNegativeButton("Nope, Keep", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();


                        }
                    }
                    ));
                    progressBar.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    searchViewSlots.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    textViewTextNoSlotAvaliable.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class CancelEvent extends AsyncTask<Void, Integer, Void> {

        int positionInList;

        public CancelEvent(int positionInList) {

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


            StringBuilder whereClause = new StringBuilder();
            whereClause.append("Slot[attendees]");
            whereClause.append(".objectId='").append(slot.get(positionInList).getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            personsToSmsCollection = Backendless.Data.of(Person.class).find(dataQuery);
            personsToSms = personsToSmsCollection.getData();

            String fullnamePersonLoggedIn = personLoggedIn.getFullname();
            String dateofslot = slot.get(positionInList).getDateofslot();
            String subject = slot.get(positionInList).getSubject();
            String placeofSlot = slot.get(positionInList).getPlace();

            for (Person pId : personsToSms) {

                sendsmss(pId.getPhone(), fullnamePersonLoggedIn, subject, dateofslot, placeofSlot);
            }


            // Deleting process

            List<String> relations = new ArrayList<String>();
            relations.add("myCreatedSlot");
            Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

            int pos = 0;

            for (int i = 0; i < person.myCreatedSlot.size(); i++) {

                if (person.myCreatedSlot.get(i).getObjectId().equals(slot.get(positionInList).getObjectId())) {
                    pos = i;
                    break;
                }
            }

            eventRemoved = slot.get(positionInList).getSubject();
            if (slot.get(positionInList).getLocation() != null) {
                Backendless.Geo.removePoint(slot.get(positionInList).getLocation());
            }

            Long result = Backendless.Persistence.of(Slot.class).remove(slot.get(positionInList)); // TODO toast "'result' events removed"

            slot.remove(positionInList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rv.setAdapter(null);

            if (!slot.isEmpty()) {

                rv.setHasFixedSize(true);

                rv.setLayoutManager(llm);

                //   rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

                Resources r = getResources();

                adapter = new RVAdapter(slot, r);

                rv.setAdapter(adapter);
                ringProgressDialog.dismiss();
            } else {
                ringProgressDialog.dismiss();
                searchViewSlots.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                textViewTextNoSlotAvaliable.setVisibility(View.VISIBLE);
            }
            Toast.makeText(v.getContext(), eventRemoved + " was cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String from, String subject, String date, String place) {

        String messageSubString = "Automated TXT - Amplified Schedule: Schedule" + subject + " on the " + date + " at " + place + " was cancelled by " + from;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
    }
}