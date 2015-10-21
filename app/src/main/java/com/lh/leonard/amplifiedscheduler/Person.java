package com.lh.leonard.amplifiedscheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
    public String ownerId;
    public String phone;
    public String email;
    public List<Person> personsImRequesting;
    public List<Person> personsRequestingMe;
    public List<Slot> unseenSlots;
    public String country;
    public String town;
    public Date updated;

    public List<Slot> pendingResponseSlot;
    public List<Slot> myCreatedSlot;
    public List<Slot> goingToSlot;

    public Boolean removeGoingToSlot(Integer position) {

        goingToSlot.remove(position);
        return true;
    }

    public Boolean removeUnseenSlot(Integer position) {

        unseenSlots.remove(position);
        return true;
    }

    public void setUnseenSlots(List<Slot> unseenSlots) {
        this.unseenSlots = unseenSlots;
    }

    public List<Person> getPersonsImRequesting() {
        return personsImRequesting;
    }

    public void setUnSeenSlots(List<Slot> unseenSlots) {
        this.unseenSlots = unseenSlots;
    }

    public List<Slot> getUnseenSlots() {
        return unseenSlots;
    }

    public String getUpdated() {
        String format = "dd/MM/yyyy";
        // Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dateEnd = "";
        if (updated != null) {
            dateEnd = simpleDateFormat.format(updated);
        } else {
            dateEnd = "NA";
        }
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

    public Integer numberUnseenSlots() {

        return unseenSlots.size(); // 1 based list
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public boolean removeContactForBoth(Person personToRemove) {

        List<Person> otherList = personToRemove.contacts;
        int otherListSize = otherList.size() - 1;
        int myListSize = contacts.size() - 1;

        boolean imRemovedFromHisList = false; // Found, not removed
        boolean hesRemovedFromMyList = false;

        int indexInMyList = 0;
        int indexInHisList = 0;

        int j = 0;

        //Check we dont search index higher then either list lize and that contacts have been removed from each list
        while (((!imRemovedFromHisList) || (!hesRemovedFromMyList)) && ((j <= otherListSize) || (j <= myListSize))) {


            if (!imRemovedFromHisList) {
                if (objectId.equals(otherList.get(j).objectId)) {

                    //otherList.remove(j);
                    indexInHisList = j;
                    imRemovedFromHisList = true;
                }
            }

            if (!hesRemovedFromMyList) {
                if (personToRemove.getObjectId().equals(contacts.get(j).objectId)) {

                    //contacts.remove(j);
                    indexInMyList = j;
                    hesRemovedFromMyList = true;
                }
            }
            j++;
        }

        if (imRemovedFromHisList && hesRemovedFromMyList) {

            contacts.remove(indexInMyList);
            otherList.remove(indexInHisList);
            return true;

        } else {
            return false;
        }
    }


    public void addPersonsRequestingMe(Person contact) {
        if (personsRequestingMe == null)
            personsRequestingMe = new ArrayList<Person>();
        personsRequestingMe.add(contact);
    }


    /***
     * Method called on non-logged in user to remove person being requested as contact from personImRequesting List
     *
     * @param person
     * @return
     */
    public boolean removePersonImRequesting(Person person) {

        Iterator i = personsImRequesting.iterator();

        int j = 0;
        while (i.hasNext()) {
            Person element = (Person) i.next();

            if (element.getObjectId().equals(person.getObjectId())) {

                //  personsImRequesting.remove(addingContact);

                personsImRequesting.remove(j);
                return true;
            }
            j++;
        }
        return false;
    }

    public boolean removeContact(Person person) {

        Iterator i = contacts.iterator();

        int j = 0;
        while (i.hasNext()) {
            Person element = (Person) i.next();

            if (element.getObjectId().equals(person.getObjectId())) {

                //  personsImRequesting.remove(addingContact);

                contacts.remove(j);
                return true;
            }
            j++;
        }
        return false;
    }


    /**
     * Method called on logged in user to remove person who is requesting me as contact from personRequestingMe List
     *
     * @param person
     * @return boolean whether user has been removed
     */
    public boolean removePersonRequestingMe(Person person) {

        Iterator i = personsRequestingMe.iterator();

        int j = 0;
        while (i.hasNext()) {
            Person element = (Person) i.next();

            if (element.getObjectId().equals(person.getObjectId())) {

                //  personsImRequesting.remove(addingContact);

                personsRequestingMe.remove(j);
                return true;
            }
            j++;
        }
        return false;
    }


    public void addToImRequesting(Person otherRequested) {


        personsImRequesting.add(otherRequested);


    }

    public void addToRequestingMe(Person otherRequested) {

    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @param addingContact
     * @return 0 Added to my requesting list. 1 if contact removed from requesting. 2 if contact is already a contact.
     */
    public Integer addPersonsImRequesting(Person addingContact) {
        if (!contacts.contains(addingContact)) {

            //  personsImRequesting.iterator()

            boolean removed = false;
            Iterator i = personsImRequesting.iterator();
            int j = 0;
            while (i.hasNext()) {
                Person element = (Person) i.next();

                if (element.getObjectId().equals(addingContact.getObjectId())) {

                    //  personsImRequesting.remove(addingContact);

                    personsImRequesting.remove(j);
                    removed = true;
                    return 1;
                }
                j++;
            }
            if (!removed) {
                personsImRequesting.add(addingContact);
                return 0;
            }
        }
        return 2;
    }


    public void setPersonsRequestingMe(List<Person> personsrequestingme) {
        this.personsRequestingMe = personsrequestingme;
    }

    public void addPersonImRequesting(Person personsimrequesting) {
        if (personsImRequesting == null)
            personsImRequesting = new ArrayList<Person>();
        personsImRequesting.add(personsimrequesting);
    }

    public List<Person> getPersonsRequestingMe() {
        return personsRequestingMe;
    }

    public void addSlotToPendingResponseSlot(Slot slotItem) {
        if (pendingResponseSlot == null)
            pendingResponseSlot = new ArrayList<Slot>();
        pendingResponseSlot.add(slotItem);
    }

    public void addToUnseenEvents(Slot slotEvent) {

        if (unseenSlots == null)
            unseenSlots = new ArrayList<Slot>();
        unseenSlots.add(slotEvent);

    }

    public void addContact(Person contact) {
        if (contacts == null) {
            contacts = new ArrayList<Person>();
        }
        contacts.add(contact);
    }

    public void addPersonRequestingMe(Person contact) {
        if (personsRequestingMe == null) {
            personsRequestingMe = new ArrayList<Person>();
        }
        personsRequestingMe.add(contact);
    }

    public void addSlotToMyCreatedSlot(Slot slotItem) {
        if (myCreatedSlot == null)
            myCreatedSlot = new ArrayList<Slot>();
        myCreatedSlot.add(slotItem);
    }

    public void addSlotGoingToSlot(Slot slotItem) {
        if (goingToSlot == null)
            goingToSlot = new ArrayList<Slot>();
        goingToSlot.add(slotItem);
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

    public void setMyCreatedSlot(List<Slot> mycreatedslot) {
        this.myCreatedSlot = mycreatedslot;
    }

    public List<Slot> getGoingToSlot() {
        return goingToSlot;
    }

    public List<Slot> getPendingResponseSlot() {
        return pendingResponseSlot;
    }


    public void setPendingResponseSlot(List<Slot> pendingresponseslot) {
        this.pendingResponseSlot = pendingresponseslot;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

