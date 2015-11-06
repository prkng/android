package ng.prk.prkngandroid.io;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.PointsGeoJSON;
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
     * @param httpLoggin Enable verbose
     * @return PrkngService
     */
    private static PrkngService getService(boolean httpLoggin) {
        final OkHttpClient client = new OkHttpClient();
        if (httpLoggin) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            client.interceptors().add(interceptor);
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.ApiPaths.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(PrkngService.class);
    }

    public static LoginObject loginEmail(PrkngService service, String email, String password) {
        return login(service, email, password, null, null);
    }

    public static LoginObject loginFacebook(PrkngService service, String token) {
        return login(service, null, null, token, Const.ApiValues.OAUTH_TYPE_FACEBOOK);
    }

    public static LoginObject loginGoogleplus(PrkngService service, String token) {
        return login(service, null, null, token, Const.ApiValues.OAUTH_TYPE_GOOGLEPLUS);
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

    //    Returns slots around the point defined by (x, y)
    public static LinesGeoJSON getParkingSpots(PrkngService service, String apkKey, double latitude, double longitude) {
        try {
            final Response<LinesGeoJSON> response = service
                    .getParkingSpots(apkKey,
                            latitude,
                            longitude,
                            Const.ApiValues.DEFAULT_RADIUS,
                            Const.ApiValues.DEFAULT_DURATION,
                            null)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //    Returns slots around the point defined by (x, y)
    public static PointsGeoJSON getParkingLots(PrkngService service, String apkKey, double latitude, double longitude) {
        try {
            final Response<PointsGeoJSON> response = service
                    .getParkingLots(apkKey,
                            latitude,
                            longitude)
                    .execute();
            if (response != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
