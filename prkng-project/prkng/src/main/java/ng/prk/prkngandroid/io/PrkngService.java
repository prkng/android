package ng.prk.prkngandroid.io;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.AnalyticsEvent;
import ng.prk.prkngandroid.model.AnalyticsQuery;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.ErrorReportData;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.model.UploadImageData;
import ng.prk.prkngandroid.model.UserProfileData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PrkngService {
    String CONTENT_TYPE = "Content-type: application/json";

    // Send analytics event data
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_ANALYTICS_EVENT)
    Object sendEventAnalytics(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body AnalyticsEvent event
    );

    // Send search query data
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_ANALYTICS_SEARCH)
    Object sendSearchAnalytics(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body AnalyticsQuery query
    );

    // Returns coverage area package versions and metadata
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_AREAS)
    Object getAreas(
            @Header(Const.ApiArgs.API_KEY) String apiKey
    );

    // Return carshare lots and data around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_CARSHARE_LOTS)
    Call<PointsGeoJSON> getCarshareLots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) int radius,
            @Query(Const.ApiArgs.CARSHARE_COMPANIES) String companies
    );

    // Return available carshares around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_CARSHARE_VEHICLES)
    Call<PointsGeoJSON> getCarshareVehicles(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) int radius,
            @Query(Const.ApiArgs.CARSHARE_COMPANIES) String companies
    );

    // Get the list of last checkins
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_CHECKINS)
    Object getCheckIns(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LIMIT) int limit
    );

    // Add a new checkin
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_CHECKINS)
    Call<CheckinData> checkin(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Field(Const.ApiArgs.SPOT_ID) long spotId
    );

    // Deactivate an existing checkin
    @Headers({CONTENT_TYPE})
    @DELETE(Const.ApiPaths.DELETE_CHECKIN)
    Call<Void> checkOut(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.CHECKIN_ID) long checkInId
    );

    // Returns coverage area information
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_CITIES)
    Object getCities(
            @Header(Const.ApiArgs.API_KEY) String apiKey
    );

    // Send analytics event data
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_HELLO)
    Call<Void> hello(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Field(Const.ApiArgs.LANG) String lang,
            @Field(Const.ApiArgs.DEVICE_TYPE) String deviceType,
            @Field(Const.ApiArgs.DEVICE_ID) String deviceId
    );

    // Generate an S3 URL for image submission
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_IMAGES)
    Object generateImageUploadPaths(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body UploadImageData uploadImageData
    );

    // Login and receive an API key
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_LOGIN)
    Call<LoginObject> login(
            @Field(Const.ApiArgs.EMAIL) String email,
            @Field(Const.ApiArgs.PASSWORD) String password,
            @Field(Const.ApiArgs.OAUTH_TOKEN) String oauthToken,
            @Field(Const.ApiArgs.OAUTH_TYPE) String oauthType
    );

    // Change an account's password via reset code
    // Used by website only
    // @Headers({CONTENT_TYPE})
    // @POST(Const.ApiPaths.POST_PASSWD_CHANGE)
    // Object changeUserPassword();

    // Register a new account
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_REGISTER)
    Call<LoginObject> registerUser(
            @Field(Const.ApiArgs.NAME) String name,
            @Field(Const.ApiArgs.EMAIL) String email,
            @Field(Const.ApiArgs.PASSWORD) String password,
            @Field(Const.ApiArgs.IMAGE_URL) String imageUrl,
            @Field(Const.ApiArgs.BIRTH_YEAR) String birthYear,
            @Field(Const.ApiArgs.GENDER) String gender
    );

    // Send an account password reset code
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_PASSWD_RESET)
    Call<Void> resetUserPassword(
            @Field(Const.ApiArgs.EMAIL) String gender
    );

    // Return parking lots and garages around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_LOTS)
    Call<PointsGeoJSON> getParkingLots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) int radius
    );

    // Returns the parking lot corresponding to the id
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_LOT)
    Call<PointsGeoJSONFeature> getParkingLotInfo(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.LOT_ID) String lotId
    );

    // Submit a report about incorrect data
    @FormUrlEncoded
    @POST(Const.ApiPaths.POST_REPORT)
    Object reportIncorrectData(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body ErrorReportData errorReportData
    );

    // Returns slots around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_SPOTS)
    Call<LinesGeoJSON> getParkingSpots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) int radius,
            @Query(Const.ApiArgs.PERMITS) String permits,
            @Query(Const.ApiArgs.DURATION) float duration,
            @Query(Const.ApiArgs.CHECKIN_TIMESTAMP) String timestamp,
            @Query(Const.ApiArgs.USE_CARSHARE) boolean useCarshare,
            @Query(Const.ApiArgs.USE_COMPACT) boolean useCompact
    );

    // Returns the parking slot corresponding to the id
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_SPOT)
    Call<LinesGeoJSONFeature> getParkingSpotInfo(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.SPOT_ID) String spotId
    );

    /**
     * Returns the parking slot corresponding to the id
     * If {@code filter} is not `true` then the endpoint will return all rules applicable,
     * regardless of season or permit status.
     * Note that `filter` is true by default, and {@code timestamp} defaults to current server date
     *
     * @param apiKey
     * @param spotId
     * @param useFilter
     * @param timestamp
     * @param permits
     * @return
     */
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_SPOT)
    Call<LinesGeoJSONFeature> getParkingSpotInfo(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.SPOT_ID) String spotId,
            @Path(Const.ApiArgs.USE_FILTER) boolean useFilter,
            @Path(Const.ApiArgs.CHECKIN_TIMESTAMP) String timestamp,
            @Query(Const.ApiArgs.PERMITS) String permits
    );

    // Get information about a user
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_USER_PROFILE)
    Object getUserProfile(
            @Header(Const.ApiArgs.API_KEY) String apiKey
    );

    // Update user profile information
    @FormUrlEncoded
    @PUT(Const.ApiPaths.PUT_USER_PROFILE)
    Object updateUserProfile(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body UserProfileData userProfileData
    );
}
