package com.lh.leonard.amplifiedscheduler;

import com.backendless.geo.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 09-Feb-16.
 */
public class Plan {

    public Date startdate;
    public Date enddate;
    public boolean alldayevent;
    public String subject;
    public String objectId;
    public GeoPoint location;
    public String place;
    public String note;

    public String getObjectId() {
        return objectId;
    }

    public boolean isAllDayEvent() {
        return alldayevent;
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

    public String getPlace() {
        return place;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }


    public String getSubject() {

        if (subject != null) {
            return subject;
        } else {
            return "";
        }
    }
}
