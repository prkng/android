package ng.prk.prkngandroid.util;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

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

    public static Annotation getNearestAnnotation(@NonNull LatLng point, @NonNull List<Annotation> annotations) {
        Annotation nearestAnnot = null;
        double shortestDist = Double.MAX_VALUE;
        for (Annotation a : annotations) {
            if (a instanceof Marker) {
                final Marker m = (Marker) a;
                final double distance = m.getPosition().distanceTo(point);

                if (nearestAnnot == null) {
                    nearestAnnot = m;
                    shortestDist = distance;
                } else if (Double.compare(distance, shortestDist) < 0) {
                    nearestAnnot = m;
                    shortestDist = distance;
                }
            } else if (a instanceof Polyline) {
                final Polyline p = (Polyline) a;

                for (LatLng latLng : p.getPoints()) {
                    final double distance = latLng.distanceTo(point);

                    if (nearestAnnot == null) {
                        nearestAnnot = p;
                        shortestDist = distance;
                    } else if (Double.compare(distance, shortestDist) < 0) {
                        nearestAnnot = p;
                        shortestDist = distance;
                    }
                }
            }
        }

        return nearestAnnot;
    }

    public static double distanceTo(@NonNull LatLng point, @NonNull Annotation annotation) {
        if (annotation instanceof Marker) {
            return point.distanceTo(((Marker) annotation).getPosition());
        } else if (annotation instanceof Polyline) {
            double minDistance = Double.MAX_VALUE;
            for (LatLng latLng : ((Polyline) annotation).getPoints()) {
                minDistance = Math.min(minDistance, latLng.distanceTo(point));
            }
            return minDistance;
        }

        return Double.MAX_VALUE;
    }
}
