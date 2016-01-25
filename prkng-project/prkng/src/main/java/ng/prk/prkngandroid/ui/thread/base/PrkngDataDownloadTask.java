package ng.prk.prkngandroid.ui.thread.base;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Annotation;
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

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.SpotsAnnotations;
import ng.prk.prkngandroid.model.ui.MapAssets;
import ng.prk.prkngandroid.util.MapUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public abstract class PrkngDataDownloadTask extends AsyncTask<MapGeometry, Void, SpotsAnnotations> {
    private final static String TAG = "PrkngDataTask ";

    private MapTaskListener listener;
    protected MapAssets mapAssets;
    private MapView vMap;
    protected long startTime;
    private PrkngApiError error = null;
    private HashMap<String, List<Annotation>> featureAnnotsList;

    public interface MapTaskListener {
        void onPreExecute();

        void onPostExecute();

        void setAnnotationsList(HashMap<String, List<Annotation>> annotations);

        void onFailure(PrkngApiError e);

        void setBounds(LatLng visible);

        float getDurationFilter();
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

    @Override
    protected SpotsAnnotations doInBackground(MapGeometry... params) {
        // This should be in `onPreExecute()`, but can
        return null;
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
            if (error != null) {
                listener.onFailure(error);
            }
            if (spots != null) {
                MapUtils.removeAllAnnotations(vMap);

                if (!spots.getPolylines().isEmpty()) {
                    // Note: Mapbox's Iterator can throw a local reference table overflow exception
                    // vMap.addPolylines(spots.getPolylines());
                    for (Map.Entry<PolylineOptions, String> entry : spots.getPolylines().entrySet()) {
                        Polyline p = vMap.addPolyline(entry.getKey());
                        addToAnnotationsList(entry.getValue(), p);
                    }
                }
                spots.clearPolylines();

                // Markers must be added after Polylines to show the dot above the line (z-order)
                if (!spots.getMarkers().isEmpty()) {
                    // TODO refactor the hashmap
                    final List<Marker> markersList = vMap.addMarkers(new ArrayList<>(spots.getMarkers().keySet()));
                    int i = 0;
                    for (Map.Entry<MarkerOptions, String> entry : spots.getMarkers().entrySet()) {
                        // Note: Mapbox si very slow when adding markers individually
//                        Marker m = vMap.addMarker(entry.getKey());
                        addToAnnotationsList(entry.getValue(), markersList.get(i++));
                    }
                    if (forceBoundingBox()) {
                        listener.setBounds(markersList.get(0).getPosition());
                    }
                }
                spots.clearMarkers();


//                    drawRadius(spots.getCenterCoordinate());
                Log.v(TAG, "Sync duration: " + (System.currentTimeMillis() - startTime) + " ms");
            } else {
                Log.v(TAG, "No spots found..");
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

    private void addToAnnotationsList(String featureId, Annotation annotation) {
        List<Annotation> annots = featureAnnotsList.get(featureId);
        if (annots == null) {
            annots = new ArrayList<>();
        }
        annots.add(annotation);
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

    protected float getDuration() {
        if (vMap == null) {
            return Const.UiConfig.DEFAULT_DURATION;
        }

        return listener.getDurationFilter();
    }

    protected boolean forceBoundingBox() {
        return false;
    }

    protected void setBackgroundError(PrkngApiError e) {
        this.error = e;
    }
}
