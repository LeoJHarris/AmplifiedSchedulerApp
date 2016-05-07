package com.lh.leonard.amplifiedscheduler;

import java.util.List;

/**
 * Created by Leonard on 18/07/2015.
 */
public class Person {

    //TODO make private for all classes
    public String fname;
    public String lname;
    public String fullname;
    public List<Person> contacts;
    public String objectId;

    public String email;
    public List<Person> personsImRequesting;
    public List<Person> personsRequestingMe;
    public String country;
    public List<Slot> pendingResponseSlot;
    public List<Slot> myCreatedSlot;
    public List<Slot> goingToSlot;
    public String gender;
    public String ownerId;
    public String social;
    public List<Plan> myPlans;
    public String picture;
    public String deviceId;
    public boolean isSilhouette;

    public boolean getIsSilhouette() {
        return isSilhouette;
    }

    public void setSilhouette(boolean silhouette) {
        isSilhouette = silhouette;
    }

    public String getGender() {
        return gender;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public List<Plan> getMyPlans() {
        return myPlans;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getSocial() {
        return social;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<Person> getPersonsRequestingMe() {
        return personsRequestingMe;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public List<Slot> getMyCreatedSlot() {
        return myCreatedSlot;
    }

    public List<Slot> getGoingToSlot() {
        return goingToSlot;
    }

    public List<Slot> getPendingResponseSlot() {
        return pendingResponseSlot;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public List<Person> getContacts() {
        return contacts;
    }

    public void setContacts(List<Person> contacts) {
        this.contacts = contacts;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getObjectId() {
        return objectId;
    }
}