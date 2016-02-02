package ng.prk.prkngandroid.model;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;

public class MapGeometry extends LatLngZoom {
    private final static float RADIUS_FIX = 1.2f;
    private int radius;

    public MapGeometry() {
    }

    public MapGeometry(double latitude, double longitude, double zoom) {
        super(latitude, longitude, zoom);
    }

    public MapGeometry(LatLng latLng, double zoom) {
        super(latLng, zoom);
    }

    public int getRadius() {
        return radius;
    }

    public int getLargeRadius() {
        return (int) Math.ceil(RADIUS_FIX * radius);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double setZoomAndRadius(double zoom, LatLng northWest) {
//        if (Double.compare(getZoom(), zoom) != 0 || radius == 0)
        this.radius = (int) Math.ceil(distanceTo(northWest));
        setZoom(zoom);

        return radius;
    }
}
