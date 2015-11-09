package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class LatLong extends LatLng {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("long")
    private double longitude;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
