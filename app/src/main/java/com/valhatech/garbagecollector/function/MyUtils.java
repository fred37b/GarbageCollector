package com.valhatech.garbagecollector.function;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.AddressGC;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.service.GPSService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by fred on 03/03/2016.
 * 03/24/2016 Ajout de la fonction GetString pour l'upload d'image
 */
public class MyUtils {


    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "GarbColCam";

    //--- Travail sur les images -----------------------------------------------------------------//
    /**
     *
     * @param bitmap
     * @return
     */
    public static String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * http://stackoverflow.com/questions/25719620/how-to-solve-java-lang-outofmemoryerror-trouble-in-android
     * http://stackoverflow.com/questions/27115944/why-my-application-use-all-memory-and-get-outofmemoryerror-failed-to-allocate
     * http://stackoverflow.com/questions/32244851/androidjava-lang-outofmemoryerror-failed-to-allocate-a-23970828-byte-allocatio
     * http://stackoverflow.com/questions/11820266/android-bitmapfactory-decodestream-out-of-memory-with-a-400kb-file-with-2mb-f
     *
     * @param uri
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFromUri(Activity activity, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        parcelFileDescriptor.close();
        return image;
    }


    /**
     * http://stackoverflow.com/questions/15549421/how-to-download-and-save-an-image-in-android
     *
     * @param myUrl
     * @param imageView
     */
    public static void downloadPictureForDisplayTrash(final String myUrl, final ImageView imageView){

        class GetMyPic extends AsyncTask<Void,Void,Bitmap> {

            @Override
            protected Bitmap doInBackground(Void... params) {
                URL    url   = null;
                Bitmap image = null;

                try {
                    url = new URL(myUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return image;
            }

            @Override
            protected void onPostExecute(Bitmap loadedBitmap) {

                // Gets the width you want it to be
                int intendedWidth = imageView.getWidth();

                int originalWidth  = 45 ;
                int originalHeight = 45 ;

                if(null != loadedBitmap){
                    // Gets the downloaded image dimensions
                    originalWidth  = loadedBitmap.getWidth();
                    originalHeight = loadedBitmap.getHeight();
                }

                // Calculates the new dimensions
                float scale     = (float) intendedWidth / originalWidth;
                int   newHeight = (int) Math.round(originalHeight * scale);

                // Resizes mImageView. Change "FrameLayout" to whatever layout mImageView is located in.
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.getLayoutParams().width = intendedWidth;
                imageView.getLayoutParams().height = newHeight;
                imageView.setImageBitmap(loadedBitmap);
                imageView.refreshDrawableState();

            }
        }

        GetMyPic getJSON = new GetMyPic();
        getJSON.execute();
    }


    /**
     * Creating file uri to store image/video
     */
    public static Uri getOutputMediaFileUri(int type) {

        File file = getOutputMediaFile(type);

        return Uri.fromFile(file);
    }

    /**
     *
     * @param inFileName
     */
    public static void deletePicture(String inFileName){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);
        File mediaFile = new File(mediaStorageDir,inFileName);

        mediaFile.delete();
    }

    // Scale and maintain aspect ratio given a desired width
    // BitmapScaler.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    // Scale and maintain aspect ratio given a desired height
    // BitmapScaler.scaleToFitHeight(bitmap, 100);
    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    /**
     * returning image / video
     * @param type
     * @return
     */
    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        boolean isDirExist = mediaStorageDir.exists();
        // Create the storage directory if it does not exist
        if (isDirExist == false) {

            mediaStorageDir.mkdirs();
            isDirExist = mediaStorageDir.exists();
            if (isDirExist == false) {
                //Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "+ IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    //--- date -----------------------------------------------------------------------------------//
    /**
     * Permet de parser une date au format : 2016-03-01 11:18:37 en objet Calendar
     * @param dateTime date au format String avec la syntaxe "YYYY-MM-DD hh:mm:ss"
     * @return date au format Calendar
     */
    public static Calendar parseDateTime(String dateTime){
        //2016-03-01 11:18:37
        String[] dateAndTimes = dateTime.split(" ");
        String[] dates = dateAndTimes[0].split("-");
        String[] times = dateAndTimes[1].split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(times[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(times[1]));
        calendar.set(Calendar.SECOND, Integer.valueOf(times[2]));

        calendar.set(Calendar.YEAR, Integer.valueOf(dates[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[2]));

        return calendar;
    }

    /**
     * Return the date and time with the following format : 2016-03-16 03:15:29
     * @return
     */
    public static String getStringDateTime(){

        Calendar c = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        int year  = c.get(Calendar.YEAR);
        stringBuilder.append(String.valueOf(year)).append("-");
        int month = c.get(Calendar.MONTH);
        stringBuilder.append(String.valueOf(month)).append("-");
        int day   = c.get(Calendar.DAY_OF_MONTH);
        stringBuilder.append(String.valueOf(day)).append("-").append(" ");
        int hour   = c.get(Calendar.HOUR_OF_DAY);
        stringBuilder.append(String.valueOf(hour)).append(":");
        int minute = c.get(Calendar.MINUTE);
        stringBuilder.append(String.valueOf(minute)).append(":");
        int second = c.get(Calendar.SECOND);
        stringBuilder.append(String.valueOf(second));

        return stringBuilder.toString() ;
    }

    /**
     *
     * @param before
     * @param after
     * @param field
     * @return
     */
    private static int elapsed(Calendar before, Calendar after, int field) {
        Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
        int elapsed = -1;
        while (!clone.after(after)) {
            clone.add(field, 1);
            elapsed++;
        }
        return elapsed;
    }

    /**
     * Calcul le temps entre 2 dates et retourne une chaine decrivant cette duree
     * @param start
     * @param end
     * @return
     */
    public static String substractCalendar(Context context,Calendar start,Calendar end){

        Integer[] elapsed = new Integer[6];
        Calendar clone = (Calendar) start.clone(); // Otherwise changes are been reflected.
        elapsed[0] = elapsed(clone, end, Calendar.YEAR);
        clone.add(Calendar.YEAR, elapsed[0]);
        elapsed[1] = elapsed(clone, end, Calendar.MONTH);
        clone.add(Calendar.MONTH, elapsed[1]);
        elapsed[2] = elapsed(clone, end, Calendar.DATE);
        clone.add(Calendar.DATE, elapsed[2]);
        elapsed[3] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 3600000;
        clone.add(Calendar.HOUR, elapsed[3]);
        elapsed[4] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 60000;
        clone.add(Calendar.MINUTE, elapsed[4]);
        //elapsed[5] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 1000;

        StringBuilder stringBuilder = new StringBuilder();

        if(elapsed[0] > 0)
            stringBuilder.append(elapsed[0]).append(" ").append(context.getResources().getString(R.string.years)).append(" ");
        if(elapsed[1] > 0)
            stringBuilder.append(elapsed[1]).append(" ").append(context.getResources().getString(R.string.months)).append(" ");
        if(elapsed[2] > 0)
            stringBuilder.append(elapsed[2]).append(" ").append(context.getResources().getString(R.string.days)).append(" ");
        if(elapsed[3] > 0)
            stringBuilder.append(elapsed[3]).append(" ").append(context.getResources().getString(R.string.hours)).append(" ");
        if(elapsed[4] > 0)
            stringBuilder.append(elapsed[4]).append(" ").append(context.getResources().getString(R.string.minutes)).append(" ");
        if(stringBuilder.toString().isEmpty()){
            return context.getResources().getString(R.string.justNow) ;
        }else{
            return stringBuilder.toString() ;
        }
    }

    //--- GPS --------------------------------------------------------------------------------------
    /**
     * Permet de recuperer un criteria configurer pour selectionner le meilleur provider de
     * localisation
     * @return Criteria configurer
     */
    public static Criteria getCriteria(){
        Criteria criteria = new Criteria();
        // Pour indiquer la précision voulue
        // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // Est-ce que le fournisseur doit être capable de donner une altitude ?
        criteria.setAltitudeRequired(true);
        // Est-ce que le fournisseur doit être capable de donner une direction ?
        criteria.setBearingRequired(true);
        // Est-ce que le fournisseur peut être payant ?
        criteria.setCostAllowed(false);
        // Pour indiquer la consommation d'énergie demandée
        // Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        // Est-ce que le fournisseur doit être capable de donner une vitesse ?
        criteria.setSpeedRequired(true);

        return criteria ;
    }

    /**
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    public static AddressGC getAdressFromLatitudeAndLongitude(Context context,double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;


        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }catch (IOException e){
            //todo
            e.printStackTrace();
        }

        if(null == addresses.get(0)){
            Log.i("My.getAdressFromLatitud", "getAdressFromLatitudeAndLongitude");
        }

        AddressGC address = new AddressGC();
        address.setAddress(addresses.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        address.setCity(addresses.get(0).getLocality());
        address.setState(addresses.get(0).getAdminArea());
        address.setCountry(addresses.get(0).getCountryName());
        address.setPostalCode(addresses.get(0).getPostalCode());
        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return address;
    }

    /**
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double DistanceTo2(double lat1, double lon1, double lat2, double lon2){

        double EARTH_RADIUS = 6378137;

        double rlat1 = Math.toRadians(lat1);
        double rlat2 = Math.toRadians(lat2);
        double rlon1 = Math.toRadians(lon1);
        double rlon2 = Math.toRadians(lon2);

        double deltaLongitude = (rlon2 - rlon1) / 2;
        double deltaLatitude  = (rlat2 - rlat1) / 2;

        double a = (Math.sin(deltaLatitude) * Math.sin(deltaLatitude)) + Math.cos(rlat1) * Math.cos(rlat2) * (Math.sin(deltaLongitude) * Math.sin(deltaLongitude));
        double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(EARTH_RADIUS * d);
    }

    /**
     * Recupere la latitude, au travers du service GPS ou si il n'est pas disponible dans la
     * derniere position enregistre
     * @param gpsTracker service du gps
     * @return latitude
     */
    public static double getLatitude(GPSService gpsTracker){

        double mlatitude = 0.0 ;

        if (null != gpsTracker.getLocation()) {
            mlatitude  = gpsTracker.getLatitude();
            Config.setLastLatitude(mlatitude);
        } else {
            mlatitude  = Config.getLastLatitude();
        }

        return mlatitude ;
    }

    /**
     * Recupere la latitude, au travers du service GPS ou si il n'est pas disponible dans la
     * derniere position enregistre
     * @param gpsTracker service du gps
     * @return longitude
     */
    public static double getLongitude(GPSService gpsTracker){

        double mlongitude = 0.0 ;

        if (null != gpsTracker.getLocation()) {
            mlongitude = gpsTracker.getLongitude();
            Config.setLastLongitude(mlongitude);
        } else {
            mlongitude = Config.getLastLatitude();
        }

        return mlongitude ;
    }


    //--- Travail sur les string -----------------------------------------------------------------//

    /**
     * met majuscule la premiere lettre d'un mot
     * @param inString
     * @return
     */
    public static String Capitalize(String inString){

        char[] char_table = inString.toCharArray();
        char_table[0]=Character.toUpperCase(char_table[0]);

        return new String(char_table);
    }
}
