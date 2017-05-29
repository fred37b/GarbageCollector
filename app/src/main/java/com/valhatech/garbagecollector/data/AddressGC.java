package com.valhatech.garbagecollector.data;

/**
 * Created by fred on 15/03/2016.
 */
public class AddressGC {

    private String address ;
    private String city ;
    private String state ;
    private String country ;
    private String postalCode ;
    private String knownName ;

    public AddressGC(){}

    public AddressGC(String address, String city, String state, String country, String postalCode) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public AddressGC(String address, String city, String state, String country, String postalCode, String knownName) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.knownName = knownName;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder(); //not synchronized

        if(address != null)
            stringBuilder.append(address).append(" ");
        if(postalCode != null)
            stringBuilder.append(postalCode).append(" ");
        if(city != null)
            stringBuilder.append(city).append(" ");
        if(state != null)
            stringBuilder.append(state).append(" ");
        if(country != null)
            stringBuilder.append(country);

        return stringBuilder.toString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getKnownName() {
        return knownName;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }
}
