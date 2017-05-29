package com.valhatech.garbagecollector.function;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.valhatech.garbagecollector.R;
import com.valhatech.garbagecollector.data.AddressGC;
import com.valhatech.garbagecollector.data.Category;
import com.valhatech.garbagecollector.data.Config;
import com.valhatech.garbagecollector.data.Trash;
import com.valhatech.garbagecollector.data.TrashComparatorDistanceDistantToNearest;
import com.valhatech.garbagecollector.data.TrashComparatorDistanceNearestToDistant;
import com.valhatech.garbagecollector.data.TrashComparatorTimeNewestToOldest;
import com.valhatech.garbagecollector.data.TrashComparatorTimeOldestToNewest;
import com.valhatech.garbagecollector.service.GPSService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by fred on 15/03/2016.
 */
public class PhpRequester {

    //---Constant
    private static String JSON_STRING;


    /**
     * http://www.androidhive.info/2014/12/android-uploading-camera-image-video-to-server-with-progress-bar/
     * @param activity
     * @param inImg
     * @param inCategory
     * @param inDate_time
     * @param inLatitude
     * @param inLongitude
     * @param inAddress
     * @param inUser
     */
    public static void addTrash(final Activity activity,
                                final Bitmap inImg,
                                final String inFileName,
                                String inCategory,
                                String inDate_time,
                                String inLatitude,
                                String inLongitude,
                                AddressGC inAddress,
                                String inUser){

        final String category   = inCategory.trim();
        final String date_time  = inDate_time.trim();
        final String latitude   = inLatitude.trim();
        final String longitude  = inLongitude.trim();
        final String address    = inAddress.getAddress().trim();
        final String city       = inAddress.getCity().trim();
        final String postalCode = inAddress.getPostalCode();
        final String country    = inAddress.getCountry().trim();
        final String user       = inUser.trim();

        /**
         * AsyncTask<doInBackground,Void,String>
         */
        class AddTrash extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activity,"Adding...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = MyUtils.getStringImage(bitmap);

                HashMap<String,String> trashParams = new HashMap<>();
                trashParams.put(Config.KEY_TRASH_CATEGORY, category);
                trashParams.put(Config.KEY_TRASH_DATETIME, date_time);
                trashParams.put(Config.KEY_TRASH_LATITUDE, latitude);
                trashParams.put(Config.KEY_TRASH_LONGITUDE, longitude);
                trashParams.put(Config.KEY_TRASH_ADDRESS, address);
                trashParams.put(Config.KEY_TRASH_TOWN, city);
                trashParams.put(Config.KEY_TRASH_POSTAL_CODE, postalCode);
                trashParams.put(Config.KEY_TRASH_COUNTRY, country);
                trashParams.put(Config.KEY_TRASH_USER, user);
                trashParams.put(Config.UPLOAD_IMG_NAME, inFileName);
                trashParams.put(Config.UPLOAD_IMG_KEY, uploadImage);

                RequestHandler requestHandler = new RequestHandler();
                String result = requestHandler.sendPostRequest(Config.URL_ADD, trashParams);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                MyUtils.deletePicture(inFileName);

                loading.dismiss();

                String sucessStr =  new String("Successfully Uploaded");
                String errorStr =  new String("Error");

                if(sucessStr.equals(s)){
                    Toast.makeText(activity, activity.getResources().getString(R.string.sucessStr), Toast.LENGTH_LONG).show();
                }else if(errorStr.equals(s)){
                    Toast.makeText(activity, activity.getResources().getString(R.string.errorStr), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, activity.getResources().getString(R.string.errorStr), Toast.LENGTH_LONG).show();
                }
            }
        }

        AddTrash addTrash = new AddTrash();
        addTrash.execute(inImg);
    }

    /**
     * Permet d'ajouter une ordure sur le serveur
     * @param activity
     * @param inImg
     * @param inCategory
     * @param inDate_time
     * @param inLatitude
     * @param inLongitude
     * @param inAddress
     * @param inUser
     */
    public static void addTrash(final Activity activity,
                                Bitmap inImg,
                                String inFileName,
                                String inCategory,
                                String inDate_time,
                                double inLatitude,
                                double inLongitude,
                                AddressGC inAddress,
                                String inUser){

        String strLatitude = String.valueOf(inLatitude);
        String strLongitude = String.valueOf(inLongitude);

        addTrash(activity, inImg, inFileName, inCategory, inDate_time, strLatitude, strLongitude,
                inAddress, inUser);
    }

    /**
     *
     */
    public static void ModifyCategoryOfTrash(final Fragment fragment,
                                             final Category category,
                                             final String   idTrash){

        // todo a renommer
        class ModifyTrash extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                String fetchingData = fragment.getResources().getString(R.string.fetchingData);
                String wait         = fragment.getResources().getString(R.string.wait);
                loading = ProgressDialog.show(fragment.getActivity(), fetchingData, wait, false, false);
            }

            @Override
            protected String doInBackground(Void... params) {

                HashMap<String,String> trashParams = new HashMap<>();
                trashParams.put(Config.KEY_TRASH_ID, idTrash);
                trashParams.put(Config.KEY_CAT_NUMBER, String.valueOf(category.getNumber()));

                RequestHandler requestHandler = new RequestHandler();
                String result = requestHandler.sendPostRequest(Config.URL_ADD, trashParams);

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();

                String sucessStr =  new String("Successfully Uploaded");
                String errorStr  =  new String("Error");

                if(sucessStr.equals(s)){
                    Toast.makeText(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.sucessStr), Toast.LENGTH_LONG).show();
                }else if(errorStr.equals(s)){
                    Toast.makeText(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.errorStr), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.errorStr), Toast.LENGTH_LONG).show();
                }
            }

        }
        ModifyTrash modifyTrash = new ModifyTrash();
        modifyTrash.execute();
    }

    /**
     *
     * @param context
     * @param catForeignKeyCountry
     * @param categories
     */
    public static void getCategories(final Context context,
                                     final String catForeignKeyCountry,
                                     final List<HashMap<String,String>> categories){

        // todo a renommer
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Fetching...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                String result = requestHandler.sendGetRequestParam(Config.URL_GET_ALL_CAT, "1");
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray  jsonArrayResult  = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i < jsonArrayResult.length(); i++){
                        JSONObject jo = jsonArrayResult.getJSONObject(i);
                        String id   = jo.getString(Config.KEY_CAT_ID);
                        String name = jo.getString(Config.KEY_CAT_NAME);

                        HashMap<String,String> category = new HashMap<>();
                        category.put(Config.KEY_CAT_NAME,id);
                        category.put(Config.KEY_CAT_NAME,name);
                        categories.add(category);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        GetEmployee getEmployee = new GetEmployee();
        getEmployee.execute();
    }

    /**
     * Recupere toutes les ordures en base
     * TODO le faire seulement avec le code postal
     *
     * @param fragment
     * @param binder
     * @param latitude
     * @param longitude
     * @param listView
     * @param sortChoice
     * @param category
     */
    public static void getAllTrashWhithSort(final Fragment            fragment,
                                            final GPSService.MyBinder binder,
                                            final double              latitude,
                                            final double              longitude,
                                            final RecyclerView        listView,
                                            final Boolean             sortChoice,
                                            final Category            category){

        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                String fetchingData = fragment.getResources().getString(R.string.fetchingData);
                String wait         = fragment.getResources().getString(R.string.wait);
                loading = ProgressDialog.show(fragment.getActivity(), fetchingData, wait, false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();

                String catNumber    = String.valueOf(category.getNumber());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("cat_number=").append(catNumber);

                String result = requestHandler.sendGetRequestParam(Config.URL_GET_ALL_BY_CAT, stringBuilder.toString());
                return result;
            }

            @Override
            protected void onPostExecute(String inString) {
                super.onPostExecute(inString);
                loading.dismiss();
                JSON_STRING = inString;
                JSONObject jsonObject = null;
                ArrayList<Trash> trashes = new ArrayList<Trash>();
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i<result.length(); i++){
                        JSONObject jo     = result.getJSONObject(i);
                        String id         = jo.getString(Config.KEY_TRASH_ID);
                        String urlImgStrg = jo.getString(Config.KEY_TRASH_IMG);
                        String catNumber    = jo.getString("catNumber");
                        String dateTime   = jo.getString(Config.KEY_TRASH_DATETIME);
                        double latDbl     = Double.valueOf(jo.getString(Config.KEY_TRASH_LATITUDE));
                        double longDbl    = Double.valueOf(jo.getString(Config.KEY_TRASH_LONGITUDE));

                        Trash trash = new Trash();
                        trash.setId(Long.valueOf(id));

                        Category category = Config.getCategoryByNumber(Integer.valueOf(catNumber));
                        trash.setCatName(category.getName());
                        trash.setCatNumber(Integer.valueOf(catNumber));
                        trash.setUrlImage(urlImgStrg);
                        trash.setLatitude(latDbl);
                        trash.setLongitude(longDbl);
                        trash.setDateAndTime(dateTime);
                        Calendar myCalendar = MyUtils.parseDateTime(dateTime);
                        MyUtils.getStringDateTime();
                        Calendar calendar = Calendar.getInstance();

                        String elapsedTime = MyUtils.substractCalendar(fragment.getContext(), myCalendar, calendar);
                        trash.setTimeElapsed(elapsedTime);

                        double distance = MyUtils.DistanceTo2(latitude,longitude,latDbl,longDbl);
                        trash.setDistanceFromUser(distance);

                        trashes.add(trash);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(sortChoice == true){
                    Collections.sort(trashes,new TrashComparatorDistanceNearestToDistant());
                }else if (sortChoice == false){
                    Collections.sort(trashes,new TrashComparatorTimeNewestToOldest());
                }else {
                    // FIXME TODO Catcher une erreur
                }

                Resources resources = fragment.getResources();
                TrashListAdapter adapter = new TrashListAdapter();
                adapter.setTrashList(trashes);
                adapter.setResources(resources);
                adapter.setBinder(binder);
                adapter.setContext(fragment.getContext());
                adapter.setFragmentManager(fragment.getFragmentManager());
                listView.setAdapter(adapter);
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    public static void getAllTrashWhithSort(final Fragment            fragment,
                                            final GPSService.MyBinder binder,
                                            final double              latitude,
                                            final double              longitude,
                                            final RecyclerView        listView,
                                            final String              sortChoice,
                                            final Category            category){

        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                String fetchingData = fragment.getResources().getString(R.string.fetchingData);
                String wait         = fragment.getResources().getString(R.string.wait);
                loading = ProgressDialog.show(fragment.getActivity(), fetchingData, wait, false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();

                String catNumber    = String.valueOf(category.getNumber());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("cat_number=").append(catNumber);

                String result = requestHandler.sendGetRequestParam(Config.URL_GET_ALL_BY_CAT, stringBuilder.toString());
                return result;
            }

            @Override
            protected void onPostExecute(String inString) {
                super.onPostExecute(inString);
                loading.dismiss();
                JSON_STRING = inString;
                JSONObject jsonObject = null;
                ArrayList<Trash> trashes = new ArrayList<Trash>();
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i<result.length(); i++){
                        JSONObject jo     = result.getJSONObject(i);
                        String id         = jo.getString(Config.KEY_TRASH_ID);
                        String urlImgStrg = jo.getString(Config.KEY_TRASH_IMG);
                        String catNumber    = jo.getString("catNumber");
                        String dateTime   = jo.getString(Config.KEY_TRASH_DATETIME);
                        double latDbl     = Double.valueOf(jo.getString(Config.KEY_TRASH_LATITUDE));
                        double longDbl    = Double.valueOf(jo.getString(Config.KEY_TRASH_LONGITUDE));

                        Trash trash = new Trash();
                        trash.setId(Long.valueOf(id));

                        Category category = Config.getCategoryByNumber(Integer.valueOf(catNumber));
                        trash.setCatName(category.getName());
                        trash.setCatNumber(Integer.valueOf(catNumber));
                        trash.setUrlImage(urlImgStrg);
                        trash.setLatitude(latDbl);
                        trash.setLongitude(longDbl);
                        trash.setDateAndTime(dateTime);
                        Calendar myCalendar = MyUtils.parseDateTime(dateTime);
                        MyUtils.getStringDateTime();
                        Calendar calendar = Calendar.getInstance();

                        String elapsedTime = MyUtils.substractCalendar(fragment.getContext(), myCalendar, calendar);
                        trash.setTimeElapsed(elapsedTime);

                        double distance = MyUtils.DistanceTo2(latitude,longitude,latDbl,longDbl);
                        trash.setDistanceFromUser(distance);

                        trashes.add(trash);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(fragment.getResources().getString(R.string.nearToFar).equals(sortChoice)){
                    Collections.sort(trashes,new TrashComparatorDistanceNearestToDistant());
                }else if(fragment.getResources().getString(R.string.farToNear).equals(sortChoice)){
                    Collections.sort(trashes,new TrashComparatorDistanceDistantToNearest());
                }else if(fragment.getResources().getString(R.string.newestToOldest).equals(sortChoice)){
                    Collections.sort(trashes,new TrashComparatorTimeNewestToOldest());
                }else if(fragment.getResources().getString(R.string.oldestToNewest).equals(sortChoice)){
                    Collections.sort(trashes,new TrashComparatorTimeOldestToNewest());
                }else {
                    // FIXME TODO Catcher une erreur
                }

                Resources resources = fragment.getResources();
                TrashListAdapter adapter = new TrashListAdapter();
                adapter.setTrashList(trashes);
                adapter.setResources(resources);
                adapter.setBinder(binder);
                adapter.setContext(fragment.getContext());
                adapter.setFragmentManager(fragment.getFragmentManager());
                listView.setAdapter(adapter);
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    /**
     *
     * @param fragment
     * @param category
     * @param gmaps
     */
    public static void getAllTrashForMap(final Fragment fragment,
                                         final Category category,
                                         final GoogleMap gmaps,
                                         final Map<Marker, Trash> allMarkersMap,
                                         final double latitude,
                                         final double longitude) {

        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                String fetchingData = fragment.getResources().getString(R.string.fetchingData);
                String wait = fragment.getResources().getString(R.string.wait);
                loading = ProgressDialog.show(fragment.getActivity(), fetchingData, wait, false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();

                String catNumber    = String.valueOf(category.getNumber());

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("&cat_number=").append(catNumber);

                String result = requestHandler.sendGetRequestParam(Config.URL_GET_ALL_BY_CAT, stringBuilder.toString());
                return result;
            }

            @Override
            protected void onPostExecute(String inString) {
                super.onPostExecute(inString);
                loading.dismiss();
                JSON_STRING = inString;
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    List<Trash> trashes = new ArrayList<com.valhatech.garbagecollector.data.Trash>();

                    for(int i = 0; i < result.length(); i++){
                        JSONObject jo = result.getJSONObject(i);
                        String id         = jo.getString(Config.KEY_TRASH_ID);
                        String urlImgStrg = jo.getString(Config.KEY_TRASH_IMG);

                        String catNumber    = jo.getString("catNumber");
                        String dateTime   = jo.getString(Config.KEY_TRASH_DATETIME);
                        double latDbl     = Double.valueOf(jo.getString(Config.KEY_TRASH_LATITUDE));
                        double longDbl    = Double.valueOf(jo.getString(Config.KEY_TRASH_LONGITUDE));

                        Trash trash = new Trash();
                        trash.setId(Long.valueOf(id));

                        Category category = Config.getCategoryByNumber(Integer.valueOf(catNumber));
                        trash.setCatName(category.getName());
                        trash.setCatNumber(Integer.valueOf(catNumber));

                        trash.setUrlImage(urlImgStrg);
                        trash.setLatitude(latDbl);
                        trash.setLongitude(longDbl);
                        trash.setDateAndTime(dateTime);
                        Calendar myCalendar = MyUtils.parseDateTime(dateTime);
                        MyUtils.getStringDateTime();
                        Calendar calendar = Calendar.getInstance();

                        String elapsedTime = MyUtils.substractCalendar(fragment.getContext(), myCalendar, calendar);
                        trash.setTimeElapsed(elapsedTime);

                        trashes.add(trash);
                    }

                    gmaps.clear();
                    //http://stackoverflow.com/questions/14811579/how-to-create-a-custom-shaped-bitmap-marker-with-android-map-api-v2
                    for (Trash trash : trashes) {

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(trash.getLatitude(), trash.getLongitude()));

                        int catNumber = trash.getCatNumber();
                        float bitmapDescriptorFactory = 0 ;

                        //https://developers.google.com/maps/documentation/android-api/marker#personnaliser_un_marqueur
                        switch (catNumber) {
                            case 1:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_furniture));
                                markerOptions.flat(true);
                                break;
                            case 2:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_book));
                                markerOptions.flat(true);
                                break;
                            case 3:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_textile));
                                markerOptions.flat(true);
                                break;
                            case 4:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rubble));
                                markerOptions.flat(true);
                                break;
                            case 5:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_multimedia));
                                markerOptions.flat(true);
                                break;
                            case 6:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_appliance));
                                markerOptions.flat(true);
                                break;
                            case 7:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bedding));
                                markerOptions.flat(true);
                                break;
                            case 8:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_detached_piece));
                                markerOptions.flat(true);
                                break;
                            case 9:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_toy_hobby));
                                markerOptions.flat(true);
                                break;
                            case 10:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_other));
                                markerOptions.flat(true);
                                break;
                            case 11:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pallet));
                                markerOptions.flat(true);
                                break;
                            //default: bitmapDescriptorFactory = BitmapDescriptorFactory.defaultMarker() ; break;
                        }

                        Marker myMarker = gmaps.addMarker(markerOptions);
                        allMarkersMap.put(myMarker,trash);
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_fixed_black_48dp));
                    markerOptions.flat(true);
                    Marker myMarker = gmaps.addMarker(markerOptions);
                    allMarkersMap.put(myMarker,null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }

    /**
     * Fonction appele durant le splash screen
     */
    public static void getAllTrash(){

        class GetJSONAllTrash extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String inString) {
                super.onPostExecute(inString);
                JSON_STRING = inString;
                JSONObject jsonObject = null;
                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i<result.length(); i++){
                        JSONObject jo = result.getJSONObject(i);
                        String id = jo.getString(Config.KEY_TRASH_ID);

                        HashMap<String,String> employees = new HashMap<>();
                        employees.put(Config.KEY_TRASH_ID,id);
                        list.add(employees);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                String result = requestHandler.sendGetRequest(Config.URL_GET_ALL);
                return result;
            }
        }
        GetJSONAllTrash getJSON = new GetJSONAllTrash();
        getJSON.execute();
    }

    /**
     * Permet de recuperer un dechet via un id
     * @param fragment
     * @param id
     */
    public static void getOneTrashById(final Fragment  fragment,
                                       final Location  location,
                                       final String    id,
                                       final TextView  textViewDisplayTrashCategory,
                                       final ImageView imageViewDisplayTrash,
                                       final TextView  myTextViewGpsCoordinate,
                                       final TextView  myTextViewSince,
                                       final EditText  myTextViewAddress,
                                       final TextView  myTextViewDistance,
                                       final StringBuilder coordinateLatLong,
                                       final ImageButton   buttonDisplayTrashRemoved){

        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {

                String countryId = String.valueOf(Config.getCountry().getId());

                HashMap<String,String> params1 = new HashMap<>();
                params1.put(Config.KEY_TRASH_ID, id);
                params1.put(Config.KEY_COUNTRY_ID, countryId);

                RequestHandler requestHandler = new RequestHandler();
                String result = requestHandler.sendPostRequest(Config.URL_GET_ONE_TRASH_BY_ID, params1);


                return result;
            }

            @Override
            protected void onPostExecute(String inString) {
                super.onPostExecute(inString);

                JSON_STRING = inString;
                JSONObject jsonObject = null;
                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i<result.length(); i++){
                        JSONObject jo = result.getJSONObject(i);
                        //String id = jo.getString(Config.KEY_TRASH_ID);

                        String pictureUrl = new String(jo.getString(Config.KEY_TRASH_IMG));
                        MyUtils.downloadPictureForDisplayTrash(pictureUrl, imageViewDisplayTrash);

                        //textViewDisplayTrashCategory.setText(MyUtils.Capitalize(jo.getString(Config.KEY_CAT_NAME)));
                        String numberStr = jo.getString("trash_temp_fk_category");
                        int number = Integer.valueOf(numberStr);
                        Category category = Config.getCategoryByNumber(number);
                        textViewDisplayTrashCategory.setText(category.getName());

                        coordinateLatLong.append(jo.getString(Config.KEY_TRASH_LATITUDE));
                        coordinateLatLong.append(",");
                        coordinateLatLong.append(jo.getString(Config.KEY_TRASH_LONGITUDE));
                        myTextViewGpsCoordinate.setText(coordinateLatLong.toString());

                        Calendar calendarTrash = MyUtils.parseDateTime(jo.getString(Config.KEY_TRASH_DATETIME));
                        Calendar calendar = Calendar.getInstance();
                        String elapsedTime = MyUtils.substractCalendar(fragment.getContext(), calendarTrash, calendar);
                        myTextViewSince.setText(elapsedTime);

                        if(null == location){
                            myTextViewDistance.setText(fragment.getResources().getString(R.string.gpsNotInit));
                        }else{
                            double distance = MyUtils.DistanceTo2(location.getLatitude(),
                                                           location.getLongitude(),
                                                           Double.valueOf(jo.getString(Config.KEY_TRASH_LATITUDE)),
                                                           Double.valueOf(jo.getString(Config.KEY_TRASH_LONGITUDE)));

                            StringBuilder stringBuilder = new StringBuilder();

                            //boolean GodApp = true ;

                            // TODO faire une fonction de ce bout de code factoriser quoi
                            if(distance < 1000){
                                stringBuilder.append(String.valueOf(distance));
                                stringBuilder.append(" ");
                                stringBuilder.append(fragment.getResources().getString(R.string.meters));

                                if(Config.developperFlag == false){
                                    if(distance < 100){
                                        buttonDisplayTrashRemoved.setVisibility(View.VISIBLE);
                                    }else{
                                        buttonDisplayTrashRemoved.setVisibility(View.GONE);
                                    }
                                }
                            }else{
                                distance = distance / 1000 ;
                                stringBuilder.append(String.valueOf(distance));
                                stringBuilder.append(" ");
                                stringBuilder.append(fragment.getResources().getString(R.string.kilometers));
                                if(Config.developperFlag == false){
                                    buttonDisplayTrashRemoved.setVisibility(View.GONE);
                                }
                            }

                            myTextViewDistance.setText(stringBuilder.toString());
                        }

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(jo.getString(Config.KEY_TRASH_ADDRESS));
                        stringBuilder.append(" ").append(jo.getString(Config.KEY_TOWN_POSTALCODE));
                        stringBuilder.append(" ").append(jo.getString(Config.KEY_TOWN_NAME));
                        stringBuilder.append(" ").append(jo.getString(Config.KEY_COUNTRY_NAME));
                        myTextViewAddress.setText(stringBuilder.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    /**
     * Permet de supprimer un dechet de la base de donnes
     * @param id identifiant du dechet a supprimer
     */
    public static void deleteOneTrashById(final String id){

        class GetJSON extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                requestHandler.sendGetRequestParam(Config.URL_DELETE_ONE_TRASH_BY_ID, id);
                return null;
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
