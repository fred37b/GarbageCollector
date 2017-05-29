package com.valhatech.garbagecollector.data;


import com.valhatech.garbagecollector.function.MyUtils;

import java.util.Calendar;

/**
 * Created by fred on 24/03/2016.
 */
public class Trash {

    private long     id ;
    private String   name ;
    private String   catName ;
    private int      catNumber ;
    private String   urlImage ;
    private long     foreignKeyCategory ;
    private String   dateAndTime ;
    private Calendar calendar ;
    private String   timeElapsed ;
    private double   latitude ;
    private double   longitude ;
    private double   distanceFromUser ;
    private String   address ;
    private String   town ;
    private long     foreignKeyCountry ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getForeignKeyCategory() {
        return foreignKeyCategory;
    }

    public void setForeignKeyCategory(long foreignKeyCategory) {
        this.foreignKeyCategory = foreignKeyCategory;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
        this.calendar = MyUtils.parseDateTime(dateAndTime);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public long getForeignKeyCountry() {
        return foreignKeyCountry;
    }

    public void setForeignKeyCountry(long foreignKeyCountry) {
        this.foreignKeyCountry = foreignKeyCountry;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(int catNumber) {
        this.catNumber = catNumber;
    }
}
