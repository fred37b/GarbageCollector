package com.valhatech.garbagecollector.data;

/**
 * Created by fred on 26/04/2016.
 */
public class Town {

    private String id ;
    private String foreignKeyCountry ;
    private String postalCode ;
    private String name ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForeignKeyCountry() {
        return foreignKeyCountry;
    }

    public void setForeignKeyCountry(String foreignKeyCountry) {
        this.foreignKeyCountry = foreignKeyCountry;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
