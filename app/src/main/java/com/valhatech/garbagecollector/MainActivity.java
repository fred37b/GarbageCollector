package com.valhatech.garbagecollector;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.fragment.AddTrashFragment;
import com.valhatech.garbagecollector.fragment.DeclarationFragment;
import com.valhatech.garbagecollector.fragment.DisplayAllTrashFragment;
import com.valhatech.garbagecollector.fragment.DisplayPicFragment;
import com.valhatech.garbagecollector.fragment.DisplayTrashFragment;
import com.valhatech.garbagecollector.fragment.MapFragment;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.service.GPSService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * http://tutos-android-france.com/listview-afficher-une-liste-delements/
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AddTrashFragment.OnFragmentInteractionListener,
                   DisplayAllTrashFragment.OnFragmentInteractionListener,
                   DisplayTrashFragment.OnFragmentInteractionListener,
                   DeclarationFragment.OnFragmentInteractionListener,
                   MapFragment.OnFragmentInteractionListener,
                   DisplayPicFragment.OnFragmentInteractionListener {



    //---Composant
    private ArrayList<HashMap<String, String>> lists ;
    private MyUtils myUtils ;
    private Point size ;
    private Location mLocation ;
    //private DisplayAllTrashFragment displayAllTrashFragment ;
    private DrawerLayout drawer;
    private FragmentManager fragmentManager;
    private Uri fileUri; // file url to store image/video

    // Retient l'état de la connexion avec le service
    private boolean bound = false;
    // Le service en lui-même
    GPSService gpsTracker ;
    GPSService.MyBinder binder ;
    boolean firstRun = true ;

    //---IHM
    private ImageView mImageView ;
    private RelativeLayout relativeLayout ;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lists = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("alltrashes");

        double defaultLatitude  = (double) getIntent().getSerializableExtra("defaultLatitude");
        double defaultLongitude = (double) getIntent().getSerializableExtra("defaultLongitude");

        Config.setLastLatitude(defaultLatitude);
        Config.setLastLongitude(defaultLongitude);

        // On recupere les categories et on cree les 2 list de categorie, pour la all trash display et le add trash display
        Config.InitCategoryList(getResources());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Config.setScreenSize(size);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gestion de la barre
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mImageView = (ImageView) findViewById(R.id.imageView3);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // test si les gps est active
        if(null != gpsTracker)
            gpsTracker.getLocation(this);

    }


    /**
     * http://stackoverflow.com/questions/7992216/android-fragment-handle-back-button-press
     * http://stackoverflow.com/questions/5448653/how-to-implement-onbackpressed-in-android-fragments
     */
    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult()", "start");

        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == Config.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger  images
                options.inSampleSize = 2;
                final Bitmap bitmap2 = BitmapFactory.decodeFile(fileUri.getPath(),options);
                File file = new File(fileUri.getPath());
                String fileName = file.getName();

                AddTrashFragment addTrashFragment = new AddTrashFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap", bitmap2);
                bundle.putString("fileName",fileName);
                bundle.putBinder("binder", binder);
                addTrashFragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout,addTrashFragment).addToBackStack(null).commitAllowingStateLoss();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            }else if (requestCode == Config.REQUEST_OPEN_TRASH_LIST && resultCode == RESULT_OK) {

            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.i("onActivityResult()", "end");
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_return:
                //this.getFragmentManager().popBackStack();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }

                return true;
            /*
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //getFragmentManager().popBackStack();
                this.getFragmentManager().popBackStack();
                return true;
                */
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = MyUtils.getOutputMediaFileUri(MyUtils.MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, Config.REQUEST_IMAGE_CAPTURE);

        } else if (id == R.id.nav_gallery) {
            DisplayAllTrashFragment displayAllTrashFragment1 = new DisplayAllTrashFragment();
            Bundle bundle = new Bundle();
            bundle.putBinder("binder",binder);
            displayAllTrashFragment1.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.relativeLayout, displayAllTrashFragment1).commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_map) {
            MapFragment mapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putBinder("binder",binder);
            mapFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.relativeLayout,mapFragment).addToBackStack(null).commitAllowingStateLoss();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_mail) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{getString(R.string.mail)} );
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjectMail));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.subjectMail));
            try {
                startActivity(Intent.createChooser(intent, getString(R.string.sendMail)));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, getString(R.string.noClientMail), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_declaration) {
            DeclarationFragment declarationFragment = new DeclarationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.relativeLayout,declarationFragment).addToBackStack(null).commitAllowingStateLoss();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, GPSService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO arreter le GPS
        // Unbind from the service
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }

    /**
     *
     */
    private void startDisplayAllTrash(){
        DisplayAllTrashFragment displayAllTrashFragment = new DisplayAllTrashFragment();
        Bundle bundle = new Bundle();
        bundle.putBinder("binder", binder);
        displayAllTrashFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.relativeLayout,displayAllTrashFragment).commitAllowingStateLoss();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder     = (GPSService.MyBinder) service;
            gpsTracker = binder.getService();
            bound      = true;

            if(true == firstRun){
                startDisplayAllTrash();
                firstRun = false ;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDisplayTrashFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAddTrashFormFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDisplayAllTrashFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDeclarationFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}