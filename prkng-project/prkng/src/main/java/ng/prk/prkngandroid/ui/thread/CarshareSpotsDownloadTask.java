package ng.prk.prkngandroid.ui.thread;

import android.content.Context;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.model.ui.MapAssets;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;
import ng.prk.prkngandroid.util.CarshareUtils;

public class CarshareSpotsDownloadTask extends PrkngDataDownloadTask {
    private final static String TAG = "CarshareSpotsTask";

    private final Context context;

    public CarshareSpotsDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        super(mapView, mapAssets, listener);

        this.context = mapView.getContext();
    }

    protected int getNearest() {
        return 0;
    }

    /**
     * Download API data and prepare map annotations
     *
     * @param params
     * @return
     */
    @Override
    protected SpotsAnnotations doInBackground(MapGeometry... params) {
        startTime = System.currentTimeMillis();
        final MapGeometry mapGeometry = params[0];

        final PrkngService service = ApiClient.getService();

        final SpotsAnnotations spotsAnnotations = new SpotsAnnotations();
        spotsAnnotations.setCenterCoordinate(mapGeometry);

        try {
            final String apiKey = getApiKey();

            if (apiKey != null && mapGeometry != null) {
                /**
                 * Carshare on-street spots
                 */
                final LinesGeoJSON spots = ApiClient.getCarshareParkingSpots(service,
                        apiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius(),
                        getDuration()
                );

                final boolean hasVisibleMarkers = hasVisibleMarkers(mapGeometry.getZoom());

                spotsAnnotations.setCenterCoordinate(mapGeometry);
                // Prepare map annotations: Polylines and Markers
                final List<LinesGeoJSONFeature> spotsFeatures = spots.getFeatures();
                for (LinesGeoJSONFeature feature : spotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    final List<List<Double>> coords = feature.getGeometry().getCoordinates();
                    LatLng[] pointsArray = new LatLng[coords.size()];
                    int i = 0;
                    for (List<Double> latLng : coords) {
                        pointsArray[i++] = new LatLng(new LatLng(latLng.get(1), latLng.get(0)));
                    }
                    final PolylineOptions polylineOptions = new PolylineOptions()
                            .add(pointsArray)
                            .width(mapAssets.getLineWidth())
                            .color(properties.isTypePaid() ? mapAssets.getLineColorPaid() : mapAssets.getLineColorFree());
                    spotsAnnotations.addPolyline(feature.getId(), polylineOptions);

                    // Add the visible buttons
                    List<LatLng> buttons = properties.getButtonLocations();
                    for (LatLng buttonLatLng : buttons) {
                        final MarkerOptions markerOptions = new MarkerOptions()
                                .position(buttonLatLng)
                                .title(properties.getWayName())
                                .snippet(feature.getId());
                        if (hasVisibleMarkers) {
                            markerOptions.icon(properties.isTypePaid() ? mapAssets.getMarkerIconPaid() : mapAssets.getMarkerIconFree());
                        } else {
                            markerOptions.icon(mapAssets.getMarkerIconTransparent());
                        }

                        spotsAnnotations.addMarker(feature.getId(), markerOptions);
                    }
                }


                /**
                 * Carshare off-street lots
                 */
                final PointsGeoJSON lots = ApiClient.getNearestCarshareLots(ApiClient.getService(),
                        apiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius(),
                        mapAssets.getCarshareCompanies(),
                        getNearest()
                );

                // Prepare map annotations: Markers only
                final List<PointsGeoJSONFeature> lotsFeatures = lots.getFeatures();
                for (PointsGeoJSONFeature feature : lotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    final List<Double> latLng = feature.getGeometry().getCoordinates();
                    // TODO replace string
                    final MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(new LatLng(latLng.get(1), latLng.get(0))))
                            .title(CarshareUtils.getCompanyName(context, properties.getCompany()))
                            .snippet(properties.getName())
                            .icon(mapAssets.getCarshareLotMarkerIcon(
                                    properties.getCompany(),
                                    properties.getAvailableOrCapacity()
                            ));

                    spotsAnnotations.addMarker(feature.getId(), markerOptions);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (PrkngApiError e) {
            setBackgroundError(e);
        }

        return spotsAnnotations;
    }

    protected boolean hasVisibleMarkers(double zoom) {
        return Double.compare(Const.UiConfig.SMALL_BUTTONS_ZOOM, zoom) <= 0;
    }
}
