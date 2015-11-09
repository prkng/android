package ng.prk.prkngandroid;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Const {

    public interface UiConfig {
        double DEFAULT_ZOOM = 15;
        double MIN_ZOOM = 15;
        double SMALL_BUTTONS_ZOOM = 17;
        double BIG_BUTTONS_ZOOM = 18;
        double MY_LOCATION_ZOOM = 17;
        double MIN_UPDATE_DISTACE = 25; // Metres
        double MONTREAL_NATURAL_NORTH_ROTATION = -34f;
        LatLng MONTREAL_LAT_LNG = new LatLng(45.5d, -73.666667d);
    }
    public interface ApiArgs {
        String API_KEY = "X-API-KEY";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String EVENT = "event";
        String QUERY = "query";
        String RADIUS = "radius";
        String CARSHARE_COMPANY = "company";
        String LIMIT = "limit";
        String CITY = "city";
        String SPOT_ID = "slot_id";
        String CHECKIN_ID = "id";
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
    }

    public interface ApiPaths {
        String PROTOCOL = "https://";
        String HOST_PROD = "api.prk.ng";
        String HOST_DEV = "test.prk.ng";
        String BASE_URL = PROTOCOL + HOST_DEV;
        String VERSION = "/v1";

        String POST_ANALYTICS_EVENT = VERSION + "/analytics/event";
        String POST_ANALYTICS_SEARCH = VERSION + "/analytics/search";
        String GET_AREAS = VERSION + "/areas";
        String GET_CARSHARE_LOTS = VERSION + "/carshare_lots";
        String GET_CARSHARE_CARS = VERSION + "/carshares";
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
        String POST_REPORT = VERSION + "/reports";
        String GET_SPOTS = VERSION + "/slots";
        String GET_SPOT = VERSION + "/slots/{" + ApiArgs.SPOT_ID + "}";
        String GET_USER_PROFILE = VERSION + "/user/profile";
        String PUT_USER_PROFILE = VERSION + "/user/profile";
    }

    public interface FragmentTags {
        String MAP = "f_map";
    }

    public interface RequestCodes {
        int PERMISSION_ACCESS_LOCATION = 10;
        int PERMISSION_ACCESS_FINE_LOCATION = 20;
        int PERMISSION_ACCESS_COARSE_LOCATION = 30;
    }

    public static String LINE_SEPARATOR = System.getProperty("line.separator");
}
