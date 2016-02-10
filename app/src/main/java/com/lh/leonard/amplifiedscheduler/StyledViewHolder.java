package com.lh.leonard.amplifiedscheduler;

import android.view.View;

import de.jodamob.android.calendar.CalendarDayViewHolder;
import de.jodamob.android.calendar.Day;
import de.jodamob.android.calendar.DayState;

public class StyledViewHolder extends CalendarDayViewHolder {

    private final AutoResizeTextView detailView;

    public StyledViewHolder(View itemView) {
        super(itemView);
        detailView = (AutoResizeTextView) itemView.findViewById(R.id.date_details);
    }

    @Override
    public void bind(Day day, DayState state) {
        super.bind(day, state);
        detailView.setText("");
    }
    public void bindBirthday(Schedule event) {
        detailView.setText(event.getSubject());
    }
}