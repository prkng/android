package ng.prk.prkngandroid.model.base;

import com.mapbox.mapboxsdk.geometry.LatLng;

public abstract class SearchItem implements Comparable<SearchItem> {
    protected final String ADDRESS_SEPARATOR = ", ";

    public abstract String getName();

    public abstract String getAddress();

    public abstract LatLng getLatLng();

    public abstract double getDistance();

    public boolean isMapbox() {
        return false;
    }

    public boolean isFoursquare() {
        return false;
    }

    public double distanceTo(SearchItem another) {
        return getLatLng().distanceTo(another.getLatLng());
    }

    @Override
    public int compareTo(SearchItem another) {
        return Double.compare(this.getDistance(), another.getDistance());
    }
}
