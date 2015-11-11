package ng.prk.prkngandroid.model;


import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class SpotsAnnotations {
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
}
