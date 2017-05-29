package com.valhatech.garbagecollector.data;

import android.content.res.Resources;
import android.graphics.Point;

import com.valhatech.garbagecollector.R;

import java.util.ArrayList;

/**
 * Created by fred on 14/03/2016.
 */
public class Config {

    public static final boolean developperFlag = false ;

    //Address of our scripts of the CRUD
    public static final String UPLOAD_IMG_KEY = "image";
    public static final String UPLOAD_IMG_NAME = "name";
    public static final String webAddress = "http://www.valhatech.com/trashcollector" ;
    // Address
    public static final String URL_ADD                    = webAddress+"/addTrash.php";
    public static final String URL_GET_ALL                = webAddress+"/getAllTrash.php";
    public static final String URL_GET_ALL_BY_CAT         = webAddress+"/getAllTrashWithSort.php?";
    public static final String URL_GET_ALL_CAT            = webAddress+"/getAllCat.php?cat_fk_country=";
    public static final String URL_GET_ONE_TRASH_BY_ID    = webAddress+"/getOneTrashById.php";
    public static final String URL_DELETE_ONE_TRASH_BY_ID = webAddress+"/deleteOneTrashById.php?id=";
    //public static final String URL_GET_TOWN               = webAddress+"/getTownAndCountry.php?";
    public static final String URL_GET_TOWN               = webAddress+"/getTownAndCountry.php?";
    public static final String URL_MODIFY_TRASH           = webAddress+"/modifyTrash.php?";

    //JSON Tags
    public static final String TAG_JSON_ARRAY = "result";

    // Trash keys that will be used to send the request to php scripts
    public static final String KEY_TRASH_ID          = "trash_temp_id";
    //public static final String KEY_TRASH_NAME      = "trash_temp_name";
    public static final String KEY_TRASH_IMG         = "trash_temp_img";
    public static final String KEY_TRASH_CATEGORY    = "trash_temp_fk_category";
    public static final String KEY_TRASH_DATETIME    = "trash_temp_date_time";
    public static final String KEY_TRASH_LATITUDE    = "trash_temp_latitude";
    public static final String KEY_TRASH_LONGITUDE   = "trash_temp_longitude";
    public static final String KEY_TRASH_ADDRESS     = "trash_temp_address";
    public static final String KEY_TRASH_POSTAL_CODE = "trash_temp_postal_code";
    public static final String KEY_TRASH_TOWN        = "trash_temp_town";
    public static final String KEY_TRASH_COUNTRY     = "trash_temp_country";
    public static final String KEY_TRASH_USER        = "trash_temp_fk_user";

    // Categories keys
    public static final String KEY_CAT_ID         = "cat_id";
    public static final String KEY_CAT_FK_COUNTRY = "cat_fk_country";
    public static final String KEY_CAT_NAME       = "cat_name";
    public static final String KEY_CAT_NUMBER     = "cat_number";

    // Country keys
    public static final String KEY_COUNTRY_ID    = "country_id";
    public static final String KEY_COUNTRY_NAME  = "country_name";

    // Town keys
    public static final String KEY_TOWN_ID         = "town_id";
    public static final String KEY_TOWN_NAME       = "town_name";
    public static final String KEY_TOWN_POSTALCODE = "town_postal_code";
    public static final String KEY_TOWN_COUNTRY    = "town_fk_country";

    // On déclare une constante dans la classe FirstClass
    public static final int REQUEST_IMAGE_CAPTURE   = 1 ;
    public static final int REQUEST_OPEN_TRASH_LIST = 2 ;

    public static final int GMAPS_ZOOM_LEVEL = 13 ;

    //--- Data

    //---MyCategory
    private static ArrayList<Category> myCategoriesForAllTrash;
    private static ArrayList<Category> myCategoriesForAddTrash;

