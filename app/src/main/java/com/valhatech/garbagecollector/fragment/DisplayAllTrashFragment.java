package com.valhatech.garbagecollector.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.function.PhpRequester;
import com.valhatech.garbagecollector.service.GPSService;

import java.io.File;


/**
 * http://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayAllTrashFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayAllTrashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayAllTrashFragment extends Fragment implements LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "binder";

    // TODO: Rename and change types of parameters
    private GPSService.MyBinder mParam1;

    //---IHM
    private View               rootView;
    private Spinner            spinnerCat;
    private RecyclerView       recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton        buttonSort;

    //---Component
    private Uri        fileUri; // file url to store image/video
    private GPSService gpsTracker ;
    private boolean    sortBoolean ; // true = location, false = time

    private OnFragmentInteractionListener mListener;

    public DisplayAllTrashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayAllTrashFragment newInstance(GPSService.MyBinder param1) {
        DisplayAllTrashFragment fragment = new DisplayAllTrashFragment();
        Bundle args = new Bundle();
        args.putBinder(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (GPSService.MyBinder) getArguments().getBinder(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView    = inflater.inflate(R.layout.fragment_display_all_trash, container, false);
        recyclerView       = (RecyclerView) rootView.findViewById(R.id.listViewTrash);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        spinnerCat         = (Spinner) rootView.findViewById(R.id.buttonDisplayAllTrashSpinner);
        buttonSort         = (ImageButton) rootView.findViewById(R.id.spinnerDisplayAllTrashSort);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        gpsTracker = mParam1.getService();

        ArrayAdapter<Category> arrayAdapterCategory = new ArrayAdapter<Category>(getContext(),
                                                                                 android.R.layout.simple_list_item_1,
                                                                                 Config.getMyCategoriesForAllTrash());

        spinnerCat.setAdapter(arrayAdapterCategory);
        int indexCategoryAll = arrayAdapterCategory.getPosition(Config.getCategoryByNumber(0));
        spinnerCat.setSelection(indexCategoryAll);


        sortBoolean = true ;
        buttonSort.setBackgroundResource(R.drawable.ic_location_on_black_48dp);

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(sortBoolean == true){
                   buttonSort.setBackgroundResource(R.drawable.ic_access_time_black_48dp);
                   sortBoolean = false ;
               } else if (sortBoolean == false){
                   buttonSort.setBackgroundResource(R.drawable.ic_location_on_black_48dp);
                   sortBoolean = true ;
               }

                double latitude  = MyUtils.getLatitude(gpsTracker);
                double longitude = MyUtils.getLongitude(gpsTracker);

                PhpRequester.getAllTrashWhithSort(DisplayAllTrashFragment.this,
                        mParam1,
                        latitude,
                        longitude,
                        recyclerView,
                        sortBoolean,
                        (Category) spinnerCat.getSelectedItem());
            }
        });

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                double latitude  = MyUtils.getLatitude(gpsTracker);
                double longitude = MyUtils.getLongitude(gpsTracker);

                PhpRequester.getAllTrashWhithSort(DisplayAllTrashFragment.this,
                                                  mParam1,
                                                  latitude,
                                                  longitude,
                                                  recyclerView,
                                                  sortBoolean,
                                                  (Category) spinnerCat.getSelectedItem());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("onItemClick", "position :");
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = MyUtils.getOutputMediaFileUri(MyUtils.MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, Config.REQUEST_IMAGE_CAPTURE); // start the image capture Intent
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                double latitude  = MyUtils.getLatitude(gpsTracker);
                double longitude = MyUtils.getLongitude(gpsTracker);

                PhpRequester.getAllTrashWhithSort(DisplayAllTrashFragment.this,
                        mParam1,
                        latitude,
                        longitude,
                        recyclerView,
                        sortBoolean,
                        (Category) spinnerCat.getSelectedItem());

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == Config.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger  images
                options.inSampleSize = 8;
                final Bitmap bitmap2 = BitmapFactory.decodeFile(fileUri.getPath(),options);
                File file = new File(fileUri.getPath());
                String fileName = file.getName();

                AddTrashFragment addTrashFragment = new AddTrashFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap", bitmap2);
                bundle.putString("fileName", fileName);
                bundle.putBinder("binder", mParam1);
                addTrashFragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout,addTrashFragment).addToBackStack(null).commitAllowingStateLoss();
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDisplayAllTrashFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Config.setLastLatitude(location.getLatitude());
        Config.setLastLongitude(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDisplayAllTrashFragmentInteraction(Uri uri);
    }
}
