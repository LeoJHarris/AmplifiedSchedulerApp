package com.lh.leonard.amplifiedscheduler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import de.jodamob.android.calendar.CalendarAdapter;
import de.jodamob.android.calendar.Day;
import de.jodamob.android.calendar.VisibleMonths;

import static de.jodamob.android.calendar.CalendarUtil.isSameDayIgnoreYear;

public class StyledAdapter extends CalendarAdapter {

    private final List<Slot> birthdays;

    public StyledAdapter(@LayoutRes int layout, LayoutInflater inflater, VisibleMonths data, List<Slot> birthdays) {
        super(layout, inflater, data);
        this.birthdays = birthdays;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder createViewHolder(View view) {
        return new StyledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Day day = data.getAt(position);
        for (Slot birthday : birthdays) {
            if (isSameDayIgnoreYear(birthday.getStartCalendar().getTime(), day.getDate())) {
                ((StyledViewHolder) holder).bindBirthday(birthday);
                break;
            }
        }
    }
}