    public static void InitCategoryList(Resources resources){

        myCategoriesForAllTrash = new ArrayList<Category>();
        myCategoriesForAddTrash = new ArrayList<Category>();

        Category cat0  = new Category(resources.getString(R.string.catAll),(Integer)0) ;
        myCategoriesForAllTrash.add(cat0) ;
        Category cat1  = new Category(resources.getString(R.string.catFurniture),(Integer)1) ;
        myCategoriesForAllTrash.add(cat1) ;
        myCategoriesForAddTrash.add(cat1) ;
        Category cat2  = new Category(resources.getString(R.string.catBook),(Integer)2) ;
        myCategoriesForAllTrash.add(cat2) ;
        myCategoriesForAddTrash.add(cat2) ;
        Category cat3  = new Category(resources.getString(R.string.catTextile),(Integer)3) ;
        myCategoriesForAllTrash.add(cat3) ;
        myCategoriesForAddTrash.add(cat3) ;
        Category cat4  = new Category(resources.getString(R.string.catRubble),(Integer)4) ;
        myCategoriesForAllTrash.add(cat4) ;
        myCategoriesForAddTrash.add(cat4) ;
        Category cat5  = new Category(resources.getString(R.string.catMultimedia),(Integer)5) ;
        myCategoriesForAllTrash.add(cat5) ;
        myCategoriesForAddTrash.add(cat5) ;
        Category cat6  = new Category(resources.getString(R.string.catHomeAppliance),(Integer)6) ;
        myCategoriesForAllTrash.add(cat6) ;
        myCategoriesForAddTrash.add(cat6) ;
        Category cat7  = new Category(resources.getString(R.string.catBedding),(Integer)7) ;
        myCategoriesForAllTrash.add(cat7) ;
        myCategoriesForAddTrash.add(cat7) ;
        Category cat8  = new Category(resources.getString(R.string.catDetachedPieces),(Integer)8) ;
        myCategoriesForAllTrash.add(cat8) ;
        myCategoriesForAddTrash.add(cat8) ;
        Category cat9  = new Category(resources.getString(R.string.catToyHobby),(Integer)9) ;
        myCategoriesForAllTrash.add(cat9) ;
        myCategoriesForAddTrash.add(cat9) ;
        Category cat10 = new Category(resources.getString(R.string.catOther),(Integer)10) ;
        myCategoriesForAllTrash.add(cat10) ;
        myCategoriesForAddTrash.add(cat10) ;
        Category cat11 = new Category(resources.getString(R.string.catPallet),(Integer)11) ;
        myCategoriesForAllTrash.add(cat11) ;
        myCategoriesForAddTrash.add(cat11) ;
    }

    public static Category getCategoryByNumber(int number){

        for (Category category: myCategoriesForAllTrash) {
            if(number == category.getNumber()){
                return category ;
            }
        }
        return null ;
    }

    public static Category getCategoryForAddTrashByName(String name){

        for (Category category: myCategoriesForAddTrash) {
            if(name == category.getName()){
                return category ;
            }
        }
        return null ;
    }

    public static ArrayList<Category> getMyCategoriesForAllTrash() {

        return myCategoriesForAllTrash;
    }

    public static ArrayList<Category> getMyCategoriesForAddTrash() {
        return myCategoriesForAddTrash;
    }


    //--- Country
    private static Country country ;

    public static Country getCountry() {
        return country;
    }

    public static void setCountry(Country country) {
        Config.country = country;
    }

    //--- Screen size
    private static Point screenSize;

    public static Point getScreenSize() {
        return screenSize;
    }

    public static void setScreenSize(Point size) {
        Config.screenSize = size;
    }

    //--- Latitude and longitude
    private static double lastLatitude ;
    private static double lastLongitude ;

    public static double getLastLatitude() {
        return lastLatitude;
    }

    public static void setLastLatitude(double lastLatitude) {
        Config.lastLatitude = lastLatitude;
    }

    public static double getLastLongitude() {
        return lastLongitude;
    }

    public static void setLastLongitude(double lastLongitude) {
        Config.lastLongitude = lastLongitude;
    }
}
