package ng.prk.prkngandroid.model.ui;

import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;

public class PolylineWrapper extends AnnotationWrapper {
    private PolylineOptions polylineOptions;
    private Polyline polyline;

    public PolylineWrapper(String featureId, String title, int type) {
        super(featureId, title, type);
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

}
