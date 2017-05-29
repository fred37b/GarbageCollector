package com.valhatech.garbagecollector.data;


import java.util.Comparator;

/**
 * Created by fred on 15/04/2016.
 */
public class TrashComparatorDistanceNearestToDistant implements Comparator<Trash> {
    @Override
    public int compare(Trash trash1, Trash trash2) {

        return Double.compare(trash1.getDistanceFromUser(),trash2.getDistanceFromUser());
    }
}
