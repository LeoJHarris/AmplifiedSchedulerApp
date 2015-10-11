package com.lh.leonard.eventhub101;

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
import android.widget.TableRow;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class SlotsAwaitingMyResponse extends Fragment {

    Contact contact;
    Person personLoggedIn;
    RecyclerView rv;
    List<Slot> slot;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    AlertDialog dialog;
    AutoResizeTextView textViewTextNoSlotAvaliable;
    SearchView searchViewSlots;
    ProgressDialog ringProgressDialog;
    RVAdapter adapter;
    LinearLayoutManager llm;
    private ProgressBar progressBar;
    String eventRemoved;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.slots_display, container, false);

        getActivity().setTitle("Event Invites");

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        personLoggedIn = (Person) userLoggedIn.getProperty("persons");

        textViewTextNoSlotAvaliable = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoSlotAvaliable);

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");

        textViewTextNoSlotAvaliable.setTypeface(regularFont);

        Backendless.Persistence.mapTableToClass("Slot", Slot.class);

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
        new ParseURL().execute();
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
            whereClause.append("Person[pendingResponseSlot]");
            whereClause.append(".objectId='").append(personLoggedIn.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();

            for (int j = 0; j < slot.size(); j++) {
                if (slot.get(j).getMaxattendees() != 0) {
                    if (slot.get(j).attendees.size() >= slot.get(j).getMaxattendees()) {
                        slot.remove(j);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
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

                            Intent slotDialogIntent = new Intent(getActivity(), SlotsPendingMyResponseDialog.class);

                            slotDialogIntent.putExtra("slotRef", position);

                            startActivity(slotDialogIntent);
                        }

                        @Override
                        public void onItemLongClick(View view, final int position) {

                            dialog = new AlertDialog.Builder(v.getContext())
                                    .setTitle("Event to go to?")
                                    .setMessage("Do you want to go to " + slot.get(position).getSubject())
                                    .setIcon(R.drawable.ic_questionmark)
                                    .setPositiveButton("Going", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Accepting invited event " + slot.get(position).getSubject() + " ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new GoingToEvent(position).execute();

                                        }
                                    })
                                    .setNegativeButton("Not Going", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Declining invited event" + slot.get(position).getSubject() + " ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new NotGoingToEvent(position).execute();
                                        }
                                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                    ));
                    progressBar.setVisibility(View.GONE);
                    TableRow rowSearchView = (TableRow) v.findViewById(R.id.rowSearchView);
                    rowSearchView.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    textViewTextNoSlotAvaliable.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class NotGoingToEvent extends AsyncTask<Void, Integer, Void> {

        int positionInList;

        public NotGoingToEvent(int positionInList) {

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
            relations.add("pendingResponseSlot");
            Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

            int pos = 0;

            for (int i = 0; i < person.pendingResponseSlot.size(); i++) {

                if (person.pendingResponseSlot.get(i).getObjectId().equals(slot.get(positionInList).getObjectId())) {
                    pos = i;
                    break;
                }
            }

            sendsmss(slot.get(positionInList).getPhone(), "Automated TXT - EVENTHUB101: " + person.getFullname() + " has indicated he/she is not to your " + slot.get(positionInList).getSubject() + " event on the " + slot.get(positionInList).getDateofslot());

            person.pendingResponseSlot.remove(pos);
            eventRemoved = slot.get(positionInList).getSubject();
            slot.remove(positionInList);
            Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rv.setAdapter(null);

            if (!slot.isEmpty()) {

                rv.setHasFixedSize(true);

                rv.setLayoutManager(llm);

                // rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

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
            Toast.makeText(v.getContext(), eventRemoved + " was declined", Toast.LENGTH_SHORT).show();
        }
    }

    private class GoingToEvent extends AsyncTask<Void, Integer, Void> {

        int position;

        public GoingToEvent(int position) {
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

                List<String> relations = new ArrayList<String>();
                relations.add("pendingResponseSlot");
                Person person = Backendless.Data.of(Person.class).findById(personLoggedIn.getObjectId(), relations);

                int pos = 0;

                for (int i = 0; i < person.pendingResponseSlot.size(); i++) {

                    if (person.pendingResponseSlot.get(i).getObjectId().equals(slot.get(position).getObjectId())) {
                        pos = i;
                        break;
                    }
                }

                sendsmss(slot.get(position).getPhone(), "Automated TXT - EVENTHUB101: " + person.getFullname() + "  has indicated he/she is going to your " + slot.get(position).getSubject() + " event on the " + slot.get(position).getDateofslot());

                person.pendingResponseSlot.remove(pos);

                eventRemoved = slot.get(position).getSubject();

                Backendless.Data.of(Person.class).save(person);

                Person p = Backendless.Data.of(Person.class).findById(personLoggedIn);

                p.addSlotGoingToSlot(slot.get(position));

                slot.remove(position);
                personLoggedIn = Backendless.Data.of(Person.class).save(p);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rv.setAdapter(null);

            if (!slot.isEmpty()) {

                rv.setAdapter(null);

                rv.setHasFixedSize(true);

                rv.setLayoutManager(llm);

                // rv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

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
            Toast.makeText(v.getContext(), "Going to " + eventRemoved, Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void sendsmss(String phoneNumber, String message) {

        int lengthToSubString;
        int lengthMessage = message.length();
        if (lengthMessage < 300) {
            lengthToSubString = lengthMessage;
        } else {
            lengthToSubString = 300;
        }
        String messageSubString = message.substring(0, lengthToSubString);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, messageSubString, null, null);
    }
}
