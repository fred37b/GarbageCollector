package com.valhatech.garbagecollector.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayPicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayPicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayPicFragment extends Fragment implements View.OnTouchListener {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "bitmap";

        // TODO: Rename and change types of parameters
        private Bitmap mParam1;

        private ImageView   imageView ;
        private FrameLayout frameLayout ;

        private static final String TAG = "Touch" ;
        // These matrices will be used to move and zoom image
        Matrix matrix = new Matrix();
        Matrix savedMatrix = new Matrix();
        PointF start = new  PointF();
        public static PointF mid = new PointF();
        // We can be in one of these 3 states
        public static final int NONE = 0;
        public static final int DRAG = 1;
        public static final int ZOOM = 2;
        public static int mode = NONE;
        float oldDist;

        int width ;
        int height ;

        private float[] matrixValues = new float[9];


        private OnFragmentInteractionListener mListener;

        public DisplayPicFragment() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment DisplayPicFragment.
         */
        // TODO: Rename and change types and number of parameters
    public static DisplayPicFragment newInstance(Bitmap param1) {
        DisplayPicFragment fragment = new DisplayPicFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_pic, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageViewDisplayPic);
        frameLayout = (FrameLayout) view.findViewById(R.id.frameLayoutDisplayPic);

        frameLayout.post(new Runnable() {

            @Override
            public void run() {
                width  = frameLayout.getWidth();
                height = frameLayout.getHeight() ;

                int currentBitmapWidth  = mParam1.getWidth();
                int currentBitmapHeight = mParam1.getHeight();

                int ivWidth = width;
                int ivHeight = height ;
                int newWidth = ivWidth;
                int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));
                Bitmap newbitMap = Bitmap.createScaledBitmap(mParam1, newWidth, newHeight, true);

                imageView.setImageBitmap(newbitMap);
                imageView.setOnTouchListener(DisplayPicFragment.this);

                int screen_width  = width ;
                int screen_height = height ;
                int image_width   = newbitMap.getWidth() ;
                int image_height  = newbitMap.getHeight() ;

                RectF drawableRect = new RectF(0, 0, image_width, image_height);
                RectF viewRect     = new RectF(0, 0, screen_width, screen_height);
                matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

                imageView.setImageMatrix(matrix);
            }

        });

        return view ;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {

                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {

                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }
                else if (mode == ZOOM) {

                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {

                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }



    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float result = (float) Math.sqrt(x * x + y * y);
        return result;
    }

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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
        void onFragmentInteraction(Uri uri);
    }
}
