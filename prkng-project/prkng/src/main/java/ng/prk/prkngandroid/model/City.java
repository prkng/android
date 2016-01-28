package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.MapUtils;

public class City implements Comparable<City> {
    private int id;
    private String name;

    @SerializedName(Const.ApiArgs.DISPLAY_NAME)
    private String areaName;

    @SerializedName(Const.ApiArgs.GEO_LAT)
    private double latitude;

    @SerializedName(Const.ApiArgs.GEO_LNG)
    private double longitude;
    @SerializedName(Const.ApiArgs.URBAN_AREA_RADIUS)
    private int areaRadius; // km
    private double distanceTo;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAreaName() {
        return areaName;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getAreaRadius() {
        return areaRadius * MapUtils.KILOMETER_IN_METERS;
    }

    public double getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(double distanceTo) {
        this.distanceTo = distanceTo;
    }

    public boolean containsInRadius(LatLng point) {
        return Double.compare(point.distanceTo(getLatLng()), getAreaRadius()) <= 0;
    }

    @Override
    public String toString() {
        return "City{" +
                "distanceTo=" + distanceTo +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", areaName='" + areaName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", areaRadius=" + areaRadius +
                '}';
    }

    @Override
    public int compareTo(@NonNull City another) {
        return Double.compare(this.distanceTo, another.distanceTo);
    }
}
