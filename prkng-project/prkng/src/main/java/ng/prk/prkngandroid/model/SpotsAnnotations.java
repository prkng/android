package ng.prk.prkngandroid.model;


import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;

public class SpotsAnnotations {
    private LatLng center;
    private HashMap<PolylineOptions, String> polylines;
    private HashMap<MarkerOptions, String> markers;
    public SpotsAnnotations() {
        this.polylines = new HashMap<>();
        this.markers = new HashMap<>();
    }

    public void addPolyline(String featureId, PolylineOptions polyline) {
        this.polylines.put(polyline, featureId);
    }

    public void addMarker(String featureId, MarkerOptions marker) {
        this.markers.put(marker, featureId);
    }

    // TODO verify impact on using complex object as KEY
    public HashMap<PolylineOptions, String> getPolylines() {
        return polylines;
    }
    public HashMap<MarkerOptions, String> getMarkers() {
        return markers;
    }

    public void clearPolylines() {
        this.polylines = null;
    }

    public void clearMarkers() {
        this.markers = null;
    }

    public LatLng getCenterCoordinate() {
        return center;
    }

    public void setCenterCoordinate(LatLng center) {
        this.center = center;
    }
}
