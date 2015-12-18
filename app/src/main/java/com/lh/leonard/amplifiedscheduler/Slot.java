package com.lh.leonard.amplifiedscheduler;

import com.backendless.geo.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 25/04/2015.
 */

public class Slot {

    public Date startdate;
    public Date enddate;
    public boolean alldayevent;
    public String subject;
    public String message;
    public String start;
    public String end;
    public List<Person> attendees;
    public Integer maxattendees;
    public String objectId;
    public GeoPoint location;
    public String phone;
    public String place;
    public String note;

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

    public boolean isAllDayEvent() {
        return alldayevent;
    }

    public String getPlace() {
        return place;
    }

    public String getPhone() {
        return phone;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public List<Person> getAttendees() {
        return attendees;
    }

    public Integer getMaxattendees() {
        return maxattendees;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStart() {
        return start;
        //return getDate(start);
    }

    public void setStart(String start) {

        this.start = start;
    }

    public String getEnd() {

        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}