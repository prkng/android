package ng.prk.prkngandroid.ui.thread;

import android.content.Context;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.model.ui.JsonSnippet;
import ng.prk.prkngandroid.model.ui.MapAssets;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;
import ng.prk.prkngandroid.util.CarshareUtils;

public class CarshareVehiclesDownloadTask extends PrkngDataDownloadTask {
    private final static String TAG = "CarshareVehiclesTask";

    private final Context context;

    public CarshareVehiclesDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
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
                // Get API data
                final PointsGeoJSON lots = ApiClient.getCarshareVehicles(service,
                        apiKey,
                        mapGeometry.getLatitude(),
                        mapGeometry.getLongitude(),
                        mapGeometry.getRadius(),
                        mapAssets.getCarshareCompanies(),
                        getNearest()
                );

                // Prepare map annotations: Markers only
                final List<PointsGeoJSONFeature> lotsFeatures = lots.getFeatures();
                final Gson gson = JsonSnippet.getGson();

                for (PointsGeoJSONFeature feature : lotsFeatures) {
                    final GeoJSONFeatureProperties properties = feature.getProperties();

                    final List<Double> latLng = feature.getGeometry().getCoordinates();
                    final JsonSnippet snippet = new JsonSnippet.Builder()
                            .id(feature.getId())
                            .title(properties.getName())
                            .company(properties.getCompany())
                            .fuel(properties.getFuel())
                            .partnerId(properties.getPartnerId())
                            .build();
                    final MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(new LatLng(latLng.get(1), latLng.get(0))))
                            .title(CarshareUtils.getCompanyName(context, properties.getCompany()))
                            .snippet(JsonSnippet.toJson(snippet, gson))
                            .icon(mapAssets.getCarshareVehicleMarkerIcon(properties.getCompany()));

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
}
