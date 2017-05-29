package com.valhatech.garbagecollector.data;

import java.io.Serializable;

/**
 * Created by fred on 18/03/2016.
 * KEY_CAT_ID
 * KEY_CAT_FK_COUNTRY
 * KEY_CAT_NAME
 * KEY_CAT_NUMBER
 */
public class Category implements Serializable {

    private String name ;
    private int number ;

    public Category(){

    }

    public Category(String name, int number){
        this.name   = name ;
        this.number = number ;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return name;
    }
}
