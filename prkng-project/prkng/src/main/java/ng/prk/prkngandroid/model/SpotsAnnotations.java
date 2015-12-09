package ng.prk.prkngandroid.model;


import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SpotsAnnotations {
    private LatLng center;
    private List<PolylineOptions> polylines;
    private List<MarkerOptions> markers;

    public SpotsAnnotations() {
        this.polylines = new ArrayList<>();
        this.markers = new ArrayList<>();
    }

    public void addPolyline(PolylineOptions polyline) {
        this.polylines.add(polyline);
    }

    public void addMarker(MarkerOptions marker) {
        this.markers.add(marker);
    }

    public List<PolylineOptions> getPolylines() {
        return polylines;
    }

    public List<MarkerOptions> getMarkers() {
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
