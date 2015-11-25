package ng.prk.prkngandroid.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.Sprite;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class MapAssets {
    private int lineColorFree;
    private int lineWidth;
    private int lineColorPaid;
    private String[] carshareLotsCompanies;
    private String[] carshareVehiclesCompanies;
    private Sprite markerIconFree;
    private Sprite markerIconPaid;
    private Sprite markerIconTransparent;
    private Sprite markerIconCarshareCommunauto;
    private Sprite markerIconCarshareAutomobile;
    private Sprite markerIconCarshareCar2go;
    private Sprite markerIconCarshareZipcar;

    public MapAssets(MapView mapView) {
        final Context context = mapView.getContext();
        markerIconPaid = mapView.getSpriteFactory().fromResource(R.drawable.ic_spot_paid);
        markerIconFree = mapView.getSpriteFactory().fromResource(R.drawable.ic_spot_free);
        markerIconTransparent = mapView.getSpriteFactory().fromDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_spot_transparent));

        markerIconCarshareCommunauto = mapView.getSpriteFactory().fromResource(R.drawable.ic_maps_carshare_communauto);
        markerIconCarshareAutomobile = mapView.getSpriteFactory().fromResource(R.drawable.ic_maps_carshare_automobile);
        markerIconCarshareCar2go = mapView.getSpriteFactory().fromResource(R.drawable.ic_maps_carshare_car2go);
        markerIconCarshareZipcar = mapView.getSpriteFactory().fromResource(R.drawable.ic_maps_carshare_zipcar);

        lineColorPaid = ContextCompat.getColor(context, R.color.map_line_paid_spot);
        lineColorFree = ContextCompat.getColor(context, R.color.map_line_free_spot);
        lineWidth = context.getResources().getDimensionPixelSize(R.dimen.map_line_width);
        carshareLotsCompanies = context.getResources().getStringArray(R.array.carshare_lots);
        carshareVehiclesCompanies = context.getResources().getStringArray(R.array.carshare_vehicles);
    }

    public int getLineColorFree() {
        return lineColorFree;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public int getLineColorPaid() {
        return lineColorPaid;
    }

    public Sprite getMarkerIconFree() {
        return markerIconFree;
    }

    public Sprite getMarkerIconPaid() {
        return markerIconPaid;
    }

    public Sprite getMarkerIconTransparent() {
        return markerIconTransparent;
    }

    public String[] getCarshareLotsCompanies() {
        return carshareLotsCompanies;
    }

    public String[] getCarshareVehiclesCompanies() {
        return carshareVehiclesCompanies;
    }

    public Sprite getCarshareVehicleMarkerIcon(String company) {
        switch (company) {
            case Const.ApiValues.CARSHARE_COMPANY_COMMUNAUTO:
                return markerIconCarshareCommunauto;
            case Const.ApiValues.CARSHARE_COMPANY_AUTOMOBILE:
                return markerIconCarshareAutomobile;
            case Const.ApiValues.CARSHARE_COMPANY_CAR2GO:
                return markerIconCarshareCar2go;
            case Const.ApiValues.CARSHARE_COMPANY_ZIPCAR:
                return markerIconCarshareZipcar;
        }
        return null;
    }
}
