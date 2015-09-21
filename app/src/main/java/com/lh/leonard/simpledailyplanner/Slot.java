package com.lh.leonard.simpledailyplanner;

import com.backendless.geo.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 25/04/2015.
 */

public class Slot {

    public String subject;
    public String message;
    public String start;
    public String end;
    public boolean appointmentOnly;
    public List<Person> attendees;
    public Integer maxattendees;
    public String ownerId;
    public String objectId;
    public String owneremailaddress;
    public String dateofslot;
    public String ownername;
    public GeoPoint location;



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
        return ownername;
    }

    public void setAttendees(List<Person> attendees) {
        this.attendees = attendees;
    }

    public void setMaxattendees(Integer maxattendees) {
        this.maxattendees = maxattendees;
    }
    public void setDateofslot(String dateofslot) {
        this.dateofslot = dateofslot;
    }

    public String getDateofslot() {
        return dateofslot;
    }

    public String getOwneremailaddress() {
        return owneremailaddress;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String title) {
        this.subject = title;
    }

    // public GeoPoint getLocation() {
    ////    return location;
    //   }

    ///// public void setLocation(GeoPoint coordinated) {
    //  this.location = coordinated;
    // }

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

    public Boolean getAppointmentOnly() {
        return appointmentOnly;
    }

    public void setAppointmentOnly(Boolean appointmentOnly) {
        this.appointmentOnly = appointmentOnly;
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