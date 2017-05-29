package com.valhatech.garbagecollector.function;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.Category;

import java.util.List;

/**
 * Created by fred on 13/04/2016.
 * http://stackoverflow.com/questions/9906464/sort-listview-with-array-adapter
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 *
 * ImageView imageViewItemTrash
 * TextView  idItemTrash
 * TextView  nameItemTrash
 * TextView  timeItemTrash
 * TextView  distanceItemTrash
 */
public class ConfigListAdapter extends ArrayAdapter<Category> {

    public ConfigListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ConfigListAdapter(Context context, int resource, List<Category> items) {
        super(context, resource, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        // Create custom TextView
        Category category = getItem(position);

        TextView textViewcategory = (TextView) super.getView(position, convertView, parent);
        textViewcategory.setText(MyUtils.Capitalize(category.getName()));

        // Add padding to the TextView, scaled to device
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int px = (int) (10 * scale + 0.5f);
        textViewcategory.setPadding(px, px, px, px);

        return textViewcategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Category category = getItem(position);
        //http://www.edureka.co/blog/custom-spinner-in-android
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View mySpinner = layoutInflater.inflate(R.layout.list_item_category, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.listItemCategory);

        main_text.setText(MyUtils.Capitalize(category.getName()));

        return mySpinner;

    }
}
