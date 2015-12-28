package ng.prk.prkngandroid.model.ui;

import com.mapbox.mapboxsdk.annotations.Icon;

public class SelectedFeature {
    private String id;
    private Icon markerIcon;
    private int polylineColor;

    public SelectedFeature(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Icon getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(Icon markerIcon) {
        this.markerIcon = markerIcon;
    }

    public int getPolylineColor() {
        return polylineColor;
    }

    public void setPolylineColor(int polylineColor) {
        this.polylineColor = polylineColor;
    }
}
