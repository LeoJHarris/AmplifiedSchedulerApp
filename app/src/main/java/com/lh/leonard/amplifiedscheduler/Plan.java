package com.lh.leonard.amplifiedscheduler;

import com.backendless.geo.GeoPoint;

import java.util.Date;

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

}
