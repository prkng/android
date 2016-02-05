package ng.prk.prkngandroid.model.mapbox;

import android.text.TextUtils;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

import ng.prk.prkngandroid.model.base.SearchItem;

public class Feature extends SearchItem {


    //    String id;
    //    String type;
    String text;
    String place_name;
    float relevance;
    Properties properties;
    List<Double> center;
    //    Object geometry;
    //    List<Object> context;
    // Additional property, needs to be initialized by Adapter
    double distance;

    public String getText() {
        return text;
    }

    public String getPlaceName() {
        return place_name;
    }

    public float getRelevance() {
        return relevance;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Double> getCenter() {
        return center;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public String getAddress() {
        if (properties != null && !TextUtils.isEmpty(properties.getAddress())) {
            return properties.getAddress();
        } else {
            return getPlaceName();
        }
    }

    @Override
    public LatLng getLatLng() {
        return (center == null) ? null :
                new LatLng(center.get(1), center.get(0));
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "text='" + text + '\'' +
                ", place_name='" + place_name + '\'' +
                ", relevance=" + relevance +
                ", center=" + center +
                ", distance=" + distance +
                '}';
    }
}
