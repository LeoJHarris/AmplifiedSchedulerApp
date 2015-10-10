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
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class SlotsImGoingTo extends Fragment {

    Contact contact;
    Person person;
    List<Slot> slot;
    BackendlessCollection<Person> persons;
    BackendlessCollection<Slot> slots;
    SearchView searchViewSlots;
    AutoResizeTextView textViewTextNoSlotAvaliable;
    RVAdapter adapter;
    private ProgressBar progressBar;
    AlertDialog dialog;
    BackendlessUser userLoggedIn = Backendless.UserService.CurrentUser();
    ProgressDialog ringProgressDialog;
    View v;
    String eventRemoved;
    RecyclerView rv;
    LinearLayoutManager llm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.slots_display, container, false);

        Backendless.Persistence.mapTableToClass("Person", Person.class);
        Backendless.Persistence.mapTableToClass("Contact", Contact.class);
        Backendless.Persistence.mapTableToClass("Slot", Slot.class);

        TextView textViewTitleSlotsDisplay = (TextView) v.findViewById(R.id.textViewTitleSlotsDisplay);
        textViewTextNoSlotAvaliable = (AutoResizeTextView) v.findViewById(R.id.textViewTextNoSlotAvaliable);

        textViewTitleSlotsDisplay.setText("Going To Events");

        final Typeface regularFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/GoodDog.otf");


        textViewTextNoSlotAvaliable.setTypeface(regularFont);
        textViewTitleSlotsDisplay.setTypeface(regularFont);

        person = (Person) userLoggedIn.getProperty("persons");

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
            whereClause.append("Person[goingToSlot]");
            whereClause.append(".objectId='").append(person.getObjectId()).append("'");

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause.toString());

            slots = Backendless.Data.of(Slot.class).find(dataQuery);
            slot = slots.getData();

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

                            Intent slotDialogIntent = new Intent(getActivity(), SlotsImGoingToDialog.class);

                            slotDialogIntent.putExtra("slotRef", position);

                            startActivity(slotDialogIntent);
                        }

                        @Override
                        public void onItemLongClick(View view, final int position) {

                            dialog = new AlertDialog.Builder(v.getContext())
                                    .setTitle("Not Going To Event?")
                                    .setMessage("Do you want to remove " + slot.get(position).getSubject())
                                    .setIcon(R.drawable.ic_questionmark)
                                    .setPositiveButton("Remove Event", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.dismiss();
                                            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",
                                                    "Remove event " + slot.get(position).getSubject() + " ...", true);
                                            ringProgressDialog.setCancelable(false);
                                            new RemoveEvent(position).execute();

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

    private class RemoveEvent extends AsyncTask<Void, Integer, Void> {

        int positionInList;

        public RemoveEvent(int positionInList) {

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
            relations.add("goingToSlot");
            Person person1 = Backendless.Data.of(Person.class).findById(person.getObjectId(), relations);

            int pos = 0;

            for (int i = 0; i < person1.getGoingToSlot().size(); i++) {

                if (person1.getGoingToSlot().get(i).getObjectId().equals(slot.get(positionInList).getObjectId())) {
                    pos = i;
                    break;
                }
            }

            sendsmss(slot.get(positionInList).getPhone(), "Automated TXT - EVENTHUB101: " + person.getFullname() + "  has indicated he/she is no longer going to your " + slot.get(positionInList).getSubject() + " event on the " + slot.get(positionInList).getDateofslot());


            person1.getGoingToSlot().remove(pos);
            eventRemoved = slot.get(positionInList).getSubject();
            slot.remove(positionInList);
            Person updatedPersonLoggedIn = Backendless.Data.of(Person.class).save(person1);

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
            Toast.makeText(v.getContext(), eventRemoved + " was removed", Toast.LENGTH_SHORT).show();
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