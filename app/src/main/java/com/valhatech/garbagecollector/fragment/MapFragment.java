package com.valhatech.garbagecollector.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.data.Trash;
import com.valhatech.garbagecollector.function.ConfigListAdapter;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.function.PhpRequester;
import com.valhatech.garbagecollector.service.GPSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * https://www.simplifiedcoding.net/google-maps-distance-calculator-google-maps-api/
 * https://www.simplifiedcoding.net/android-google-maps-tutorial-google-maps-android-api/
 * http://stackoverflow.com/questions/35496493/getmapasync-in-fragment
 * http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * http://tutos-android-france.com/google-maps/
 * https://openclassrooms.com/courses/creez-des-applications-pour-android/la-localisation-et-les-cartes
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "binder";

    // TODO: Rename and change types of parameters
    private GPSService.MyBinder mParam1;

    private OnFragmentInteractionListener mListener;

    private View           view;
    private View           infoWindowView;
    private LayoutInflater inflater;
    private MapView        mapView;
    private GoogleMap      googleMap;
    private Spinner        spinnerCat ;
    private ImageView      imageView;

    //--- Composant
    private GPSService gpsTracker ;

    private Map<Marker, Trash> allMarkersMap = new HashMap<Marker, Trash>();

    //private ArrayList<Trash> trashes ;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(GPSService.MyBinder param1/*, String param2*/) {
        MapFragment fragment = new MapFragment();
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
        this.inflater = inflater ;
        view           = inflater.inflate(R.layout.fragment_map, container, false);
        spinnerCat     = (Spinner) view.findViewById(R.id.spinnerMapSort);
        // Inflate info view
        infoWindowView = inflater.inflate(R.layout.info_window, container, false);
        imageView      = (ImageView) infoWindowView.findViewById(R.id.iconInfoMap);

        gpsTracker = mParam1.getService();

        ConfigListAdapter arrayAdapterCategory = new ConfigListAdapter(getContext(),
                                                                       R.layout.list_item_category,
                                                                       Config.getMyCategoriesForAllTrash());

        spinnerCat.setAdapter(arrayAdapterCategory);
        int indexCategoryAll = arrayAdapterCategory.getPosition(Config.getCategoryByNumber(0));
        spinnerCat.setSelection(indexCategoryAll);

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //trashes = new ArrayList<Trash>();
                allMarkersMap.clear();

                double latitude  = MyUtils.getLatitude(gpsTracker);
                double longitude = MyUtils.getLongitude(gpsTracker);

                PhpRequester.getAllTrashForMap(MapFragment.this,
                                               (Category) spinnerCat.getSelectedItem(),
                                               googleMap,
                                               allMarkersMap,
                                               latitude,
                                               longitude);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("onItemClick", "position :");
            }
        });

        //trashes = new ArrayList<Trash>();

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMapFragmentInteraction(uri);
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Trash trash = allMarkersMap.get(marker);

                if(null != trash) {
                    Picasso.with(getContext()).load(trash.getUrlImage()).into(imageView);
                    infoWindowView.setVisibility(View.VISIBLE);
                }else {
                    infoWindowView.setVisibility(View.GONE);
                }

                return false;
            }
        });

        this.googleMap.setInfoWindowAdapter(this);
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Trash trash = allMarkersMap.get(marker);

                if(null != trash){
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.KEY_TRASH_ID, String.valueOf(trash.getId()));
                    bundle.putBinder("binder", mParam1);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    DisplayTrashFragment displayTrashFragment = new DisplayTrashFragment();
                    displayTrashFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.relativeLayout, displayTrashFragment);
                    fragmentTransaction.addToBackStack(null).commit();
                }
            }
        });

        double latitude  = MyUtils.getLatitude(gpsTracker);
        double longitude = MyUtils.getLongitude(gpsTracker);

        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(Config.GMAPS_ZOOM_LEVEL).build();
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Category myCat = (Category) spinnerCat.getSelectedItem();
        allMarkersMap.clear();
        PhpRequester.getAllTrashForMap(MapFragment.this,
                                       myCat,
                                       this.googleMap,
                                       allMarkersMap,
                                       latitude,
                                       longitude);

    }

    /*
    La première méthode (getInfoWindow()) vous permet de fournir une vue qui peut être utilisée
    pour l'intégralité de la fenêtre d'info.
    La seconde (getInfoContents()) vous permet uniquement de personnaliser le contenu de la fenêtre
    mais conserve le cadre et l'arrière-plan de la fenêtre d'info par défaut.
    */
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        Trash trash = allMarkersMap.get(marker);

        if(null != trash) {
            Picasso.with(getContext()).load(trash.getUrlImage()).into(imageView);
            infoWindowView.setVisibility(View.VISIBLE);
        }else{
            infoWindowView.setVisibility(View.GONE);
        }

        return infoWindowView;
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
        void onMapFragmentInteraction(Uri uri);
    }
}
