package ng.prk.prkngandroid;

import android.os.Build;
import android.text.format.DateUtils;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Const {

    public static final String DATABASE_NAME = "prkng.db";
    public static final String APP_PREFS_NAME = "prkngPrefs";
    public static final int UNKNOWN_VALUE = -1;

    public interface UiConfig {
        double DEFAULT_ZOOM = 15.0d;
        double SPOTS_MIN_ZOOM = 15.0d;
        double LOTS_MIN_ZOOM = 14.0d;
        double CARSHARE_SPOTS_MIN_ZOOM = SPOTS_MIN_ZOOM;
        double CARSHARE_VEHICLES_MIN_ZOOM = 13.0d;
        double CLEAR_MAP_MIN_ZOOM = 11.0d;
        double AVAILABLE_CITIES_MIN_ZOOM = 6.0d;
        double SMALL_BUTTONS_ZOOM = 17.0d;
        double BIG_BUTTONS_ZOOM = 18.0d;
        double MY_LOCATION_ZOOM = 17.0d;
        double CHECKIN_ZOOM = 16.0d;
        double MIN_UPDATE_DISTACE = 50.0d; // Metres
        String DEFAULT_CITY = PrefsValues.CITY_MONTREAL;
        double MONTREAL_NATURAL_NORTH_ROTATION = -34.0d;
        LatLng MONTREAL_LAT_LNG = new LatLng(45.5016889d, -73.567256d);
        float LOT_INFO_ATTRS_OPACITY = 0.55f;
        float DEFAULT_DURATION = 0.5f;
        float DRIVING_MIN_SPEED = 25000f; // 25 km/h
        double MIN_CLICK_DISTANCE = 13d; // 13 metres
        int FOURSQUARE_LIMIT = 10;
    }

    public interface AppsIntents {
        String GOOGLE_MAPS = "http://maps.google.com/maps?saddr=%1$s&daddr=%2$s&f=d";
        String GOOGLE_NAVIGATION = "google.navigation:q=%1$s,%2$s&mode=d";
//        String GOOGLE_MAPS = "geo:%1$s?q=%2$s(%3$s)";
    }

    public interface NotifationConfig {
        long EXPIRY = DateUtils.MINUTE_IN_MILLIS * 30;
        long SMART_DAY_OFFSET = DateUtils.DAY_IN_MILLIS; // Smart alert is on the day before
        int SMART_HOUR_OF_DAY = 20; // Smart alert is around 20:00
        int SMART_MINUTE = 0; // Smart alert is around 20:00
    }

    public interface MapSections {
        int _COUNT = 2;
        int CARSHARE_OFFSET = 2;
        int ON_STREET = 0;
        int OFF_STREET = 1;
        int CARSHARE_VEHICLES = 2;
        int CARSHARE_SPOTS = 3;
    }

    public interface TutorialSections {
        int _COUNT = 6;
        int SPLASH = 0;
        int ONE = 1;
        int TWO = 2;
        int THREE = 3;
        int FOUR = 4;
        int TRANSPARENT = 5;
    }

    public interface NotificationTypes {
        int START = 0;
        int SMART_REMINDER = 1;
        int EXPIRY = 2;
//        int GEOFENCE = 3;
    }

    public interface IntentActions {
        String NOTIFY_SMART_REMINDER = "ng.prk.prkngandroid.NOTIFY_SMART_REMINDER";
        String NOTIFY_EXPIRY = "ng.prk.prkngandroid.NOTIFY_EXPIRY";
    }

    public interface ApiArgs {
        String API_KEY = "X-API-KEY";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String GEO_LAT = "lat";
        String GEO_LNG = "long";
        String EVENT = "event";
        String QUERY = "query";
        String RADIUS = "radius";
        String NEAREST = "nearest";
        String PERMITS = "permit";
        String CARSHARE_COMPANIES = "company";
        String LIMIT = "limit";
        String CITY = "city";
        String PARTNER_NAME = "partner_name";
        String PAID_HOURLY_RATE = "paid_hourly_rate";
        String PERMIT_NO = "permit_no";
        String RESTRICT_TYPES = "restrict_types";
        String TIME_MAX_PARKING = "time_max_parking";
        String SEASON_START = "season_start";
        String SEASON_END = "season_end";
        String SPECIAL_DAY = "special_days";
        String ACCESSIBLE = "handicap";
        String STREET_VIEW = "street_view";
        String AREA_NAME = "name_disp";
        String DISPLAY_NAME = "display_name";
        String URBAN_AREA_RADIUS = "urban_area_radius";
        String COUNTRY_CODE = "country_code";
        String REGION_CODE = "region_code";
        String USER_ID = "user_id";
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
        String CHECKIN_START_TIME = "checkin_time";
        String CHECKIN_END_TIME = "checkout_time";
        String DURATION = "duration";
        // Mapbox Search
        String MAPBOX_PROXIMITY = "proximity";
        String MAPBOX_TOKEN = "access_token";
        String MAPBOX_COUNTRY = "country";
        String MAPBOX_REGION = "region";
        // Foursquare Search
        String FOURSQUARE_QUERY = "query";
        String FOURSQUARE_LAT_LNG = "ll";
        String FOURSQUARE_RADIUS = "radius";
        String FOURSQUARE_LIMIT = "limit";
        String FOURSQUARE_VERSION = "v";
        String FOURSQUARE_CLIENT_ID = "client_id";
        String FOURSQUARE_CLIENT_SECRET = "client_secret";

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
        String CARSHARE_COMPANY_AUTOMOBILE = "auto-mobile";
        String CARSHARE_COMPANY_COMMUNAUTO = "communauto";
        String CARSHARE_COMPANY_ZIPCAR = "zipcar";
        // Following are ISO days-of-week
        String AGENDA_DAY_MONDAY = "1";
        String AGENDA_DAY_TUESDAY = "2";
        String AGENDA_DAY_WEDNESDAY = "3";
        String AGENDA_DAY_THURSDAY = "4";
        String AGENDA_DAY_FRIDAY = "5";
        String AGENDA_DAY_SATURDAY = "6";
        String AGENDA_DAY_SUNDAY = "7";
        String DEVICE_TYPE_ANDROID = "android";
    }

    public interface ApiPaths {
        // String BASE_URL = BuildConfig.API_BASE_URL; // "https://{api|test}.prk.ng/v1/"

        String POST_ANALYTICS_EVENT = "analytics/event";
        String POST_ANALYTICS_SEARCH = "analytics/search";
        String GET_AREAS = "areas";
        String GET_CARSHARE_LOTS = "carshare_lots";
        String GET_CARSHARE_VEHICLES = "carshares";
        String GET_CHECKINS = "checkins";
        String POST_CHECKINS = "checkins";
        String DELETE_CHECKIN = "checkins/{" + ApiArgs.CHECKIN_ID + "}";
        String GET_CITIES = "cities";
        String POST_HELLO = "hello";
        String POST_IMAGES = "images";
        String POST_LOGIN = "login";
        //        String POST_PASSWD_CHANGE = "login/changepass";
        String POST_REGISTER = "login/register";
        String POST_PASSWD_RESET = "login/resetpass";
        String GET_LOTS = "lots";
        String GET_LOT = "lots/{" + ApiArgs.LOT_ID + "}";
        String POST_REPORT = "reports";
        String GET_SPOTS = "slots";
        String GET_SPOT = "slots/{" + ApiArgs.SPOT_ID + "}";
        String GET_USER_PROFILE = "user/profile";
        String PUT_USER_PROFILE = "user/profile";

        // Search services
        String SEARCH_MAPBOX = "https://api.mapbox.com/geocoding/v5/" +
                "mapbox.places/{" + ApiArgs.QUERY + "}.json";
        String SEARCH_FOURSQUARE = "https://api.foursquare.com/v2/venues/suggestcompletion/";
    }

    public interface SearchApis {
    }

    public interface FragmentTags {
        String MAP = "f_map";
        String ABOUT = "f_about";
        String SETTINGS = "f_settings";
        String DIALOG_DURATIONS = "d_durations";
        String DIALOG_CITIES = "d_cities";
        String MAP_INFO = "f_map_info";
        String MAP_INFO_EXPANDED = "f_map_info_expanded";
        String CHECKIN_INFO = "f_checkin_info";
    }

    public interface RequestCodes {
        int PERMISSION_ACCESS_LOCATION = 10;
        int PERMISSION_ACCESS_FINE_LOCATION = 20;
        int PERMISSION_ACCESS_COARSE_LOCATION = 30;
        int ONBOARDING = 40;
        int AUTH_LOGIN = 50;
        int AUTH_LOGIN_GOOGLE = 60;
        int AUTH_LOGIN_GOOGLE_RESOLVE = 61;
        int AUTH_LOGIN_EMAIL = AUTH_LOGIN;
        int AUTH_LOGIN_FACEBOOK = 64206; // 0xface
        int NOTIFY_CHECKIN = 100;
        int CHECKIN_REMINDER = 110;
        int CHECKIN_SMART_REMINDER = 111;
        int CITY_SELECTOR = 120;
        int SEARCH = 130;
    }

    public interface BundleKeys {
        String IS_INITIAL_ONBOARDING = "onboarding_skip_login";
        String PAGE = "page";
        String URL = "url";
        String EMAIL = "email";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String ZOOM = "zoom";
        String CHECKIN_ID = "checkin_id";
        String CURRENT_INDEX = "current_index";
        String MARKER_ID = "marker_id";
        String MARKER_TITLE = "marker_title";
        String IS_EXPANDED = "is_expanded";
        String DURATION = "duration";
        String CITY = "city";
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

    public interface CarshareCompanies {
        String CAR2GO = "car2go";
        String AUTOMOBILE = "auto-mobile";
        String COMMUNAUTO = "communauto";
        String ZIPCAR = "zipcar";
    }

    public interface TypeFaces {
        int _COUNT = 3;
        String LIGHT = "fonts/IntroLight.ttf"; // Light
        String REGULAR = "fonts/IntroRegular.ttf"; // Book on Zeplin
        String BOLD = "fonts/IntroSemiBold.ttf"; // Intro-Regular on Zeplin
    }

    public interface PrefsNames {
        String IS_ONBOARDING = "p_is_onboarding";
        String NOTIFY_SMART_REMINDER = "p_notify_smart_reminder";
        // Authentication
        String AUTH_API_KEY = "p_api_key";
        String AUTH_USER_NAME = "p_auth_name";
        String AUTH_USER_EMAIL = "p_auth_email";
        String AUTH_USER_PICTURE = "p_auth_picture";
        // Map
        String DURATION = "p_duration";
        // Checkin
        String CHECKIN_ID = "p_checkin_id";
        String CHECKIN_ADDRESS = "p_checkin_address";
        String CHECKIN_START_AT = "p_checkin_start_at";
        String CHECKIN_END_AT = "p_checkin_end_at";
        String CHECKIN_SMART_REMINDER = "p_checkin_smart_reminder";
        String CHECKIN_LAT = "p_checkin_lat";
        String CHECKIN_LNG = "p_checkin_lng";
        // PreferenceScreens
        String NOTIFY_EXPIRY = "prefs_notify_expiry";
        String TOGGLE_LOGIN = "prefs_toggle_login";
        String CITY = "prefs_city";
        String CARSHARE_MODE = "prefs_carshare_mode";
        String CARSHARE_CAR2GO = "prefs_carshare_car2go";
        String CARSHARE_AUTOMOBILE = "prefs_carshare_automobile";
        String CARSHARE_COMMUNAUTO = "prefs_carshare_communauto";
        String CARSHARE_ZIPCAR = "prefs_carshare_zipcar";
        String ONBOARDING = "prefs_onboarding";
        String TERMS = "prefs_terms";
        String PRIVACY = "prefs_privacy";
        String FAQ = "prefs_faq";
    }

    public interface PrefsValues {
        String CITY_MONTREAL = "montreal";
        String CITY_NEW_YORK = "newyork";
        String CITY_QUEBEC = "quebec";
        String CITY_SEATTLE = "seattle";
    }

    public interface AnalyticsScreens {
        // Activities
        String ABOUT_ACTIVITY = "About";
        String CHECKIN_ACTIVITY = "Checkin";
        String LOGIN_ACTIVITY = "Login Services";
        String LOGIN_SIGNUP_ACTIVITY = "Login Sign-up";
        String LOGIN_EMAIL_ACTIVITY = "Login Email";
        String LOGIN_FORGOTPASSWORD_ACTIVITY = "Login Forgot password";
        String MAIN_ACTIVITY = "Main";
        String SEARCH_ACTIVITY = "Search";
        String SETTINGS_ACTIVITY = "Settings";
        String TUTORIAL_ACTIVITY = "Tutorial";
        String FAQ_WEBVIEW_ACTIVITY = "FAQ Webview";
        String TERMS_WEBVIEW_ACTIVITY = "Terms Webview";
        String PRIVACY_WEBVIEW_ACTIVITY = "Privacy Webview";
        String WEBVIEW_ACTIVITY = "Webview";
        // Fragments
        String AGENDA_LIST_FRAGMENT = "Agenda";
        String LOT_INFO_FRAGMENT = "Lot Info";
        String MAP_FRAGMENT = "Map";
        String MAP_ON_STREET = "Map On-street";
        String MAP_OFF_STREET = "Map Off-street";
        String MAP_CARSHARE_VEHICLES = "Map Carshare Vehicles";
        String MAP_CARSHARE_SPOTS = "Map Carshare Spots";
        String SPOT_INFO_FRAGMENT = "Spot Info";
        String TUTORIAL_FRAGMENT = "Tutorial";
        // Events
    }

    public interface AnalyticsValues {
        String TUTORIAL_LOGO = " Logo";
        String TUTORIAL_PAGE = " #";
        String SPOT_ID = "spot_id";
        String LOT_ID = "lot_id";
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
