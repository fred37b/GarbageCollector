package com.valhatech.garbagecollector.data;


import java.util.Comparator;

/**
 * Created by fred on 15/04/2016.
 */
public class TrashComparatorDistanceDistantToNearest implements Comparator<Trash> {
    @Override
    public int compare(Trash trash1, Trash trash2) {

        return Double.compare(trash2.getDistanceFromUser(),trash1.getDistanceFromUser());
    }
}
