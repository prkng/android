package ng.prk.prkngandroid.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class MapAssets {
    private int lineWidth;
    private int lineColorFree;
    private int lineColorPaid;
    private int lineColorSelected;
    private String[] carshareLotsCompanies;
    private String[] carshareVehiclesCompanies;
    private Icon markerIconFree;
    private Icon markerIconPaid;
    private Icon markerIconSelected;
    private Icon markerIconTransparent;
    private Icon markerIconCarshareCommunauto;
    private Icon markerIconCarshareAutomobile;
    private Icon markerIconCarshareCar2go;
    private Icon markerIconCarshareZipcar;

    public MapAssets(MapView mapView) {
        final Context context = mapView.getContext();
        markerIconFree = mapView.getIconFactory().fromResource(R.drawable.ic_spot_free);
        markerIconPaid = mapView.getIconFactory().fromResource(R.drawable.ic_spot_paid);
        markerIconSelected = mapView.getIconFactory().fromResource(R.drawable.ic_spot_selected);
        markerIconTransparent = mapView.getIconFactory().fromDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_spot_transparent));

        markerIconCarshareCommunauto = mapView.getIconFactory().fromResource(R.drawable.ic_maps_carshare_communauto);
        markerIconCarshareAutomobile = mapView.getIconFactory().fromResource(R.drawable.ic_maps_carshare_automobile);
        markerIconCarshareCar2go = mapView.getIconFactory().fromResource(R.drawable.ic_maps_carshare_car2go);
        markerIconCarshareZipcar = mapView.getIconFactory().fromResource(R.drawable.ic_maps_carshare_zipcar);

        lineColorFree = ContextCompat.getColor(context, R.color.map_line_free_spot);
        lineColorPaid = ContextCompat.getColor(context, R.color.map_line_paid_spot);
        lineColorSelected = ContextCompat.getColor(context, R.color.map_line_selected_spot);
        lineWidth = context.getResources().getDimensionPixelSize(R.dimen.map_line_width);
        carshareLotsCompanies = context.getResources().getStringArray(R.array.carshare_lots);
        carshareVehiclesCompanies = context.getResources().getStringArray(R.array.carshare_vehicles);
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public int getLineColorFree() {
        return lineColorFree;
    }

    public int getLineColorPaid() {
        return lineColorPaid;
    }

    public int getLineColorSelected() {
        return lineColorSelected;
    }

    public Icon getMarkerIconFree() {
        return markerIconFree;
    }

    public Icon getMarkerIconPaid() {
        return markerIconPaid;
    }

    public Icon getMarkerIconSelected() {
        return markerIconSelected;
    }

    public Icon getMarkerIconTransparent() {
        return markerIconTransparent;
    }

    public String[] getCarshareLotsCompanies() {
        return carshareLotsCompanies;
    }

    public String[] getCarshareVehiclesCompanies() {
        return carshareVehiclesCompanies;
    }

    public Icon getCarshareVehicleMarkerIcon(String company) {
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
