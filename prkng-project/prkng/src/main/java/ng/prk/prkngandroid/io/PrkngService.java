package ng.prk.prkngandroid.io;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.AnalyticsEvent;
import ng.prk.prkngandroid.model.AnalyticsQuery;
import ng.prk.prkngandroid.model.CheckInData;
import ng.prk.prkngandroid.model.DeviceData;
import ng.prk.prkngandroid.model.ErrorReportData;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.PasswordResetData;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.UploadImageData;
import ng.prk.prkngandroid.model.UserProfileData;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

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
    Object getCarshareLots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) float radius,
            @Query(Const.ApiArgs.CARSHARE_COMPANY) float carshareCompany
    );

    // Return available carshares around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_CARSHARE_CARS)
    Object getCarshareCars(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) float radius,
            @Query(Const.ApiArgs.CARSHARE_COMPANY) float carshareCompany
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
    Object checkIn(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body CheckInData checkInData
    );

    // Deactivate an existing checkin
    @Headers({CONTENT_TYPE})
    @DELETE(Const.ApiPaths.DELETE_CHECKIN)
    Object checkOut(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.CHECKIN_ID) float checkInId
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
    Object hello(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body DeviceData deviceData
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
    Object resetUserPassword(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Body PasswordResetData passwordResetData
    );

    // Return parking lots and garages around the point defined by (x, y)
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_LOTS)
    Call<PointsGeoJSON> getParkingLots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude
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
            @Query(Const.ApiArgs.DURATION) float duration,
            @Query(Const.ApiArgs.CHECKIN_TIMESTAMP) String timestamp,
            @Query(Const.ApiArgs.USE_CARSHARE) boolean useCarshare,
            @Query(Const.ApiArgs.USE_COMPACT) boolean useCompact
    );

    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_SPOTS)
    @Deprecated
    Call<LinesGeoJSON> getParkingSpots(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Query(Const.ApiArgs.LATITUDE) double latitude,
            @Query(Const.ApiArgs.LONGITUDE) double longitude,
            @Query(Const.ApiArgs.RADIUS) int radius,
            @Query(Const.ApiArgs.DURATION) float duration,
            @Query(Const.ApiArgs.CHECKIN_TIMESTAMP) String timestamp,
            @Query(Const.ApiArgs.USE_CARSHARE) String useCarshare,
            @Query(Const.ApiArgs.USE_COMPACT) String useCompact
    );

    // Returns the parking slot corresponding to the id
    @Headers({CONTENT_TYPE})
    @GET(Const.ApiPaths.GET_SPOT)
    Object getParkingSpotInfo(
            @Header(Const.ApiArgs.API_KEY) String apiKey,
            @Path(Const.ApiArgs.SPOT_ID) float spotId
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
