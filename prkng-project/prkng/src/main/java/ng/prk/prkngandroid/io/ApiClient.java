package ng.prk.prkngandroid.io;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ng.prk.prkngandroid.BuildConfig;
import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.util.ArrayUtils;
import ng.prk.prkngandroid.util.CalendarUtils;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ApiClient {
    private final static String TAG = "ApiClient";

    public static PrkngService getService() {
        return getService(false);
    }

    @Deprecated
    public static PrkngService getServiceLog() {
        return getService(true);
    }

    /**
     * Get the Prkng API service
     *
     * @param httpLogging Enable verbose
     * @return PrkngService
     */
    private static PrkngService getService(boolean httpLogging) {
        final OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        if (httpLogging) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);
            client.interceptors().add(new HttpErrorInterceptor());
        }

        final Gson gson = new GsonBuilder()
                .setDateFormat(CalendarUtils.DATE_FORMAT_ISO_8601)
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
//                .addConverterFactory(LatLngConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(PrkngService.class);
    }

    private final static Callback CALLBACK = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {
        }

        @Override
        public void onFailure(Throwable t) {
            Log.v(TAG, "onFailure");
            t.printStackTrace();
        }
    };

    @WorkerThread
    public static LoginObject loginEmail(PrkngService service, String email, String password) {
        return login(service, email, password, null, null);
    }

    @WorkerThread
    public static LoginObject loginSocial(PrkngService service, String token, String type) {
        return login(service, null, null, token, type);
    }

    /**
     * Login and receive an API key
     *
     * @param service
     * @param email    user email (for email logins only)
     * @param password user password (for email logins only)
     * @param token    OAuth2 user access token (for facebook/google logins only)
     * @param type     login type (facebook, google, etc). required for OAuth2
     * @return
     */
    @WorkerThread
    private static LoginObject login(PrkngService service, String email, String password, String token, String type) {
        try {
            final Response<LoginObject> response = service
                    .login(email, password, token, type)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Register a new account
     *
     * @param service
     * @param name
     * @param email
     * @param password
     * @return
     */
    @WorkerThread
    public static LoginObject registerUser(PrkngService service, String name, String email, String password) {
        try {
            final Response<LoginObject> response = service
                    .registerUser(name, email, password, null, null, null)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns slots around the point defined by (x, y)
     *
     * @param service
     * @param apiKey
     * @param latitude
     * @param longitude
     * @param radius
     * @param permits
     * @param duration
     * @return
     */
    @WorkerThread
    public static LinesGeoJSON getParkingSpots(PrkngService service, String apiKey, double latitude, double longitude, int radius, String[] permits, float duration) {
        try {
            final String timestamp = CalendarUtils.getIsoTimestamp();
            final Response<LinesGeoJSON> response = service
                    .getParkingSpots(apiKey,
                            latitude,
                            longitude,
                            radius,
                            ArrayUtils.join(permits),
                            duration,
                            timestamp,
                            false,
                            true)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns Carshare slots around the point defined by (x, y)
     *
     * @param service
     * @param apiKey
     * @param latitude
     * @param longitude
     * @param radius
     * @param duration
     * @return
     */
    @WorkerThread
    public static LinesGeoJSON getCarshareParkingSpots(PrkngService service, String apiKey, double latitude, double longitude, int radius, float duration) {
        try {
            final String timestamp = CalendarUtils.getIsoTimestamp();
            final Response<LinesGeoJSON> response = service
                    .getParkingSpots(apiKey,
                            latitude,
                            longitude,
                            radius,
                            null,
                            duration,
                            timestamp,
                            true,
                            true)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns slots around the point defined by (x, y)
     *
     * @param service
     * @param apiKey
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    @WorkerThread
    public static PointsGeoJSON getParkingLots(PrkngService service, String apiKey, double latitude, double longitude, int radius) {
        try {
            final Response<PointsGeoJSON> response = service
                    .getParkingLots(apiKey,
                            latitude,
                            longitude,
                            radius)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @WorkerThread
    public static PointsGeoJSON getCarshareLots(PrkngService service, String apiKey, double latitude, double longitude, int radius) {
        return getCarshareLots(service, apiKey, latitude, longitude, radius, null);
    }

    /**
     * Return carshare lots and data around the point defined by (x, y)
     *
     * @param service
     * @param apiKey
     * @param latitude
     * @param longitude
     * @param radius
     * @param companies
     * @return
     */
    @WorkerThread
    public static PointsGeoJSON getCarshareLots(PrkngService service, String apiKey, double latitude, double longitude, int radius, String[] companies) {
        try {
            final Response<PointsGeoJSON> response = service
                    .getCarshareLots(apiKey,
                            latitude,
                            longitude,
                            radius,
                            ArrayUtils.join(companies))
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @WorkerThread
    public static PointsGeoJSON getCarshareVehicles(PrkngService service, String apiKey, double latitude, double longitude, int radius) {
        return getCarshareVehicles(service, apiKey, latitude, longitude, radius, null);
    }

    /**
     * Return available carshares around the point defined by (x, y)
     *
     * @param service
     * @param apiKey
     * @param latitude
     * @param longitude
     * @param radius
     * @param companies
     * @return
     */
    @WorkerThread
    public static PointsGeoJSON getCarshareVehicles(PrkngService service, String apiKey, double latitude, double longitude, int radius, String[] companies) {
        try {
            final Response<PointsGeoJSON> response = service
                    .getCarshareVehicles(apiKey,
                            latitude,
                            longitude,
                            radius,
                            ArrayUtils.join(companies))
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @WorkerThread
    public static LinesGeoJSONFeature getParkingSpotInfo(PrkngService service, String apiKey, String spotId) {
        try {
            final Response<LinesGeoJSONFeature> response = service
                    .getParkingSpotInfo(apiKey, spotId)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @WorkerThread
    public static PointsGeoJSONFeature getParkingLotInfo(PrkngService service, String apiKey, String lotId) {
        try {
            final Response<PointsGeoJSONFeature> response = service
                    .getParkingLotInfo(apiKey, lotId)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void hello(PrkngService service, String apiKey, String deviceId, Callback<Void> cb) {
        final String lang = Locale.getDefault().getLanguage();

        service
                .hello(apiKey,
                        lang,
                        Const.ApiValues.DEVICE_TYPE_ANDROID,
                        deviceId)
                .enqueue(cb != null ? cb : CALLBACK);
    }

    @WorkerThread
    public static void resetUserPassword(PrkngService service, String email) {
        try {
            service
                    .resetUserPassword(email)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkin(PrkngService service, String apiKey, String slotId, Callback<Void> cb) {
        try {
            service
                    .checkin(apiKey,
                            Long.valueOf(slotId))
                    .enqueue(cb != null ? cb : CALLBACK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            CALLBACK.onFailure(e);
        }
    }

    public static void checkout(PrkngService service, String apiKey, long checkinId, Callback<CheckinData> cb) {
        try {
            service
                    .checkOut(apiKey,
                            checkinId)
                    .enqueue(cb != null ? cb : CALLBACK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            CALLBACK.onFailure(e);
        }
    }
}
