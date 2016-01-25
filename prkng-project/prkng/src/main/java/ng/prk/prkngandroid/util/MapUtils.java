package ng.prk.prkngandroid.util;

import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.Const;

public class MapUtils {

    public static double getMinZoomPerType(int type) {
        switch (type) {
            case Const.MapSections.OFF_STREET:
                return Const.UiConfig.LOTS_MIN_ZOOM;
            case Const.MapSections.CARSHARE_SPOTS:
            case Const.MapSections.ON_STREET:
                return Const.UiConfig.SPOTS_MIN_ZOOM;
            case Const.MapSections.CARSHARE_VEHICLES:
                return Const.UiConfig.CARSHARE_VEHICLES_MIN_ZOOM;
        }

        return Const.UiConfig.DEFAULT_ZOOM;
    }

    public static boolean isMinZoom(double zoom, int type) {
        final double minZoom = getMinZoomPerType(type);

        return Double.compare(minZoom, zoom) <= 0;
    }

    public static MarkerOptions extractMarkerOptions(Marker marker) {
        final MarkerOptions options = new MarkerOptions();
        if (marker != null) {
            options.position(marker.getPosition())
                    .icon(marker.getIcon())
                    .title(marker.getTitle())
                    .snippet(marker.getSnippet());
        }

        return options;
    }

    public static PolylineOptions extractPolylineOptions(Polyline polyline) {
        final PolylineOptions options = new PolylineOptions();
        if (polyline != null) {
            options.addAll(polyline.getPoints())
                    .width(polyline.getWidth())
                    .color(polyline.getColor());
        }

        return options;
    }

    public static void removeAllAnnotations(MapView mapView) {
        for (Annotation annot : mapView.getAllAnnotations()) {
            if (annot instanceof Polyline || annot instanceof Marker) {
                mapView.removeAnnotation(annot);
            }
        }
    }

    public static void showSupportedArea(MapView mapView) {
        mapView.addPolygons(
                CityBoundsHelper.getAreaPolygonOptions(mapView.getContext(), mapView.getLatLng()));

    }
}
