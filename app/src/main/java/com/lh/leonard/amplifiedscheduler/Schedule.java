package com.lh.leonard.amplifiedscheduler;

import com.backendless.geo.GeoPoint;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Leonard on 10-Feb-16.
 */
public class Schedule {

    public GeoPoint location;
    public Date startdate;
    public Date enddate;
    public boolean alldayevent;
    public String subject;
    public String objectId;
    public String note;
    public String place;

    public String getObjectId() {
        return objectId;
    }

    public String getSubject() {

        if (subject != null) {
            return subject;
        } else {
            return "";
        }
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public boolean isAllDayEvent() {
        return alldayevent;
    }

    public String getPlace() {
        return place;
    }

    public String getNote() {
        return note;
    }

    public Calendar getStartCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startdate);
        return cal;
    }

    public Calendar getEndCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(enddate);
        return cal;
    }

}
