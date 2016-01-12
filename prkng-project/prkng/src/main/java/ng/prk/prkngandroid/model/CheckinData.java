package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Date;

import ng.prk.prkngandroid.Const;

public class CheckinData {
    private long id;
    private boolean active;
    @SerializedName(Const.ApiArgs.CHECKIN_START_TIME)
    private Date checkinAt;
    @SerializedName(Const.ApiArgs.CHECKIN_END_TIME)
    private Date checkoutAt;
    private String city;
    private String address;
    @SerializedName(Const.ApiArgs.GEO_LAT)
    private double latitude;
    @SerializedName(Const.ApiArgs.GEO_LNG)
    private double longitude;
    @SerializedName(Const.ApiArgs.USER_ID)
    private long userId;

    public CheckinData(long id, long checkinAt, long checkoutAt, String address, LatLng latLng) {
        this.id = id;
        this.checkinAt = new Date(checkinAt);
        this.checkoutAt = !Long.valueOf(Const.UNKNOWN_VALUE).equals(checkoutAt) ?
                new Date(checkoutAt) : null;
        this.address = address;
        this.latitude = latLng.getLatitude();
        this.longitude = latLng.getLongitude();
    }

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public long getCheckinAt() {
        return (checkinAt == null) ? Const.UNKNOWN_VALUE : checkinAt.getTime();
    }

    public long getCheckoutAt() {
        return (checkoutAt == null) ? Const.UNKNOWN_VALUE : checkoutAt.getTime();
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public long getUserId() {
        return userId;
    }

    public void setCheckoutAt(Date checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Deprecated
    public long getDuration() {
        // TODO should be getReaminingTime
        return checkoutAt.getTime() - checkinAt.getTime();
    }

    @Override
    public String toString() {
        return "CheckinData{" +
                "id=" + id +
                ", active=" + active +
                ", checkinAt=" + checkinAt +
                ", checkoutAt=" + checkoutAt +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
