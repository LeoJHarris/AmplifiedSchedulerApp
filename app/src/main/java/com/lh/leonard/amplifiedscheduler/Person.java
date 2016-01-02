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
    public String phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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