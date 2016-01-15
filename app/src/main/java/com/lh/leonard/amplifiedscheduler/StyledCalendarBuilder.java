package com.lh.leonard.amplifiedscheduler;

import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import de.jodamob.android.calendar.CalendarAdapter;
import de.jodamob.android.calendar.CalendarBuilder;
import de.jodamob.android.calendar.VisibleMonths;

public class StyledCalendarBuilder extends CalendarBuilder {

    private final List<Slot> birthdays = new ArrayList<>();

    public StyledCalendarBuilder(List<Slot> events) {
        super(R.layout.calendar_item, R.layout.calendar_header);
        this.birthdays.addAll(events);
    }

    @Override
    public CalendarAdapter createAdapterFor(LayoutInflater inflater, VisibleMonths months) {
        return new StyledAdapter(R.layout.calendar_item, inflater, months, birthdays);
    }
}