package ng.prk.prkngandroid.util;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.UnsupportedAreaException;
import ng.prk.prkngandroid.model.CheckinData;

public class MapUtils {
    private final static String TAG = "MapUtils";
    public static final int KILOMETER_IN_METERS = 1000;
    public final static String MARKER_ID_CHECKIN = "checkin_marker";
    public final static String MARKER_ID_SEARCH = "search_marker";

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

    public static LatLngZoom getInitialCenterCoordinates(MapView mapView, Bundle extras) throws UnsupportedAreaException {
        // First, start by checking bundle coordinates (from Intent or savedInstance)
        if (extras != null) {
            final LatLngZoom latLngZoom = getBundleGeoPoint(extras);
            if (latLngZoom != null) {
                return latLngZoom;
            }
        }

        // Second, does user have an active checkin
        final CheckinData checkin = PrkngPrefs.getInstance(mapView.getContext())
                .getCheckinData();
        if (checkin != null) {
            return new LatLngZoom(checkin.getLatLng(),
                    Const.UiConfig.CHECKIN_ZOOM);
        }

        // Third, user's current location
        if (mapView.isMyLocationEnabled()) {
            final Location myLocation = mapView.getMyLocation();
            if (CityBoundsHelper.isValidLocation(mapView.getContext(), myLocation)) {
                return new LatLngZoom(myLocation.getLatitude(),
                        myLocation.getLongitude(),
                        Const.UiConfig.MY_LOCATION_ZOOM);
            } else {
                throw new UnsupportedAreaException();
            }
        }

        // TODO handle selected city

        return null;
    }

    public static LatLngZoom getBundleGeoPoint(@NonNull Bundle extras) {
        final double lat = extras.getDouble(Const.BundleKeys.LATITUDE, Const.UNKNOWN_VALUE);
        final double lng = extras.getDouble(Const.BundleKeys.LONGITUDE, Const.UNKNOWN_VALUE);
        if ((Double.compare(lat, Const.UNKNOWN_VALUE) != 0)
                && (Double.compare(lng, Const.UNKNOWN_VALUE) != 0)) {
            // Mapbox's empty value for zoom is 0.0
            double zoom = extras.getDouble(Const.BundleKeys.ZOOM, 0.0);
            return new LatLngZoom(lat, lng, zoom);
        }

        return null;
    }

    public static void setInitialCenterCoordinates(final MapView mapView, Bundle extras) throws UnsupportedAreaException {
        final long startMillis = System.currentTimeMillis();

        final LatLngZoom initialCoords = MapUtils
                .getInitialCenterCoordinates(mapView, extras);

        if (initialCoords != null) {
            mapView.setLatLng((LatLng) initialCoords);
            mapView.setZoom(initialCoords.getZoom(), true);
        } else if (mapView.isMyLocationEnabled()) {
            mapView.setOnMyLocationChangeListener(new MapView.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (System.currentTimeMillis() - startMillis > DateUtils.SECOND_IN_MILLIS) {
                        mapView.setOnMyLocationChangeListener(null);
                    } else if (CityBoundsHelper.isValidLocation(mapView.getContext(), location)) {
                        mapView.setOnMyLocationChangeListener(null);

                        mapView.setLatLng(new LatLng(
                                location.getLatitude(), location.getLongitude()));
                        mapView.setZoom(Const.UiConfig.MY_LOCATION_ZOOM, true);
                    }
                }
            });
        }
    }

    public static void addCheckinMarkerIfAvailable(MapView mapView, Icon checkinIcon) {
        final CheckinData checkin = PrkngPrefs.getInstance(mapView.getContext()).getCheckinData();
        if (checkin != null) {
            mapView.addMarker(new MarkerOptions()
                    .icon(checkinIcon)
                    .snippet(MARKER_ID_CHECKIN)
                    .position(checkin.getLatLng()));
        }
    }

    public static boolean removeCheckinMarker(MapView mapView) {
        for (Annotation annotation : mapView.getAllAnnotations()) {
            if (annotation instanceof Marker) {
                final Marker marker = (Marker) annotation;
                if (MARKER_ID_CHECKIN.equals(marker.getSnippet())) {
                    mapView.removeMarker(marker);
                    return true;
                }
            }
        }

        return false;
    }

    public static Marker addSearchMarker(MapView mapView, LatLng latLng, Icon searchIcon) {
        return mapView.addMarker(new MarkerOptions()
                        .snippet(MapUtils.MARKER_ID_SEARCH)
                        .position(latLng)
                        .icon(searchIcon)
        );
    }
}
