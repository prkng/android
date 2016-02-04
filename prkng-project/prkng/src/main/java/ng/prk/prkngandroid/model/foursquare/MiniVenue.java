package ng.prk.prkngandroid.model.foursquare;


import android.text.TextUtils;

import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.model.base.SearchItem;

public class MiniVenue extends SearchItem {
    String id;
    String name;
    Location location;
//    Object categories;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        if (location == null) {
            return null;
        }

        if (TextUtils.isEmpty(location.getCity())) {
            return location.getAddress();
        } else if (TextUtils.isEmpty(location.getAddress())) {
            return location.getCity();
        } else {
            return location.getAddress()
                    + ADDRESS_SEPARATOR
                    + location.getCity();
        }
    }

    @Override
    public LatLng getLatLng() {
        return location == null ? null :
                location.getLatLng();
    }

    @Override
    public double getDistance() {
        if (location == null) {
            return Double.MAX_VALUE;
        } else {
            final int distance = location.getDistance();
            return (distance > 0) ? distance : Double.MAX_VALUE;
        }
    }

    @Override
    public boolean isFoursquare() {
        return true;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "MiniVenue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
