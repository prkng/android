package ng.prk.prkngandroid.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeoJSONFeatureProperties {
    private List<LatLong> button_locations;
    private boolean compact;
    private String restrict_typ;
    private List<String> restrict_types;
    private String way_name;

    public List<LatLng> getButtonLocations() {
        if (button_locations != null) {
            List<LatLng> buttons = new ArrayList<>();
            for (LatLong latLon : button_locations) {
                buttons.add(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
            }
            return buttons;
        }
        return null;
    }

    public boolean isCompact() {
        return compact;
    }

    public String getRestrictType() {
        return restrict_typ;
    }

    public List<String> getRestrictTypes() {
        return restrict_types;
    }

    public String getWayName() {
        return way_name;
    }
}
