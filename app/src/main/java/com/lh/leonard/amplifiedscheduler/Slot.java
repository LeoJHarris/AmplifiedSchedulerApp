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
    public boolean allDayEvent;
    public String subject;
    public String message;
    public String start;
    public String end;
    public List<Person> attendees;
    public Integer maxattendees;
    public String ownerId;
    public String objectId;
    public String ownername;
    public GeoPoint location;
    public String phone;
    public String place;
    public String note;

    public Date getStartdate() {
        return startdate;
    }

    public String getNote() {
        return note;
    }

    public Calendar getStartCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startdate);
        return cal;
    }

    public Date getEndDate() {
        return enddate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public void setEndDate(Date endDate) {
        this.enddate = endDate;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public String getPlace() {
        return place;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnername() {
        if (ownername != null) {
            return ownername;
        } else {
            return "";
        }
    }

    public void addAttendee(Person person) {
        if (attendees == null)
            attendees = new ArrayList<>();
        attendees.add(person);
    }

    public void setAttendees(List<Person> attendees) {
        this.attendees = attendees;
    }

    public String getObjectId() {
        return objectId;
    }

    public long parseStringToLong() {
        return Long.valueOf(objectId);

    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
        //this.start = parseStringDate(date);
    }

    public String getEnd() {

        return end;

        //return getDate(end);
    }

    public void setEnd(String end) {
        this.end = end;

        //this.end = parseStringDate(endDate);
    }

    private String getDate(Date date) {
        String format = "EEEE, MMMM dd, yyyy, h:mm a";
        // Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dateEnd = simpleDateFormat.format(date);
        return dateEnd;
    }

    private Date parseStringDate(String stringDate) {

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date result = null;
        try {
            result = df.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}