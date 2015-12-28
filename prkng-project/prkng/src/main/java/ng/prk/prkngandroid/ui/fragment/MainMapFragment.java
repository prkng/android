package ng.prk.prkngandroid.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.ui.SelectedFeature;
import ng.prk.prkngandroid.ui.thread.CarshareSpotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.CarshareVehiclesDownloadTask;
import ng.prk.prkngandroid.ui.thread.LotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.SpotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;
import ng.prk.prkngandroid.util.MapUtils;

public class MainMapFragment extends Fragment implements
        SpotsDownloadTask.MapTaskListener,
        MapView.OnMapChangedListener,
        MapView.OnMapClickListener,
        MapView.OnMarkerClickListener {
    private final static String TAG = "MainMapFragment";
    private final static double RADIUS_FIX = 1.4d;
    @Deprecated
    private final static boolean MY_LOCATION_ENABLED = true;

    private CircleProgressBar vProgressBar;
    private MapView vMap;
    private String mApiKey;
    private MapAssets mapAssets;
    private MapGeometry mLastMapGeometry;
    private boolean mIgnoreMinDistance;
    private PrkngDataDownloadTask mTask;
    private OnMapMarkerClickListener listener;
    private int mPrkngMapType;
    private HashMap<String, List<Annotation>> mFeatureAnnotsList;
    private List<Annotation> mSelectedAnnotsList;
    private SelectedFeature mSelectedFeature;

    public static MainMapFragment newInstance() {
        return new MainMapFragment();
    }

    public interface OnMapMarkerClickListener {
        void showMarkerInfo(Marker marker, int type);

        void hideMarkerInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnMapMarkerClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMarkerClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        vProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PackageManager.PERMISSION_GRANTED !=
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        PackageManager.PERMISSION_GRANTED !=
                                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissionIfNeeded();
                } else {
                    moveToMyLocation(true);
                }
            }
        });

        createMapIfNecessary(view, savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart");

        super.onStart();
        vMap.onStart();

        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            vMap.setMyLocationEnabled(MY_LOCATION_ENABLED);
            vMap.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");

        super.onResume();
        onMapResume();

        if (vMap.getMyLocation() != null) {
            moveToMyLocation(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        vMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        onMapStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vMap.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        vMap.onSaveInstanceState(outState);
    }

    private void createMapIfNecessary(View view, Bundle savedInstanceState) {
        vMap = (MapView) view.findViewById(R.id.mapview);
        vMap.onCreate(savedInstanceState);

        vMap.setCenterCoordinate(Const.UiConfig.MONTREAL_LAT_LNG);
        vMap.setZoomLevel(Const.UiConfig.DEFAULT_ZOOM);
        vMap.setTiltEnabled(false);
        vMap.setMyBearingTrackingMode(MyBearingTracking.NONE);

        // Load map assets and colors
        if (mapAssets == null) {
            mapAssets = new MapAssets(vMap);
        }
        mLastMapGeometry = new MapGeometry(vMap.getCenterCoordinate(), vMap.getZoomLevel());
        mPrkngMapType = Const.MapSections.ON_STREET;
    }

    private void onMapResume() {
        if (vMap == null) {
            createMapIfNecessary(getView(), null);
        }

        vMap.onResume();

        vMap.addOnMapChangedListener(this);
        vMap.setOnMapClickListener(this);
        vMap.setOnMarkerClickListener(this);
    }

    private void onMapStop() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(false);
        }

        if (vMap != null) {
            vMap.onStop();

            vMap.removeAllAnnotations();
            vMap.removeOnMapChangedListener(this);
            vMap.setOnMapClickListener(null);
            vMap.setOnMarkerClickListener(null);
        }
    }

    /**
     * Implements MapView.OnMapChangedListener
     * Called when the displayed map view changes.
     *
     * @param change The type of map change event
     */
    @Override
    public void onMapChanged(int change) {

        switch (change) {
            case MapView.REGION_DID_CHANGE:
                updateMapData(vMap.getCenterCoordinate(), vMap.getZoomLevel());
                break;
            case MapView.DID_FINISH_LOADING_MAP:
            case MapView.REGION_DID_CHANGE_ANIMATED:
                if (mLastMapGeometry.distanceTo(vMap.getCenterCoordinate()) >= Const.UiConfig.MIN_UPDATE_DISTACE
                        || isIgnoreMinDistance()) {
                    mIgnoreMinDistance = false;
                    updateMapData(vMap.getCenterCoordinate(), vMap.getZoomLevel());
                }
                break;
            case MapView.REGION_WILL_CHANGE:
            case MapView.REGION_WILL_CHANGE_ANIMATED:
            case MapView.REGION_IS_CHANGING:
            case MapView.WILL_START_LOADING_MAP:
            case MapView.WILL_START_RENDERING_MAP:
            case MapView.WILL_START_RENDERING_FRAME:
//                Log.v(TAG, "onMapChanged @ " + change);
                break;
            case MapView.DID_FINISH_RENDERING_FRAME:
            case MapView.DID_FINISH_RENDERING_FRAME_FULLY_RENDERED:
                break;
            case MapView.DID_FINISH_RENDERING_MAP:
            case MapView.DID_FINISH_RENDERING_MAP_FULLY_RENDERED:
                Log.d(TAG, "onMapChanged @ " + change);
                break;
            case MapView.DID_FAIL_LOADING_MAP:
                Log.e(TAG, "onMapChanged failed");
                break;
        }
    }

    /**
     * Implements MapView.OnMapClickListener
     * Called when the user clicks on the map view.
     *
     * @param point The projected map coordinate the user clicked on.
     */
    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (listener != null) {
            listener.hideMarkerInfo();
        }
        unselectFeatureIfNecessary();
    }

    /**
     * Implements MapView.OnMarkerClickListener
     * Called when the user clicks on a marker.
     *
     * @param marker
     * @return True, to consume the event and skip showing the infoWindow
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        final String featureId = marker.getSnippet();
        final String selectedId = mSelectedFeature == null ? null : mSelectedFeature.getId();
        if (featureId != null && featureId.equals(selectedId)) {
            // Skip if re-clicked the same Feature
            return true;
        }

        switch (mPrkngMapType) {
            case Const.MapSections.ON_STREET: {
                unselectFeatureIfNecessary();
                selectFeature(marker.getSnippet());

                if (listener != null) {
                    listener.showMarkerInfo(marker, mPrkngMapType);
                }
                return true;
            }
            case Const.MapSections.OFF_STREET:
                if (listener != null) {
                    listener.showMarkerInfo(marker, mPrkngMapType);
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Implements UpdateSpotsTasks.MapTaskListener
     */
    @Override
    public void onPreExecute() {
        if (vProgressBar != null) {
            vProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Implements UpdateSpotsTasks.MapTaskListener
     */
    @Override
    public void onPostExecute() {
        if (vProgressBar != null) {
            vProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAnnotationsList(HashMap<String, List<Annotation>> annotations) {
        mFeatureAnnotsList = annotations;
        mSelectedAnnotsList = new ArrayList<>();
        mSelectedFeature = null;

        if (listener != null) {
            listener.hideMarkerInfo();
        }
    }

    public void requestPermissionIfNeeded() {
        Log.v(TAG, "requestPermissionIfNeeded");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(vMap, R.string.snackbar_location_permission_needed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermission();
                                }
                            })
                    .show();
        } else {
            requestPermission();
        }
    }

    public void requestPermission() {
        Log.v(TAG, "requestPermission");

        // Permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Const.RequestCodes.PERMISSION_ACCESS_LOCATION);
    }

    private boolean isIgnoreMinDistance() {
        return mIgnoreMinDistance;
    }

    private void updateMapData(LatLng latLng, double zoom) {
        Log.v(TAG, "updateMapData @ " + zoom);

        if (MapUtils.isMinZoom(zoom, mPrkngMapType)) {
            if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e(TAG, "skipped");
                mTask.cancel(false);
//                return;
            }
            mLastMapGeometry.setLatitude(latLng.getLatitude());
            mLastMapGeometry.setLongitude(latLng.getLongitude());
            mLastMapGeometry.setZoomAndRadius(zoom, vMap.fromScreenLocation(new PointF(0, 0)));

            switch (mPrkngMapType) {
                case Const.MapSections.OFF_STREET:
                    mTask = new LotsDownloadTask(vMap, mapAssets, this);
                    break;
                case Const.MapSections.ON_STREET:
                    mTask = new SpotsDownloadTask(vMap, mapAssets, this);
                    break;
                case Const.MapSections.CARSHARE_SPOTS:
                    mTask = new CarshareSpotsDownloadTask(vMap, mapAssets, this);
                    break;
                case Const.MapSections.CARSHARE_VEHICLES:
                    mTask = new CarshareVehiclesDownloadTask(vMap, mapAssets, this);
                    break;
            }

            mTask.execute(mLastMapGeometry);
        } else {
            mIgnoreMinDistance = true;
            Snackbar.make(vMap, R.string.snackbar_map_zoom_needed, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            vMap.setZoomLevel(MapUtils.getMinZoomPerType(mPrkngMapType), true);
                        }
                    }).show();
        }
    }

    private void moveToMyLocation(boolean animated) {
        if (!vMap.isMyLocationEnabled()) {
            vMap.setMyLocationEnabled(MY_LOCATION_ENABLED);
            vMap.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        }
        final Location myLocation = vMap.getMyLocation();
        if (myLocation != null) {
            vMap.setCenterCoordinate(new LatLngZoom(
                    myLocation.getLatitude(),
                    myLocation.getLongitude(),
                    Math.max(Const.UiConfig.MY_LOCATION_ZOOM, vMap.getZoomLevel())
            ), animated);
        }
    }

    public void setMapType(int type) {
        Log.v(TAG, "setMapType @ " + type);
        if (type != mPrkngMapType) {
            vMap.removeAllAnnotations();
            mPrkngMapType = type;
            updateMapData(vMap.getCenterCoordinate(), vMap.getZoomLevel());
        }
    }

    private void selectFeature(String featureId) {
        // Store the selected feature's ID, for restore
        mSelectedFeature = new SelectedFeature(featureId);
        final List<Annotation> annotations = mFeatureAnnotsList.get(featureId);

        // Reset the selection array
        mSelectedAnnotsList = new ArrayList<>();

        for (Annotation annot : annotations) {
            if (annot instanceof Marker) {
                final Marker m = ((Marker) annot);

                if (!m.getIcon().getId().equals(mapAssets.getMarkerIconTransparent().getId())) {
                    // Store the selected feature's marker Icon, for restore
                    mSelectedFeature.setMarkerIcon(m.getIcon());

                    // Change the marker's Icon
                    final Marker selectedMarker = vMap.addMarker(
                            MapUtils.extractMarkerOptions(m)
                                    .icon(mapAssets.getMarkerIconSelected())
                    );

                    // Add to the selection array
                    mSelectedAnnotsList.add(selectedMarker);

                    // Remove old marker from Map
                    vMap.removeAnnotation(m);
                } else {
                    // Store transparent buttons without any changes
                    mSelectedAnnotsList.add(m);
                }
            } else if (annot instanceof Polyline) {
                final Polyline p = ((Polyline) annot);
                // Store the selected feature's polyline Color, for restore
                mSelectedFeature.setPolylineColor(p.getColor());

                // Change the polyline's color
                final Polyline selected = vMap.addPolyline(
                        MapUtils.extractPolylineOptions(p)
                                .color(mapAssets.getLineColorSelected())
                );
                // Add to the selection array
                mSelectedAnnotsList.add(selected);

                // Remove old polyline from Map
                vMap.removeAnnotation(p);
            }
        }

        // Update the global feature-annotations reference list
        mFeatureAnnotsList.put(featureId, mSelectedAnnotsList);
    }

    private void unselectFeatureIfNecessary() {
        if (mSelectedFeature == null) {
            return;
        }

        final List<Annotation> restoredAnnotsList = new ArrayList<>();

        for (Annotation annot : mSelectedAnnotsList) {
            if (annot instanceof Marker) {
                final Marker m = ((Marker) annot);
                if (!m.getIcon().getId().equals(mapAssets.getMarkerIconTransparent().getId())) {
                    // Create a marker with original Icon
                    final MarkerOptions markerOptions = MapUtils
                            .extractMarkerOptions(m)
                            .icon(mSelectedFeature.getMarkerIcon());

                    // Add the restored marker to map
                    final Marker selected = vMap.addMarker(markerOptions);

                    // Add the restored marker to the the global feature-annotations reference list
                    restoredAnnotsList.add(selected);

                    // Remove selected marker from map
                    vMap.removeAnnotation(m);
                } else {
                    // Restore the invisible buttons
                    restoredAnnotsList.add(m);
                }
            } else if (annot instanceof Polyline) {
                final Polyline p = ((Polyline) annot);
                // Create a polyline with original color
                final PolylineOptions polylineOptions = MapUtils
                        .extractPolylineOptions(p)
                        .color(mSelectedFeature.getPolylineColor());

                // Add the restored polyline to map
                final Polyline selected = vMap.addPolyline(polylineOptions);

                // Add the restored polyline to the the global feature-annotations reference list
                restoredAnnotsList.add(selected);

                // Remove selected polyline from map
                vMap.removeAnnotation(p);
            }
        }
        // Update the global feature-annotations reference list
        mFeatureAnnotsList.put(mSelectedFeature.getId(), restoredAnnotsList);

        // Clear the selected Feature
        mSelectedFeature = null;
        mSelectedAnnotsList = new ArrayList<>();
    }
}
