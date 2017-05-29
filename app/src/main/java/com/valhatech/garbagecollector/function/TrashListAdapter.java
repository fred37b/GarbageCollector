package com.valhatech.garbagecollector.function;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valhatech.garbagecollector.R;

import com.squareup.picasso.Picasso;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.data.Trash;
import com.valhatech.garbagecollector.fragment.DisplayTrashFragment;
import com.valhatech.garbagecollector.service.GPSService;

import java.text.DecimalFormat;
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
public class TrashListAdapter extends RecyclerView.Adapter<TrashListAdapter.TrashViewHolder> {

    private List<Trash> trashes;
    private Resources   resources;
    private FragmentManager     fragmentManager ;
    private Context             context ;
    private GPSService.MyBinder binder;

    public void setTrashList(List<Trash> items){
        trashes = items ;
    }

    public void setResources(Resources inResources){
        this.resources = inResources;
    }

    public GPSService.MyBinder getBinder() {
        return binder;
    }

    public void setBinder(GPSService.MyBinder binder) {
        this.binder = binder;
    }

    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setContext(Context context){this.context = context;}

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_trash,parent, false);
        return new TrashViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrashViewHolder holder, int position) {
        final Trash currentTrash = trashes.get(position);
        holder.display(currentTrash);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString(Config.KEY_TRASH_ID, String.valueOf(currentTrash.getId()));
                bundle.putBinder("binder",binder);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DisplayTrashFragment displayTrashFragment = new DisplayTrashFragment();
                displayTrashFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.relativeLayout, displayTrashFragment);
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trashes.size();
    }

    /**
     * Inner class pour le trash view holder
     */
    public class TrashViewHolder extends RecyclerView.ViewHolder {

        private final ImageView    imageViewItemTrash  ;
        private final TextView     textViewItemTrash   ;
        private final TextView     textViewElapsedTime ;
        private final TextView     textViewDistance    ;
        private final LinearLayout linearLayout ;

        private Trash currentTrash ;

        public TrashViewHolder(View itemView) {
            super(itemView);

            linearLayout        = (LinearLayout) itemView.findViewById(R.id.linearLayoutItemTrash);
            imageViewItemTrash  = (ImageView) itemView.findViewById(R.id.imageViewItemTrash);
            textViewItemTrash   = (TextView)  itemView.findViewById(R.id.catNameItemTrash);
            textViewElapsedTime = (TextView)  itemView.findViewById(R.id.elapsedTimeItemTrash);
            textViewDistance    = (TextView)  itemView.findViewById(R.id.distanceItemTrash);

        }

        /**
         * Allow to display a trash with information
         * @param trash the trash to display
         */
        public void display(Trash trash){
            this.currentTrash = trash ;

            if (trash != null) {

                if (imageViewItemTrash != null) {
                    Picasso.with(context).load(trash.getUrlImage()).into(imageViewItemTrash);
                }

                if (textViewItemTrash != null) {
                    textViewItemTrash.setText(MyUtils.Capitalize(trash.getCatName()));
                }

                if (textViewElapsedTime != null) {
                    StringBuilder strbuilder = new StringBuilder();
                    strbuilder.append(resources.getString(R.string.throwedSince));
                    strbuilder.append(trash.getTimeElapsed());
                    textViewElapsedTime.setText(strbuilder.toString());
                }

                if (textViewDistance != null) {

                    String unit ;
                    double distance = trash.getDistanceFromUser();
                    if(distance > 1000){
                        distance = distance / 1000 ;
                        unit = resources.getString(R.string.kilometers);
                    }
                    else{
                        unit = resources.getString(R.string.meters);
                    }

                    StringBuilder strbuilder = new StringBuilder();
                    strbuilder.append(resources.getString(R.string.toADistanceOf));

                    DecimalFormat decimalFormat = new DecimalFormat("0.##");
                    strbuilder.append(decimalFormat.format(distance));
                    strbuilder.append(" "+unit);
                    textViewDistance.setText(strbuilder.toString());
                }
            }
        }
    }
}
