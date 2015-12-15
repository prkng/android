package ng.prk.prkngandroid;

import android.os.Build;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Const {

    public static final String DATABASE_NAME = "prkng.db";
    public static final String APP_PREFS_NAME = "prkngPrefs";
    public static final int UNKNOWN_VALUE = -1;

    public interface UiConfig {
        double DEFAULT_ZOOM = 15.0d;
        double SPOTS_MIN_ZOOM = 15.0d;
        double LOTS_MIN_ZOOM = 14.0d;
        double CARSHARE_VEHICLES_MIN_ZOOM = 13.0d;
        double SMALL_BUTTONS_ZOOM = 17.0d;
        double BIG_BUTTONS_ZOOM = 18.0d;
        double MY_LOCATION_ZOOM = 17.0d;
        double MIN_UPDATE_DISTACE = 25.0d; // Metres
        double MONTREAL_NATURAL_NORTH_ROTATION = -34.0d;
        LatLng MONTREAL_LAT_LNG = new LatLng(45.501689d, -73.567256d);
        LatLng MANHATTAN_LAT_LNG = new LatLng(40.814589d, -73.927345d);
        float LOT_INFO_ATTRS_OPACITY = 0.4f;
    }

    public interface MapSections {
        int _COUNT = 4;
        int OFF_STREET = 0;
        int ON_STREET = 1;
        int CARSHARE_SPOTS = 2;
        int CARSHARE_VEHICLES = 3;
    }

    public interface ApiArgs {
        String API_KEY = "X-API-KEY";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String EVENT = "event";
        String QUERY = "query";
        String RADIUS = "radius";
        String PERMITS = "permit";
        String CARSHARE_COMPANIES = "company";
        String LIMIT = "limit";
        String CITY = "city";
        String PARTNER_NAME = "partner_name";
        String ACCESSIBLE = "handicap";
        String STREET_VIEW = "street_view";
        String LOT_ID = "lot_id";
        String SPOT_ID = "slot_id";
        String CHECKIN_ID = "id";
        String USE_FILTER = "filter";
        String LANG = "lang";
        String DEVICE_TYPE = "device_type";
        String DEVICE_ID = "device_id";
        String IMAGE_TYPE = "image_type";
        String FILE_NAME = "file_name";
        String OAUTH_TOKEN = "access_token";
        String PASSWORD = "password";
        String OAUTH_TYPE = "type";
        String EMAIL = "email";
        String NAME = "name";
        String GENDER = "gender";
        String BIRTH_YEAR = "birthyear";
        String IMAGE_URL = "image_url";
        String NOTES = "notes";
        String USE_CARSHARE = "carsharing";
        String USE_COMPACT = "compact";
        String CHECKIN_TIMESTAMP = "checkin";
        String DURATION = "duration";
    }

    public interface ApiValues {
        String OAUTH_TYPE_FACEBOOK = "facebook";
        String OAUTH_TYPE_GOOGLEPLUS = "google";
        int DEFAULT_RADIUS = 300; // Radius search in meters
        float DEFAULT_DURATION = 0.5f; // Desired Parking time in hours
        String SPOT_TYPE_PAID = "paid";
        String SPOT_TYPE_PERMIT = "permit";
        String CAR_TYPE_ELECTRIC = "electric";
        String CARSHARE_COMPANY_CAR2GO = "car2go";
        String CARSHARE_COMPANY_COMMUNAUTO = "communauto";
        String CARSHARE_COMPANY_AUTOMOBILE = "auto-mobile";
        String CARSHARE_COMPANY_ZIPCAR = "zipcar";
        // Following are ISO days-of-week
        String AGENDA_DAY_MONDAY = "1";
        String AGENDA_DAY_TUESDAY = "2";
        String AGENDA_DAY_WEDNESDAY = "3";
        String AGENDA_DAY_THURSDAY = "4";
        String AGENDA_DAY_FRIDAY = "5";
        String AGENDA_DAY_SATURDAY = "6";
        String AGENDA_DAY_SUNDAY = "7";
    }

    public interface ApiPaths {
        // String BASE_URL = BuildConfig.API_BASE_URL;
        String VERSION = "/v1";

        String POST_ANALYTICS_EVENT = VERSION + "/analytics/event";
        String POST_ANALYTICS_SEARCH = VERSION + "/analytics/search";
        String GET_AREAS = VERSION + "/areas";
        String GET_CARSHARE_LOTS = VERSION + "/carshare_lots";
        String GET_CARSHARE_VEHICLES = VERSION + "/carshares";
        String GET_CHECKINS = VERSION + "/checkins";
        String POST_CHECKINS = VERSION + "/checkins";
        String DELETE_CHECKIN = VERSION + "/checkins/{" + ApiArgs.CHECKIN_ID + "}";
        String GET_CITIES = VERSION + "/cities";
        String POST_HELLO = VERSION + "/hello";
        String POST_IMAGES = VERSION + "/images";
        String POST_LOGIN = VERSION + "/login";
        //        String POST_PASSWD_CHANGE = VERSION + "/login/changepass";
        String POST_REGISTER = VERSION + "/login/register";
        String POST_PASSWD_RESET = VERSION + "/login/resetpass";
        String GET_LOTS = VERSION + "/lots";
        String GET_LOT = VERSION + "/lots/{" + ApiArgs.LOT_ID + "}";
        String POST_REPORT = VERSION + "/reports";
        String GET_SPOTS = VERSION + "/slots";
        String GET_SPOT = VERSION + "/slots/{" + ApiArgs.SPOT_ID + "}";
        String GET_USER_PROFILE = VERSION + "/user/profile";
        String PUT_USER_PROFILE = VERSION + "/user/profile";
    }

    public interface FragmentTags {
        String MAP = "f_map";
        String ABOUT = "f_about";
        String SETTINGS = "f_settings";
    }

    public interface RequestCodes {
        int PERMISSION_ACCESS_LOCATION = 10;
        int PERMISSION_ACCESS_FINE_LOCATION = 20;
        int PERMISSION_ACCESS_COARSE_LOCATION = 30;
        int ONBOARDING = 40;
        int AUTH_LOGIN = 50;
    }

    public interface BundleKeys {
        String IS_INITIAL_ONBOARDING = "onboarding_skip_login";
    }

    /**
     * The agenda's different parking restriction types.
     */
    public interface ParkingRestrType {
        int NONE = 0;
        int ALL_TIMES = 1;
        int TIME_MAX = 2;
        int PAID = 3;
        int TIME_MAX_PAID = 4;
    }

    /**
     * Renaming Spot's restrictions for Lot's state
     */
    public interface BusinnessHourType {
        int FREE = ParkingRestrType.NONE; // 0
        int CLOSED = ParkingRestrType.ALL_TIMES; // 1
        int OPEN = ParkingRestrType.PAID; // 3
    }

    public static interface TypeFaces {
        final int _COUNT = 3;
        final String REGULAR = "fonts/intro_regular.ttf";
        final String BOOK = "fonts/intro_book.ttf";
        final String LIGHT = "fonts/intro_light.ttf";
    }

    public interface PrefsNames {
        String API_KEY = "p_api_key";
        String IS_ONBOARDING = "p_is_onboarding";
        // PreferenceScreens
        String TOGGLE_LOGIN = "prefs_toggle_login";
    }

    public interface PrefsValues {

    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final boolean SUPPORTS_M = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean SUPPORTS_LOLLIPOP_MR1 = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    public static final boolean SUPPORTS_LOLLIPOP = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean SUPPORTS_KITKAT = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean SUPPORTS_JELLY_BEAN_MR2 = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final boolean SUPPORTS_JELLY_BEAN_MR1 = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final boolean SUPPORTS_JELLY_BEAN = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
}
