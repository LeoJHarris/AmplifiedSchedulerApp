package com.lh.leonard.eventhub101;

import java.util.List;

/**
 * Created by Leonard on 25/07/2015.
 */
public class Contact {

    public List<Slot> pendingresponseslot;
    public List<Slot> mycreatedslot;
    public List<Slot> goingtoslot;
    public String objectId;

    public String getObjectID() {
        return objectId;
    }

    public List<Slot> getMyCreatedSlot() {
        return mycreatedslot;
    }

    public void setMyCreatedSlot(List<Slot> mycreatedslot) {
        this.mycreatedslot = mycreatedslot;
    }

    public List<Slot> getGoingToSlot() {
        return goingtoslot;
    }

    public List<Slot> getPendingResponseSlot() {
        return pendingresponseslot;
    }

    public void setGoingToSlot(List<Slot> goingtoslot) {
        this.goingtoslot = goingtoslot;
    }

    public void setPendingResponseSlot(List<Slot> pendingresponseslot) {
        this.pendingresponseslot = pendingresponseslot;
    }
}
