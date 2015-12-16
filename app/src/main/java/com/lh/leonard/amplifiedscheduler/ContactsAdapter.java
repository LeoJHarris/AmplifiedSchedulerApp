package com.lh.leonard.amplifiedscheduler;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Leonard on 17/07/2015.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> implements Filterable {

    private List<Person> listSlots;
    Typeface RobotoBlack;
    Typeface RobotoCondensedLightItalic;
    Typeface RobotoCondensedLight;
    Typeface RobotoCondensedBold;
    private List<Person> orig;
    Drawable drawableRequesting;
    Drawable drawableContacts;
    Drawable drawableActionRequired;
    HashMap<Integer, Integer> hashMap = new HashMap<>();
    int VAL;
    Resources r;
    Drawable draw = r.getDrawable(R.drawable.user_requesting);

    public ContactsAdapter(List<Person> list, int val, Resources r) {

        listSlots = list;
        VAL = val;
        r = r;
    }

    public ContactsAdapter(List<Person> list, HashMap<Integer, Integer> hashMap, Resources r) {

        listSlots = list;
        this.hashMap = hashMap;
        VAL = 10;
        this.r = r;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_card_view, viewGroup, false);
        RobotoBlack = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Roboto-Black.ttf");
        RobotoCondensedLightItalic = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        RobotoCondensedLight = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        RobotoCondensedBold = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        ContactViewHolder pvh = new ContactViewHolder(v);
        drawableRequesting = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_friend_requested);
        drawableActionRequired = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_actionrequiredcontactspng);
        drawableContacts = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_currentcontact);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder slotViewHolder, int i) {

        if (VAL == 0) {
            slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableContacts, null);
        } else if (VAL == 1) {
            slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableActionRequired, null);
        } else {
            if (hashMap.get(i) != null) {
                if (hashMap.get(i) == 1) {
                    slotViewHolder.userImage.setImageDrawable(draw);
                    slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRequesting, null);

                } else if (hashMap.get(i) == 2) {
                    slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableActionRequired, null);

                } else if (hashMap.get(i) == 3) {
                    slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableContacts, null);

                } else if (hashMap.get(i) == 4) {
                    slotViewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableContacts, null);

                }
            }
        }
        slotViewHolder.personCountry.setTypeface(RobotoCondensedLight);
        slotViewHolder.name.setTypeface(RobotoCondensedLight);
        slotViewHolder.name.setText(listSlots.get(i).getFname() + " " + listSlots.get(i).getLname());
        slotViewHolder.personCountry.setText("Country: " + listSlots.get(i).getCountry());
        slotViewHolder.personCountry.setText("Last Activity: " + listSlots.get(i).getUpdated());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return listSlots.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Person> results = new ArrayList<>();
                if (orig == null)
                    orig = listSlots;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final Person g : orig) {
                            if (g.getLname().toLowerCase().contains(constraint.toString()) || g.getFname().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listSlots = (ArrayList<Person>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        AutoResizeTextView name;
        AutoResizeTextView personCountry;
        ImageView userImage;

        ContactViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (AutoResizeTextView) itemView.findViewById(R.id.textViewName);

            personCountry = (AutoResizeTextView) itemView.findViewById(R.id.person_country); //
            userImage = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}