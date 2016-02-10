package com.lh.leonard.amplifiedscheduler;

import com.backendless.geo.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 25/04/2015.
 */

public class Slot extends Schedule {

    public String message;
    public List<Person> attendees;
    public Integer maxattendees;
    public String phone;
    public String ownername;

    public String getPhone() {
        return phone;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}