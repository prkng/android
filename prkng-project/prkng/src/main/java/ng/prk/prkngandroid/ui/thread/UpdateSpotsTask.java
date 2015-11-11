package ng.prk.prkngandroid.ui.thread;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.SpotsAnnotations;

public class UpdateSpotsTask extends AsyncTask<MapGeometry, Void, SpotsAnnotations> {
    private final static String TAG = "UpdateSpotsTask";

    private MapTaskListener listener;
    private MapAssets mapAssets;
    private MapView vMap;
    private long startTime;
    private String mApiKey;

    public interface MapTaskListener {
        void onPreExecute();

        void onPostExecute();
    }

    public static UpdateSpotsTask create() {
        // Initialize an empty instance
        return new UpdateSpotsTask(null, null, null);
    }

    public UpdateSpotsTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        this.vMap = mapView;
        this.mapAssets = mapAssets;
        this.listener = listener;
    }

    @Deprecated
    private LatLng mCenterLatLng;

    /**
     * Display the progressbar, before processing API data
     */
    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    /**
     * Validate if the map's zoomLevel allows showing Markers
     *
     * @return
     */
    private boolean hasVisibleMarkers() {
        return Double.compare(Const.UiConfig.SMALL_BUTTONS_ZOOM, vMap.getZoomLevel()) <= 0;
    }

    /**
     * Download API data and prepare map annotations
     *
     * @param params
     * @return
     */
    @Override
    protected SpotsAnnotations doInBackground(MapGeometry... params) {
        Log.v(TAG, "doInBackground");
        startTime = System.currentTimeMillis();
        final MapGeometry mapGeometry = params[0];

        final PrkngService service = ApiClient.getServiceLog();

        final SpotsAnnotations spotsAnnotations = new SpotsAnnotations();
        try {
            if (mApiKey == null || mApiKey.isEmpty()) {
                LoginObject loginObject = ApiClient
                        .loginEmail(
                                service,
                                "mudar@prk.ng",
                                "mudar123");
                Log.v(TAG, "name = " + loginObject.getName() + " email = " + loginObject.getEmail());
                Log.v(TAG, "mApiKey = " + loginObject.getApikey());
                mApiKey = loginObject.getApikey();
            }

            if (mApiKey != null && mapGeometry != null) {
                // Get API data
                final LinesGeoJSON spots = ApiClient.getParkingSpots(service,
                        mApiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius(),
                        Const.ApiValues.DEFAULT_DURATION
                );

                mCenterLatLng = mapGeometry;
                // Prepare map annotations: Polylines and Markers
                final List<LinesGeoJSONFeature> spotsFeatures = spots.getFeatures();
                for (LinesGeoJSONFeature feature : spotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    List<List<Double>> coords = feature.getGeometry().getCoordinates();
                    LatLng[] pointsArray = new LatLng[coords.size()];
                    int i = 0;
                    for (List<Double> latLng : coords) {
                        pointsArray[i++] = new LatLng(new LatLng(latLng.get(1), latLng.get(0)));
                    }
                    final PolylineOptions polylineOptions = new PolylineOptions()
                            .add(pointsArray)
                            .width(mapAssets.getLineWidth())
                            .color(properties.isTypePaid() ? mapAssets.getLineColorPaid() : mapAssets.getLineColorFree());
                    spotsAnnotations.addPolyline(polylineOptions);

                    if (hasVisibleMarkers()) {
                        List<LatLng> buttons = properties.getButtonLocations();
                        for (LatLng buttonLatLng : buttons) {
                            String snippet = "ID: " + feature.getId();
                            if (properties.getRestrictType() != null) {
                                snippet += Const.LINE_SEPARATOR + "Restriction: " + properties.getRestrictType();
                            }
                            final MarkerOptions markerOptions = new MarkerOptions()
                                    .position(buttonLatLng)
                                    .title(properties.getWayName())
                                    .snippet(snippet)
                                    .icon(properties.isTypePaid() ? mapAssets.getMarkerIconPaid() : mapAssets.getMarkerIconFree());

                            spotsAnnotations.addMarker(markerOptions);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return spotsAnnotations;
    }

    /**
     * Replace attributions and hide progressbar
     *
     * @param spots
     */
    @Override
    protected void onPostExecute(SpotsAnnotations spots) {
        Log.v(TAG, "onPostExecute");
        if (isCancelled() || vMap == null) {
            return;
        }

        try {
            if (spots != null) {
                Log.v(TAG, "removeAllAnnotations");
                vMap.removeAllAnnotations();

                Log.v(TAG, "addPolylines");
                vMap.addPolylines(spots.getPolylines());

                // Markers must be added after Polylines to show the dot above the line (z-order)
                if (hasVisibleMarkers()) {
                    Log.v(TAG, "addMarkers");
                    vMap.addMarkers(spots.getMarkers());
                }

//                    drawRadius();
                Log.v(TAG, "Sync duration: " + (System.currentTimeMillis() - startTime) + " ms");
            } else {
                Log.e(TAG, "No spots found..");
            }

            // Done processing: hide the progressbar
            if (listener != null) {
                listener.onPostExecute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        if (listener != null) {
            listener.onPostExecute();
        }
    }

    private void drawRadius() {
        final LatLng radiusLatLng = vMap.fromScreenLocation(new PointF(0, 0));
        final MarkerOptions marker = new MarkerOptions();
        marker.position(radiusLatLng);
        vMap.addMarker(marker);

        vMap.addPolyline(new PolylineOptions()
                .add(new LatLng[]{mCenterLatLng, radiusLatLng})
                .color(Color.GREEN)
                .width(5));
    }
}
