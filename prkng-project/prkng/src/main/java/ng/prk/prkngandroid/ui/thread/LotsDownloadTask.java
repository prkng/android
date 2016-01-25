package ng.prk.prkngandroid.ui.thread;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.model.ui.MapAssets;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotsDownloadTask extends PrkngDataDownloadTask {
    private final static String TAG = "LotsTask";

    public LotsDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        super(mapView, mapAssets, listener);
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

        final PrkngService service = ApiClient.getServiceLog();

        final SpotsAnnotations spotsAnnotations = new SpotsAnnotations();
        spotsAnnotations.setCenterCoordinate(mapGeometry);
        try {
            final String apiKey = getApiKey();

            if (apiKey != null && mapGeometry != null) {
                // Get API data
                PointsGeoJSON lots = ApiClient.getNearestParkingLots(service,
                        apiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius(),
                        getNearest()
                );

                final long now = CalendarUtils.todayMillis();

                // Prepare map annotations: Markers only
                final List<PointsGeoJSONFeature> lotsFeatures = lots.getFeatures();

                final int bestPrice = getBestPrice(lotsFeatures, now);

                for (PointsGeoJSONFeature feature : lotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    final LotCurrentStatus status = feature.getProperties().getAgenda().getLotCurrentStatus(now);
                    final int price = (status == null) ? Const.UNKNOWN_VALUE : status.getMainPriceRounded();
                    final int type = (status == null) ? Const.BusinnessHourType.CLOSED : Const.BusinnessHourType.OPEN;

                    if (type == Const.BusinnessHourType.CLOSED) {
                        // TODO handle closed lots correctly
                        continue;
                    }

                    final List<Double> latLng = feature.getGeometry().getCoordinates();
                    final MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(new LatLng(latLng.get(1), latLng.get(0))))
                            .title(properties.getAddress())
                            .snippet(feature.getId())
                            .icon(mapAssets.getLotMarkerIcon(price, type, price == bestPrice));

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

    protected static int getBestPrice(List<PointsGeoJSONFeature> lotsFeatures, long now) {
        int best = Integer.MAX_VALUE;

        for (PointsGeoJSONFeature feature : lotsFeatures) {
            final LotCurrentStatus status = feature.getProperties().getAgenda().getLotCurrentStatus(now);
            if (status != null) {
                best = Math.min(best, status.getMainPriceRounded());
            }
        }

        return best;
    }

}
