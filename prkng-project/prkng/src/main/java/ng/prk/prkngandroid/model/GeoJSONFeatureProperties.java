package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;
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
    private LotAgendaRaw agenda;
    private LotAttrs attrs;
    private Integer available;
    private Integer capacity;
    private String city;
    private String operator;
    @SerializedName(Const.ApiArgs.PARTNER_NAME)
    private String partnerName;
    @SerializedName(Const.ApiArgs.STREET_VIEW)
    private StreetView streetView;


    /**
     * Carshare vehicles
     */
    private String company;
    private Integer fuel;
    @SerializedName(Const.ApiArgs.PARTNER_ID)
    private String partnerId;

    /**
     * Supported Areas
     */
    @SerializedName(Const.ApiArgs.AREA_NAME)
    private String areaName;

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

    public SpotRules getSpotRules() {
        return rules == null ? null : new SpotRules(rules);
    }

    public boolean isTypePaid() {
        return restrict_types != null &&
                restrict_types.contains(Const.ApiValues.SPOT_TYPE_PAID);
    }

    public String getAddress() {
        return address;
    }

    public LotAgenda getAgenda() {
        return agenda == null ? null : new LotAgenda(agenda);
    }

    public LotAttrs getAttrs() {
        return attrs;
    }

    public int getAvailable() {
        return available == null ? Const.UNKNOWN_VALUE : available;
    }

    public int getCapacity() {
        return capacity == null ? Const.UNKNOWN_VALUE : capacity;
    }

    public int getAvailableOrCapacity() {
        return available == null ? Const.UNKNOWN_VALUE : capacity;
    }

    public String getCity() {
        return city;
    }

    public String getOperator() {
        return operator;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public StreetView getStreetView() {
        return streetView;
    }

    public String getCompany() {
        return company;
    }

    public int getFuel() {
        return fuel == null ? Const.UNKNOWN_VALUE : fuel;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getName() {
        return name;
    }

    public String getAreaName() {
        return areaName;
    }
}
