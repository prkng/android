package ng.prk.prkngandroid.ui.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.LinesGeoJSON;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.PointsGeoJSON;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;

public class MainMapFragment extends Fragment implements
        MapView.OnMapChangedListener {
    private final static String TAG = "MainMapFragment";

    private MapView mapView;
    private String mApiKey;

    public static MainMapFragment newInstance() {
        return new MainMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        createMapIfNecessary(view, savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        new UpdateSpotsTasks().execute(
                new LatLng(45.501689d, -73.567256d));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void createMapIfNecessary(View view, Bundle savedInstanceState) {
        if (mapView == null) {
            mapView = (MapView) view.findViewById(R.id.mapview);

            mapView.setCenterCoordinate(new LatLng(45.501689, -73.567256));
            mapView.setZoomLevel(14);
            mapView.onCreate(savedInstanceState);
//            mapView.addOnMapChangedListener(this);
        }
    }

    @Override
    public void onMapChanged(int change) {
        Log.v(TAG, "onMapChanged @ " + change);
        if (change == MapView.DID_FINISH_RENDERING_FRAME_FULLY_RENDERED) {
            Snackbar.make(mapView, "Map region DID change", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }


    private class UpdateSpotsTasks extends AsyncTask<LatLng, Void, List<PolylineOptions>> {

        @Override
        protected List<PolylineOptions> doInBackground(LatLng... params) {
            Log.v(TAG, "doInBackground");
            final LatLng centerLatLng = params[0];

            final PrkngService service = ApiClient.getService();
            final List<PolylineOptions> polylines = new ArrayList<>();
            final int lineColor = Color.parseColor("#3bb2d0");

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

                if (mApiKey != null && centerLatLng != null) {
                    final LinesGeoJSON spots = ApiClient.getParkingSpots(service,
                            mApiKey,
                            centerLatLng.getLatitude(),
                            centerLatLng.getLongitude()
                    );

                    final List<LinesGeoJSONFeature> spotsFeatures = spots.getFeatures();
                    for (LinesGeoJSONFeature feature : spotsFeatures) {
                        List<List<Double>> coords = feature.getGeometry().getCoordinates();

                        LatLng[] pointsArray = new LatLng[coords.size()];
                        int i = 0;
                        for (List<Double> latLng : coords) {
                            pointsArray[i++] = new LatLng(new LatLng(latLng.get(1), latLng.get(0)));
                        }
                        polylines.add(new PolylineOptions()
                                        .add(pointsArray)
                                        .color(lineColor)
                                        .width(2)
                        );
                    }

                    Log.v(TAG, "DDD");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return polylines;
        }

        @Override
        protected void onPostExecute(List<PolylineOptions> spots) {
            if (spots != null) {
                mapView.addPolylines(spots);
                Log.v(TAG, "EEE");

            } else {
                Log.v(TAG, "spots not found");
            }
        }
    }

    private class UpdateLotsTasks extends AsyncTask {

        private PointsGeoJSON lots;

        @Override
        protected Object doInBackground(Object[] params) {
            Log.v(TAG, "doInBackground");

            final PrkngService service = ApiClient.getServiceLog();

            try {
                LoginObject loginObject = ApiClient
                        .loginEmail(
                                service,
                                "mudar@prk.ng",
                                "mudar123");
                if (loginObject != null) {
                    Log.v(TAG, "name = " + loginObject.getName() + " email = " + loginObject.getEmail());
                    Log.v(TAG, "mApiKey = " + loginObject.getApikey());

                    lots = ApiClient.getParkingLots(service,
                            loginObject.getApikey(),
                            45.501689d,
                            -73.567256d);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (lots != null) {
                final List<PointsGeoJSONFeature> lotsFeatures = lots.getFeatures();
                for (PointsGeoJSONFeature feature : lotsFeatures) {
                    List<Double> latLng = feature.getGeometry().getCoordinates();

                    Log.v(TAG, "Point: " + latLng.toString());
                    final MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(latLng.get(1), latLng.get(0)));
                    mapView.addMarker(marker);

                }
            } else {
                Log.v(TAG, "lots not found");
            }
        }
    }
}
