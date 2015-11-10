package com.lh.leonard.amplifiedscheduler;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonard on 17/07/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SlotViewHolder> implements Filterable {

    Resources resources;
    private List<Slot> listSlots;
    Typeface RobotoBlack;
    Typeface RobotoCondensedLightItalic;
    Typeface RobotoCondensedLight;
    Typeface RobotoCondensedBold;
    private List<Slot> orig;

    public RVAdapter(List<Slot> list, Resources R) {

        resources = R;
        listSlots = list;
    }

    @Override
    public SlotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        RobotoBlack = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Roboto-Black.ttf");
        RobotoCondensedLightItalic = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-LightItalic.ttf");
        RobotoCondensedLight = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Light.ttf");
        RobotoCondensedBold = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        SlotViewHolder pvh = new SlotViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(SlotViewHolder slotViewHolder, int i) {
        slotViewHolder.slotTitle.setText(listSlots.get(i).getSubject());
        slotViewHolder.slotDate.setText("Date: " + listSlots.get(i).getDateofslot().toString());
        slotViewHolder.slotFullName.setText("From: " + listSlots.get(i).getOwnername().toString());

//        if (listSlots.get(i).getMaxattendees() != 0) {
//            Integer spacesAvaliable = listSlots.get(i).getMaxattendees();
//            Integer going = listSlots.get(i).getAttendees().size();
//            {
//
//                slotViewHolder.slotAvaliability.setText(going + " going, waiting response from " + (spacesAvaliable - going));
//
//            }
//
//        } else {
//            slotViewHolder.slotAvaliability.setText("Unlimited Spaces");
//        }

        int lengthToSubString;
        int lengthMessage = listSlots.get(i).getMessage().length();
        if (lengthMessage < 50) {
            lengthToSubString = lengthMessage;
        } else {
            lengthToSubString = 50;
        }
        String messageSubString = listSlots.get(i).getMessage().substring(0, lengthToSubString);
        messageSubString += "...";
        slotViewHolder.slotMessage.setText("Message: " + messageSubString);

       // slotViewHolder.slotAvaliability.setTypeface(RobotoCondensedLight);
        slotViewHolder.slotFullName.setTypeface(RobotoCondensedLight);
        slotViewHolder.slotTitle.setTypeface(RobotoCondensedLight);
        slotViewHolder.slotMessage.setTypeface(RobotoCondensedLight);
        slotViewHolder.slotDate.setTypeface(RobotoCondensedLight);

        //personViewHolder.personPhoto.setImageResource(listTeachers.get(i).getPhoto());
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
                final List<Slot> results = new ArrayList<Slot>();
                if (orig == null)
                    orig = listSlots;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final Slot g : orig) {
                            // Add on g.getSubject().toLowerCase().contains(constraint.toString()))
                            if (g.getSubject().toLowerCase().contains(constraint.toString()) || g.getOwnername().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listSlots = (ArrayList<Slot>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        AutoResizeTextView slotTitle;
        AutoResizeTextView slotDate;
        AutoResizeTextView slotMessage;
        AutoResizeTextView slotFullName;
        //  AutoResizeTextView slotAvaliability;

        SlotViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            slotTitle = (AutoResizeTextView) itemView.findViewById(R.id.slot_title);
            slotDate = (AutoResizeTextView) itemView.findViewById(R.id.slot_date);
            slotMessage = (AutoResizeTextView) itemView.findViewById(R.id.slot_message);
            slotFullName = (AutoResizeTextView) itemView.findViewById(R.id.slot_person_name);
            //     slotAvaliability = (AutoResizeTextView) itemView.findViewById(R.id.Availability);
        }
    }
}