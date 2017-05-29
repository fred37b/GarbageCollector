package com.valhatech.garbagecollector.function;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.HashMap;
import java.util.List;

/**
 * Created by fred on 15/04/2016.
 */
public class AdapterCategory extends ArrayAdapter<HashMap<String, String>> {

    private List<HashMap<String, String>> categories ;

    public AdapterCategory(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public AdapterCategory(Context context, int resource, List<HashMap<String, String>> items) {
        super(context, resource, items);
        this.categories = items ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> category = categories.get(position);

        return super.getView(position, convertView, parent);
    }
}
