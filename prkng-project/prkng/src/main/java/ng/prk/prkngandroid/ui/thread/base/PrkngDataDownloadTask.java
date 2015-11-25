package ng.prk.prkngandroid.ui.thread.base;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.SpotsAnnotations;

public abstract class PrkngDataDownloadTask extends AsyncTask<MapGeometry, Void, SpotsAnnotations> {
    private final static String TAG = "PrkngDataTask ";

    private MapTaskListener listener;
    protected MapAssets mapAssets;
    private MapView vMap;
    private String mApiKey;
    protected long startTime;

    public interface MapTaskListener {
        void onPreExecute();

        void onPostExecute();
    }

    public PrkngDataDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        this.vMap = mapView;
        this.mapAssets = mapAssets;
        this.listener = listener;
    }

    /**
     * Display the progressbar, before processing API data
     */
    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onPreExecute();
        }
        startTime = System.currentTimeMillis();
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

                if (!spots.getPolylines().isEmpty()) {
                    Log.v(TAG, "addPolylines");
                    vMap.addPolylines(spots.getPolylines());
                }

                // Markers must be added after Polylines to show the dot above the line (z-order)
                if (!spots.getMarkers().isEmpty()) {
                    Log.v(TAG, "addMarkers");
                    vMap.addMarkers(spots.getMarkers());
                }


//                    drawRadius(spots.getCenterCoordinate());
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


    /**
     * Validate if the map's zoomLevel allows showing Markers
     *
     * @return
     */
    private void drawRadius(LatLng center) {
        final LatLng radiusLatLng = vMap.fromScreenLocation(new PointF(0, 0));
        final MarkerOptions marker = new MarkerOptions();
        marker.position(radiusLatLng);
        vMap.addMarker(marker);

        vMap.addPolyline(new PolylineOptions()
                .add(new LatLng[]{center, radiusLatLng})
                .color(Color.GREEN)
                .width(5));
    }

    @Deprecated
    public String getApiKey(PrkngService service) {
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

        return mApiKey;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
    }
}
