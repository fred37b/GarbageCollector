package com.valhatech.garbagecollector.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.valhatech.garbagecollector.MainActivity;
import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.AddressGC;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.function.PhpRequester;
import com.valhatech.garbagecollector.service.GPSService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTrashFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddTrashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTrashFragment extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "bitmap";
    private static final String ARG_PARAM2 = "fileName";
    private static final String ARG_PARAM3 = "binder";

    // TODO: Rename and change types of parameters
    private Bitmap              mParamBitmap;
    private String              mParamFileName;
    private GPSService.MyBinder mParamBinder;

    //---Composant
    private Location location;
    private File file;
    private ArrayList<HashMap<String, String>> categories;
    private Bitmap thumbnail = null;
    private GPSService gpsTracker ;
    private int finalHeight ;
    private int finalWidth  ;

    //---IHM
    private View rootView;
    private ImageView imageView;
    private EditText editTextAddress;
    private Button formButtonCancel;
    private ImageButton buttonGpsRefresh;
    private ImageButton buttonRotatePic;
    private Button formButtonSave;
    private Spinner spinnerCat;
    private Uri uriSelectedImage;



    private OnFragmentInteractionListener mListener;

    public AddTrashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTrashFragment newInstance(Bitmap param1, String param2, GPSService.MyBinder param3) {
        AddTrashFragment fragment = new AddTrashFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBinder(ARG_PARAM3, param3);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamBitmap   = getArguments().getParcelable(ARG_PARAM1);
            mParamFileName = getArguments().getString(ARG_PARAM2);
            mParamBinder   = (GPSService.MyBinder) getArguments().getBinder(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_trash, container, false);

        imageView             = (ImageView) rootView.findViewById(R.id.imageViewForm);
        editTextAddress       = (EditText) rootView.findViewById(R.id.editTextAddress);
        formButtonCancel      = (Button) rootView.findViewById(R.id.formButtonCancel);
        buttonGpsRefresh      = (ImageButton) rootView.findViewById(R.id.addTrashButtonGpsRefresh);
        buttonRotatePic       = (ImageButton) rootView.findViewById(R.id.addTrashButtonRotatePic);
        formButtonSave        = (Button) rootView.findViewById(R.id.formButtonSave);
        spinnerCat            = (Spinner) rootView.findViewById(R.id.spinnerCategory);

        gpsTracker = mParamBinder.getService();

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(getContext(),
                android.R.layout.simple_list_item_1,
                Config.getMyCategoriesForAddTrash());

        spinnerCat.setAdapter(dataAdapter);

        Point screenSize = Config.getScreenSize();
        int intendedWidth = screenSize.x;
        int originalWidth  = mParamBitmap.getWidth();
        int originalHeight = mParamBitmap.getHeight();

        // Calculates the new dimensions
        float scale   = (float) intendedWidth / originalWidth;
        int newHeight = (int) Math.round(originalHeight * scale);

        int height = imageView.getHeight();
        int width  = imageView.getWidth();

        imageView.setImageBitmap(mParamBitmap);

        double latitude  = MyUtils.getLatitude(gpsTracker);
        double longitude = MyUtils.getLongitude(gpsTracker);

        final AddressGC address = MyUtils.getAdressFromLatitudeAndLongitude(this.getContext(), latitude, longitude);

        editTextAddress.setText(address.toString());

        formButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO il y a un truc a refaire la
                file = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                Uri fileUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, Config.REQUEST_IMAGE_CAPTURE);
            }
        });

        buttonRotatePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int width     = mParamBitmap.getWidth();
                int height    = mParamBitmap.getHeight();
                int newWidth  = imageView.getWidth();
                int newHeight = imageView.getHeight();

                // calculate the scale - in this case = 0.4f
                float scaleWidth  ;
                float scaleHeight ;
                float myHeight ;

               if(width < height){
                    scaleWidth  = ((float) newWidth) / width;
                    scaleHeight = ((float) newHeight) / height;
                   myHeight = newHeight ;
                }else{
                    scaleWidth  = ((float) newWidth) / width;
                    myHeight = newWidth * height / width;
                    scaleHeight = ((float) myHeight) / height;
                }

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                matrix.postRotate(90);

                /*Bitmap resizedBitmap*/
                mParamBitmap = Bitmap.createBitmap(mParamBitmap, 0, 0, width, height, matrix, true);
                imageView.setImageBitmap(mParamBitmap);

            }
        });

        buttonGpsRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    double latitude  = MyUtils.getLatitude(gpsTracker);
                    double longitude = MyUtils.getLongitude(gpsTracker);
                    final AddressGC address = MyUtils.getAdressFromLatitudeAndLongitude(getContext(), latitude, longitude);
                    editTextAddress.setText(address.toString());

                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }
        });

        formButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean errorFlag = false;
                StringBuilder strBuilder = new StringBuilder();

                Category category = (Category) spinnerCat.getSelectedItem();
                String catNumber = String.valueOf(category.getNumber());

                double mlatitude  = MyUtils.getLatitude(gpsTracker);
                double mlongitude = MyUtils.getLongitude(gpsTracker);

                if (editTextAddress.getText().length() == 0) {
                    strBuilder.append(R.string.form_errorAddress).append("\n");
                    errorFlag = true;
                }

                if (errorFlag == true) {
                    Toast.makeText(rootView.getContext(),
                                   strBuilder.toString(),
                                   Toast.LENGTH_LONG).show();
                } else {

                    PhpRequester.addTrash(getActivity(),
                            mParamBitmap,
                            mParamFileName,
                            catNumber,//Category
                            MyUtils.getStringDateTime(),//Date time
                            mlatitude, // latitude
                            mlongitude, // longitude
                            address, // country
                            "1"); //user

                    //---Replace the fragment by another
                    // TODO il y a peut etre un bug d'affichage ici
                    // TODO on a peut être 2 fragment supperpose suite a ce code
                    // Il faudrait peut etre retourne sur l'activity et demarrer le display all
                    final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    DisplayAllTrashFragment displayAllTrashFragment = new DisplayAllTrashFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBinder("binder", mParamBinder);
                    displayAllTrashFragment.setArguments(bundle);
                    //displayAllTrashFragment.setLocation(location);
                    fragmentTransaction.replace(R.id.relativeLayout, displayAllTrashFragment, "NewFragmentTag");
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        imageView.setImageBitmap(mParamBitmap);
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddTrashFormFragmentInteraction(uri);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == Config.REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.RESULT_OK) {

                if(data != null){
                    Uri selectedImage = data.getData();

                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        mParamBitmap = BitmapFactory.decodeFile(selectedImage.getPath(),options);

                        imageView.setImageBitmap(mParamBitmap);

                        double latitude  = MyUtils.getLatitude(gpsTracker);
                        double longitude = MyUtils.getLongitude(gpsTracker);

                        AddressGC address = MyUtils.getAdressFromLatitudeAndLongitude(this.getContext(), latitude, longitude);
                        editTextAddress.setText(address.toString());

                    }catch (SecurityException e){
                        e.printStackTrace();
                    }
                }

            }else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
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

    public void setCategories(ArrayList<HashMap<String, String>> categories) {
        this.categories = categories;
    }
    public void setUriSelectedImage(Uri uriSelectedImage) {
        this.uriSelectedImage = uriSelectedImage;
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
        void onAddTrashFormFragmentInteraction(Uri uri);
    }
}
