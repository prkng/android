package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import ng.prk.prkngandroid.Const;

public class CheckinObject {
    private long id;
    private boolean active;
    private String checkinAt;
    private String checkoutAt;
    private String city;
    @SerializedName(Const.ApiArgs.GEO_LAT)
    private double latitude;
    @SerializedName(Const.ApiArgs.GEO_LNG)
    private double longitude;
    @SerializedName(Const.ApiArgs.USER_ID)
    private long userId;

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public String getCheckinAt() {
        return checkinAt;
    }

    public String getCheckoutAt() {
        return checkoutAt;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getUserId() {
        return userId;
    }
}
