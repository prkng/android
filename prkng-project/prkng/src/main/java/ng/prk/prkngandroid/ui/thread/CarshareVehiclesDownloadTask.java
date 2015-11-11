package ng.prk.prkngandroid.ui.thread;

import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;

public class CarshareVehiclesDownloadTask extends PrkngDataDownloadTask {
    private final static String TAG = "CarshareVehiclesTask";

    public CarshareVehiclesDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        super(mapView, mapAssets, listener);
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
        spotsAnnotations.setCenterCoordinate(mapGeometry);
        try {
            final String apiKey = getApiKey(service);

            if (apiKey != null && mapGeometry != null) {
                // Get API data
                final PointsGeoJSON lots = ApiClient.getCarshareVehicles(service,
                        apiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius()
                );

                // Prepare map annotations: Markers only
                final List<PointsGeoJSONFeature> lotsFeatures = lots.getFeatures();
                for (PointsGeoJSONFeature feature : lotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    final List<Double> latLng = feature.getGeometry().getCoordinates();
                    final MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(new LatLng(latLng.get(1), latLng.get(0))))
                            .title(properties.getCompany() + " " + properties.getName())
                            .snippet(feature.getId())
                            .icon(mapAssets.getMarkerIconCarshare());

                    spotsAnnotations.addMarker(markerOptions);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return spotsAnnotations;
    }
}