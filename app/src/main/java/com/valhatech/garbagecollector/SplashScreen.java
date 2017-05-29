package com.valhatech.garbagecollector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.valhatech.garbagecollector.data.AddressGC;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.data.Country;
import com.valhatech.garbagecollector.data.Town;
import com.valhatech.garbagecollector.function.MyUtils;
import com.valhatech.garbagecollector.function.RequestHandler;
import com.valhatech.garbagecollector.graphical.PlayGifView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Splash screen permettant de recuperer des informations durant le lancement de l'application
 */
public class SplashScreen extends Activity {

    private Intent intent;
    private LocationManager locationManager;
    private String bestProvider;

    private boolean flag;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private ArrayList<HashMap<String, String>> trashes;
    //private ArrayList<Category> categories;

    private double defaultLatitude ;
    private double defaultLongitude ;
    private String countryName ;
    private String townName ;
    private String postalCode ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PlayGifView playGifView = (PlayGifView) findViewById(R.id.imgLogo);
        playGifView.setMinimumWidth(120);
        playGifView.setMinimumHeight(120);
        playGifView.setImageResource(R.drawable.ic_loading_48dp);

        if(true == checkAndRequestPermissions()){
            PrefetchGpsAndNetwork prefetchGpsAndNetwork = new PrefetchGpsAndNetwork(this);
            prefetchGpsAndNetwork.execute();
        }else{
            finish();
        }
    }

    /**
     *
     * @return
     */
    private  boolean checkAndRequestPermissions() {

        int cameraPermission   = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int storagePermission  = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED    ) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //Log.d("TAG", "Some permissions are not granted ask again ");
                        // permission is denied (this is the first time, when "never ask again" is not checked)
                        // so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK(getString(R.string.permissions),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, getString(R.string.goToSettings), Toast.LENGTH_LONG).show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(true == checkAndRequestPermissions()){
            PrefetchGpsAndNetwork prefetchGpsAndNetwork = new PrefetchGpsAndNetwork(this);
            prefetchGpsAndNetwork.execute();
        }else{
            finish();
        }
    }

    /**
     *
     * @param message
     * @param okListener
     */
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    /**
     *
     */
    private class PrefetchGpsAndNetwork extends AsyncTask<Void, Void, Void> implements LocationListener {

        private Context contextAsync;

        protected LocationManager locationManager; // Declaring a Location Manager
        boolean isGPSEnabled = false;        // flag for GPS status
        boolean isGpsNetworkEnabled = false; // flag for network status
        boolean isNetworkEnabled = false;    // flag for network
        boolean canGetLocation = false;      // flag for GPS status
        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

        Location location; // location

        /**
         * Constructeur
         * @param context
         */
        public PrefetchGpsAndNetwork(Context context) {
            this.contextAsync = context;
        }

        //Dialog progress;
        double latitude ;
        double longitude ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                locationManager = (LocationManager) contextAsync.getSystemService(LOCATION_SERVICE);
                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // getting network status
                isGpsNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo         info                = connectivityManager.getActiveNetworkInfo();

                if (info.getType() == ConnectivityManager.TYPE_WIFI){
                    isNetworkEnabled = true; }
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isNetworkEnabled = true; }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            boolean flagExecute = true ;

            if (!isGPSEnabled && !isGpsNetworkEnabled) {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(contextAsync);
                localBuilder.setMessage(R.string.gpsActivation);
                localBuilder.setCancelable(false);
                localBuilder.setPositiveButton(R.string.activateGPS,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            }
                        }
                );

                localBuilder.setNegativeButton(R.string.notActivateGPS,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                paramDialogInterface.cancel();
                                finish();
                            }
                        }
                );
                flagExecute = false ;
                localBuilder.create().show();
            }

            if(!isNetworkEnabled){
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(contextAsync);

                localBuilder.setTitle(R.string.title_activity_permission_internet);
                localBuilder.setMessage(R.string.internetActivation);
                localBuilder.setCancelable(false);
                localBuilder.setPositiveButton(R.string.internetActOk, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                finish();
                            }
                        }
                );
                flagExecute = false ;
                localBuilder.create().show();
            }

            if(true == flagExecute) {

                try {
                    this.canGetLocation = true;

                    // First get location from Network Provider
                    if (isGpsNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this);

                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onLocationChanged(Location location) {

            latitude  = location.getLatitude();
            longitude = location.getLongitude();

            PrefetchData prefetchData = new PrefetchData(latitude,longitude);
            prefetchData.execute();

            try {
                locationManager.removeUpdates(this);
            }catch (SecurityException e){
                e.printStackTrace();
            }
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
    }

    /**
     * Permet de recuperer des listes de categories et de dechets, l'appel de l'activite principal
     * se fait a la fin de cette fonction.
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        private double latitude ;
        private double longitude ;

        public PrefetchData(double inLatitude, double inLongitude) {
            this.latitude  = inLatitude ;
            this.longitude = inLongitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            RequestHandler requestHandler = new RequestHandler();

            AddressGC addressGC  = MyUtils.getAdressFromLatitudeAndLongitude(getApplicationContext(),
                    latitude,
                    longitude);

            defaultLatitude  = latitude ;
            defaultLongitude = longitude ;
            countryName = addressGC.getCountry();
            townName    = addressGC.getCity();
            postalCode  = addressGC.getPostalCode() ;

            HashMap<String,String> townParams = new HashMap<>();
            townParams.put(Config.KEY_COUNTRY_NAME, countryName);
            townParams.put(Config.KEY_TOWN_NAME, townName);
            townParams.put(Config.KEY_TOWN_POSTALCODE, postalCode);

            RequestHandler requestHandler2 = new RequestHandler();
            String resultCountryAndTown = requestHandler2.sendPostRequest(Config.URL_GET_TOWN, townParams);

            Town    town    = new Town();
            Country country = new Country();

            try {
                //---Category
                JSONObject jsonObjectCat     = new JSONObject(resultCountryAndTown);
                JSONArray jsonArrayResultCat = jsonObjectCat.getJSONArray(com.valhatech.garbagecollector.data.Config.TAG_JSON_ARRAY);

                for(int i = 0; i < jsonArrayResultCat.length(); i++){
                    JSONObject jo = jsonArrayResultCat.getJSONObject(i);

                    town.setId(jo.getString(Config.KEY_TOWN_ID));
                    town.setName(jo.getString(Config.KEY_TOWN_NAME));
                    town.setPostalCode(jo.getString(Config.KEY_TOWN_POSTALCODE));
                    town.setForeignKeyCountry(jo.getString(Config.KEY_TOWN_COUNTRY));

                    country.setId(jo.getString(Config.KEY_COUNTRY_ID));
                    country.setName(jo.getString(Config.KEY_COUNTRY_NAME));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Config.setCountry(country);
            String resultAllTrash = requestHandler.sendGetRequest(Config.URL_GET_ALL);
            trashes = new ArrayList<HashMap<String, String>>();

            try {

                //---All trash
                JSONObject jsonObjectAllTrash      = new JSONObject(resultAllTrash);
                JSONArray  JSONArrayResultAllTrash = jsonObjectAllTrash.getJSONArray(Config.TAG_JSON_ARRAY);

                for(int i = 0; i < JSONArrayResultAllTrash.length(); i++){
                    JSONObject jo = JSONArrayResultAllTrash.getJSONObject(i);
                    String id      = jo.getString(Config.KEY_TRASH_ID);
                    String catName = jo.getString(Config.KEY_CAT_NAME);

                    HashMap<String,String> trash = new HashMap<>();
                    trash.put(Config.KEY_TRASH_ID,id);
                    trash.put(Config.KEY_CAT_NAME,catName);
                    trashes.add(trash);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.putExtra("alltrashes", trashes);
            intent.putExtra("defaultLatitude", defaultLatitude);
            intent.putExtra("defaultLongitude", defaultLongitude );
            intent.putExtra("country", countryName);
            intent.putExtra("town", townName);
            intent.putExtra("postalCode", postalCode);

            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();

        }
    }
}
