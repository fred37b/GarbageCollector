package com.valhatech.garbagecollector.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.data.Trash;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.function.PhpRequester;
import com.valhatech.garbagecollector.service.GPSService;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayTrashFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayTrashFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * https://www.simplifiedcoding.net/android-upload-image-using-php-mysql-android-studio/
 */
public class DisplayTrashFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "binder";

    // TODO: Rename and change types of parameters
    private GPSService.MyBinder mParam1;

    //--- Composant
    private String id ;
    // Constant for identifying the dialog
    private static final int DIALOG_ALERT = 10;
    private StringBuilder stringBuilder ;
    private GPSService gpsTracker ;

    //--- IHM
    private TextView  textViewDisplayTrashCategory ;
    private ImageView imageViewDisplayTrash ;
    private EditText  editTextDisplayTrashLatAndLong;
    private TextView  textViewDisplayTrashSince ;
    private ImageButton buttonDisplayTrashRemoved ;
    private ImageButton buttonDisplayTrashGmap ;
    private ImageButton buttonDisplayTrashCopy ;
    private ImageButton buttonDisplayTrashShare ;
    private AlertDialog.Builder builder ;
    private EditText  editTextDisplayTrashAddress ;
    private TextView  textViewDisplayTrashDistance;

    private Context context ;

    private OnFragmentInteractionListener mListener;

    public DisplayTrashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DisplayTrashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayTrashFragment newInstance(GPSService.MyBinder param1) {
        DisplayTrashFragment fragment = new DisplayTrashFragment();
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

        Bundle bundle = this.getArguments();
        this.id = bundle.getString(Config.KEY_TRASH_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_display_trash, container, false);

        imageViewDisplayTrash          = (ImageView) rootView.findViewById(R.id.imageViewDisplayTrash) ;
        editTextDisplayTrashLatAndLong = (EditText) rootView.findViewById(R.id.editTextDisplayTrashLatAndLong) ;
        editTextDisplayTrashAddress    = (EditText) rootView.findViewById(R.id.editTextDisplayTrashAddress) ;
        textViewDisplayTrashSince      = (TextView) rootView.findViewById(R.id.textViewDisplayTrashSince) ;
        textViewDisplayTrashDistance   = (TextView) rootView.findViewById(R.id.textViewDisplayTrashDisplayDistance);
        textViewDisplayTrashCategory   = (TextView) rootView.findViewById(R.id.textViewDisplayTrashCategory) ;
        buttonDisplayTrashRemoved      = (ImageButton) rootView.findViewById(R.id.buttonDisplayTrashRemoved);
        buttonDisplayTrashGmap         = (ImageButton) rootView.findViewById(R.id.buttonDisplayTrashGmap);
        buttonDisplayTrashCopy         = (ImageButton) rootView.findViewById(R.id.buttonDisplayTrashCopy);
        buttonDisplayTrashShare        = (ImageButton) rootView.findViewById(R.id.buttonDisplayTrashShare);

        Button  buttonDisplayTrashModify    = (Button) rootView.findViewById(R.id.buttonDisplayTrashModify);
        Spinner spinnerDisplayTrashCategory = (Spinner) rootView.findViewById(R.id.spinnerDisplayTrashCategory);

        context    = getContext();
        gpsTracker = mParam1.getService();

        Bundle bundle = this.getArguments();
        this.id = bundle.getString(Config.KEY_TRASH_ID);


        // on recupere la taille de la fenetre pour gerer la taille de l'image
        Point screenSize = Config.getScreenSize();
        imageViewDisplayTrash.setMinimumWidth(screenSize.x);
        imageViewDisplayTrash.setMinimumHeight(screenSize.y);

        Trash trash ;
        stringBuilder = new StringBuilder();

        /**
         * Il faut que cet appel prene en compte le spinner et le mette a jour lors du depart
         *
         * */
        PhpRequester.getOneTrashById(DisplayTrashFragment.this,
                                     gpsTracker.getLocation(),
                                     id,
                                     textViewDisplayTrashCategory,
                                     imageViewDisplayTrash,
                                     editTextDisplayTrashLatAndLong,
                                     textViewDisplayTrashSince,
                                     editTextDisplayTrashAddress,
                                     textViewDisplayTrashDistance,
                                     stringBuilder,
                                     buttonDisplayTrashRemoved);


        // --- Gestionnaire d'evenements ---------------------------------------------------------//
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.displayTrashAreYouSureButton);

        builder.setPositiveButton(R.string.displayTrashYesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PhpRequester.deleteOneTrashById(id);
                DisplayAllTrashFragment displayAllTrashFragment = new DisplayAllTrashFragment();
                Bundle bundle = new Bundle();
                bundle.putBinder("binder", mParam1);
                displayAllTrashFragment.setArguments(bundle);
                //---Replace the fragment by another
                final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.relativeLayout, displayAllTrashFragment, "NewFragmentTag");
                fragmentTransaction.commit();
            }
        });

        builder.setNegativeButton(R.string.displayTrashNoButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        buttonDisplayTrashRemoved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        buttonDisplayTrashGmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder gmapStringBuilder = new StringBuilder();
                gmapStringBuilder.append("geo:");
                gmapStringBuilder.append(stringBuilder.toString());
                gmapStringBuilder.append("?q=");
                gmapStringBuilder.append(stringBuilder.toString());

                Uri gmmIntentUri = Uri.parse(gmapStringBuilder.toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        imageViewDisplayTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageViewDisplayTrash.getDrawable());
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap", bitmap);
                FragmentTransaction fragmentTransaction = DisplayTrashFragment.this.getFragmentManager().beginTransaction();
                DisplayPicFragment displayPicFragment = new DisplayPicFragment();
                displayPicFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.relativeLayout, displayPicFragment);
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

        buttonDisplayTrashCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", editTextDisplayTrashAddress.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), getResources().getString(R.string.displayTrashToast), Toast.LENGTH_LONG).show();
            }
        });

        buttonDisplayTrashShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append(getResources().getString(R.string.objectAvailable));
                strBuilder.append("\n");
                strBuilder.append(editTextDisplayTrashAddress.getText());
                strBuilder.append("\n");
                strBuilder.append(getResources().getString(R.string.atGpsCoordinate));
                strBuilder.append("\n");
                strBuilder.append(editTextDisplayTrashLatAndLong.getText());

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageViewDisplayTrash.getDrawable());
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        bitmap,
                        "Title.jpeg",
                        null);

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, strBuilder.toString());
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shareWith)));

            }
        });


        if(Config.developperFlag == true){

            ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(getContext(),
                    android.R.layout.simple_list_item_1,
                    Config.getMyCategoriesForAddTrash());

        /*
         spinnerSort.setAdapter(dataAdapterForSorting);
        int indexNewestToOldest = dataAdapterForSorting.getPosition(getResources().getString(R.string.newestToOldest));
        spinnerSort.setSelection(indexNewestToOldest);
         */

            spinnerDisplayTrashCategory.setAdapter(dataAdapter);
            //String   categoryName = String.valueOf(textViewDisplayTrashCategory.getText());
            //Category category = Config.getCategoryForAddTrashByName(categoryName);
            //int      index = dataAdapter.getPosition(category) ;
            //spinnerDisplayTrashCategory.setSelection(index);


            textViewDisplayTrashCategory.setVisibility(View.GONE);
            spinnerDisplayTrashCategory.setVisibility(View.VISIBLE);
            buttonDisplayTrashModify.setVisibility(View.VISIBLE);

            /*
            TODO
            final Category category2 = (Category) spinnerDisplayTrashCategory.getSelectedItem();

            buttonDisplayTrashModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhpRequester.ModifyCategoryOfTrash(DisplayTrashFragment.this,category2,id);
                }
            });
            */
        }else{
            textViewDisplayTrashCategory.setVisibility(View.VISIBLE);
            spinnerDisplayTrashCategory.setVisibility(View.GONE);
            buttonDisplayTrashModify.setVisibility(View.GONE);
        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDisplayTrashFragmentInteraction(uri);
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
        void onDisplayTrashFragmentInteraction(Uri uri);
    }
}
