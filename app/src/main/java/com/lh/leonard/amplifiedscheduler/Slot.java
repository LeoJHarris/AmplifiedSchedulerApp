package com.lh.leonard.amplifiedscheduler;

import java.util.List;

/**
 * Created by Leonard on 25/04/2015.
 */

public class Slot extends Schedule {

    public String message;
    public List<Person> attendees;
    public Integer maxattendees;

    public String ownername;
    public String objectId;

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