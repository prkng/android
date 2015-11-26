package ng.prk.prkngandroid.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.Const;

public class GeoJSONFeatureProperties {
    /**
     * Spots
     */
    private List<LatLong> button_locations;
    private boolean compact;
    private List<String> restrict_types;
    private String way_name;
    private List<SpotRule> rules;

    /**
     * Lots
     */
    private String name;
    private String address;

    /**
     * Carshare vehicles
     */
    private String company;

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

    public List<String> getRestrictTypes() {
        return restrict_types;
    }

    public String getWayName() {
        return way_name;
    }

    public SpotRules getRules() {
        return new SpotRules(rules);
    }

    public boolean isTypePaid() {
        return restrict_types != null &&
                restrict_types.contains(Const.ApiValues.SPOT_TYPE_PAID);
    }

    public String getAddress() {
        return address;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }
}
