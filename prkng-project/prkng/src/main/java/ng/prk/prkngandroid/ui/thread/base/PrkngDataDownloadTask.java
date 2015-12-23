package ng.prk.prkngandroid.ui.thread.base;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.util.PrkngPrefs;

public abstract class PrkngDataDownloadTask extends AsyncTask<MapGeometry, Void, SpotsAnnotations> {
    private final static String TAG = "PrkngDataTask ";

    private MapTaskListener listener;
    protected MapAssets mapAssets;
    private MapView vMap;
    private String mApiKey;
    protected long startTime;
    private PrkngApiError error = null;
    private HashMap<String, List<Long>> featureAnnotsList;

    public interface MapTaskListener {
        void onPreExecute();

        void onPostExecute();

        void setAnnotationsList(HashMap<String, List<Long>> annotations);
    }

    public PrkngDataDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        this.vMap = mapView;
        this.mapAssets = mapAssets;
        this.listener = listener;
        this.featureAnnotsList = new HashMap<>();
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

        if (error != null) {
            error.showSnackbar(vMap);
            return;
        }
        try {
            if (spots != null) {
                Log.v(TAG, "removeAllAnnotations");
                vMap.removeAllAnnotations();

                if (!spots.getPolylines().isEmpty()) {
                    Log.v(TAG, "addPolylines");

                    // Note: Mapbox's Iterator can throw a local reference table overflow exception
                    // vMap.addPolylines(spots.getPolylines());
                    for (Map.Entry<PolylineOptions, String> entry : spots.getPolylines().entrySet()) {
                        Polyline p = vMap.addPolyline(entry.getKey());
                        addToAnnotationsList(entry.getValue(), p.getId());
                    }
                }
                spots.clearPolylines();

                // Markers must be added after Polylines to show the dot above the line (z-order)
                if (!spots.getMarkers().isEmpty()) {
                    Log.v(TAG, "addMarkers");
                    for (Map.Entry<MarkerOptions, String> entry : spots.getMarkers().entrySet()) {
                        Marker m = vMap.addMarker(entry.getKey());
                        addToAnnotationsList(entry.getValue(), m.getId());
                    }
                }
                spots.clearMarkers();


//                    drawRadius(spots.getCenterCoordinate());
                Log.v(TAG, "Sync duration: " + (System.currentTimeMillis() - startTime) + " ms");
            } else {
                Log.e(TAG, "No spots found..");
            }

            // Done processing: hide the progressbar
            if (listener != null) {
                listener.setAnnotationsList(featureAnnotsList);
                listener.onPostExecute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addToAnnotationsList(String featureId, Long annotId) {
        List<Long> annots = featureAnnotsList.get(featureId);
        if (annots == null) {
            annots = new ArrayList<>();
        }
        annots.add(annotId);
        featureAnnotsList.put(featureId, annots);
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

    protected String getApiKey() {
        if (vMap == null) {
            return null;
        }
        return PrkngPrefs.getInstance(vMap.getContext()).getApiKey();
    }

    protected void setBackgroundError(PrkngApiError e) {
        this.error = e;
    }
}
