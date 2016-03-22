package com.lh.leonard.amplifiedscheduler;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

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
    HashMap<Integer, Integer> hashMap = new HashMap<>();
    int VAL;

    Drawable requestedDrawable;
    Drawable requestingDrawable;
    Drawable friendDrawable;
    Drawable blankDrawable;
    Context context;

    public ContactsAdapter(List<Person> list, int val, Context context) {

        listSlots = list;
        VAL = val;
        this.context = context;
    }

    public ContactsAdapter(List<Person> list, HashMap<Integer, Integer> hashMap, Context context) {

        listSlots = list;
        this.hashMap = hashMap;
        VAL = 10;
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_card_view, viewGroup, false);
        RobotoBlack = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Roboto-Black.ttf");
        RobotoCondensedLightItalic = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        RobotoCondensedLight = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        RobotoCondensedBold = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        ContactViewHolder pvh = new ContactViewHolder(v);
        requestedDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.requested_user);
        requestingDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.requesting_user);
        friendDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.friend_user);
        blankDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.user_blank);


        return pvh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder slotViewHolder, int i) {

        if (listSlots.get(i).getPicture() != null) {
            if (!listSlots.get(i).getPicture().equals("")) {
                Picasso.with(context).load(listSlots.get(i).getPicture()).into(slotViewHolder.userImage);
            }
        }else {


            //In my contacts list
            if (VAL == 0) {
                slotViewHolder.userImage.setImageDrawable(friendDrawable);
            }
            //requestingDrawable
            else if (VAL == 1) {
                slotViewHolder.userImage.setImageDrawable(requestingDrawable);
            } else {

                if (hashMap.get(i) != null) {
                    //requestingDrawable
                    if (hashMap.get(i) == 1) {
                        slotViewHolder.userImage.setImageDrawable(requestedDrawable);
                        //requestingDrawable
                    } else if (hashMap.get(i) == 2) {
                        slotViewHolder.userImage.setImageDrawable(requestingDrawable);
                        // friendDrawable
                    } else if (hashMap.get(i) == 3) {
                        slotViewHolder.userImage.setImageDrawable(friendDrawable);
                        //friendDrawable
                    } else if (hashMap.get(i) == 4) {
                        slotViewHolder.userImage.setImageDrawable(friendDrawable);
                        // blankDrawable not friends
                    } else if (hashMap.get(i) == 0) {
                        slotViewHolder.userImage.setImageDrawable(blankDrawable);
                    }
                }
            }
        }
        slotViewHolder.personCountry.setTypeface(RobotoCondensedLight);
        slotViewHolder.name.setTypeface(RobotoCondensedLight);
        slotViewHolder.name.setText(listSlots.get(i).getFname() + " " + listSlots.get(i).getLname());
        slotViewHolder.personCountry.setText("Country: " + listSlots.get(i).getCountry());
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

                        if (isValidEmail(constraint)) {
                            for (final Person g : orig) {
                                results.add(g);
                                oReturn.values = results;
                                return oReturn;
                            }
                        }

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
            cv = (CardView) itemView.findViewById(R.id.cardview);
            name = (AutoResizeTextView) itemView.findViewById(R.id.textViewName);
            personCountry = (AutoResizeTextView) itemView.findViewById(R.id.person_country);
            userImage = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}