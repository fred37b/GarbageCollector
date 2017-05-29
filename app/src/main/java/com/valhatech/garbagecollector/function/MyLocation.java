package com.valhatech.garbagecollector.function;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.valhatech.garbagecollector.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fred on 29/04/2016.
 */
public class MyLocation {

    Timer timer1;
    LocationManager locationManager;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    private Location location;

    /**
     *
     * @param context
     * @param result
     * @return
     */
    public boolean getLocation(Context context, LocationResult result) {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //don't start listeners if no provider is enabled
            if (!gps_enabled && !network_enabled)
                return false;

            if (gps_enabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListenerGps);
            } else if (network_enabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 2, locationListenerNetwork);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }



    /**
     *
     */
    LocationListener locationListenerGps = new LocationListener() {

        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);

            try {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    /**
     *
     */
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);

            try {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerGps);
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    /**
     *
     */
    class GetLastLocation extends TimerTask {
        @Override
        public void run() {

            try {
                locationManager.removeUpdates(locationListenerGps);
                locationManager.removeUpdates(locationListenerNetwork);
            }catch (SecurityException e) {
                e.printStackTrace();
            }

            Location net_loc=null, gps_loc=null;

            try {
                if(gps_enabled)
                    gps_loc= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(network_enabled)
                    net_loc= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }catch (SecurityException e) {
                e.printStackTrace();
            }

            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
