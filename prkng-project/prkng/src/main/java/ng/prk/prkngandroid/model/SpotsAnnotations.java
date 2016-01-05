package ng.prk.prkngandroid.model;


import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;
import java.util.List;

import ng.prk.prkngandroid.model.ui.MarkerWrapper;
import ng.prk.prkngandroid.model.ui.PolylineWrapper;

public class SpotsAnnotations {
    private LatLng center;
    private HashMap<PolylineOptions, String> polylines;
    private HashMap<MarkerOptions, String> markers;
    private List<PolylineWrapper> polylinesList;
    private List<MarkerWrapper> markersList;

    public SpotsAnnotations() {
        this.polylines = new HashMap<>();
        this.markers = new HashMap<>();
    }

    public void addPolyline(String featureId, PolylineOptions polyline) {
        this.polylines.put(polyline, featureId);
    }

    public void addPolyline(PolylineWrapper polyline) {
        this.polylinesList.add(polyline);
    }

    public void addMarker(String featureId, MarkerOptions marker) {
        this.markers.put(marker, featureId);
    }

    public void addMarker(MarkerWrapper marker) {
        this.markersList.add(marker);
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
