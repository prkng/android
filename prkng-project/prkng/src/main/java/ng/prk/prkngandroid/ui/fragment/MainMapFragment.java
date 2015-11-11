package ng.prk.prkngandroid.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.MapAssets;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.ui.thread.UpdateSpotsTask;

public class MainMapFragment extends Fragment implements
        UpdateSpotsTask.MapTaskListener,
        MapView.OnMapChangedListener,
        MapView.OnMapClickListener,
        MapView.OnMarkerClickListener {
    private final static String TAG = "MainMapFragment";
    private final static double RADIUS_FIX = 1.4d;

    private CircleProgressBar vProgressBar;
    private MapView vMap;
    private String mApiKey;
    private MapAssets mMapAssets;
    private MapGeometry mLastMapGeometry;
    private boolean mIgnoreMinDistance;
    private UpdateSpotsTask mTask;

    public static MainMapFragment newInstance() {
        return new MainMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        mTask = UpdateSpotsTask.create();  // Initialize empty Task

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
            vMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");

        super.onResume();
        vMap.onResume();

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

        vMap.removeAllAnnotations();
        vMap.onStop();
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
        if (vMap == null) {
            vMap = (MapView) view.findViewById(R.id.mapview);

            vMap.setCenterCoordinate(new LatLng(45.501689, -73.567256));
            vMap.setZoomLevel(Const.UiConfig.DEFAULT_ZOOM);
            vMap.onCreate(savedInstanceState);
            vMap.addOnMapChangedListener(this);
            vMap.setOnMapClickListener(this);
            vMap.setOnMarkerClickListener(this);

            // Load map assets and colors
            mMapAssets = new MapAssets(vMap);
            mLastMapGeometry = new MapGeometry(vMap.getCenterCoordinate(), vMap.getZoomLevel());
        }
    }

    /**
     * Implements MapView.OnMapChangedListener
     *
     * @param change
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
                        || mIgnoreMinDistance) {
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
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        Log.v(TAG, "onMapClick "
                + String.format("latLng = %s", latLng));

    }

    /**
     * Implements MapView.OnMarkerClickListener
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.v(TAG, "onMarkerClick @ " + marker.getTitle());
        return false;
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

    public void requestPermissionIfNeeded() {
        Log.v(TAG, "requestPermissionIfNeeded");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(vMap, R.string.location_permission_needed, Snackbar.LENGTH_INDEFINITE)
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

    private void updateMapData(LatLng latLng, double zoom) {
        Log.v(TAG, "updateMapData @ " + zoom);

        if (Double.compare(Const.UiConfig.MIN_ZOOM, zoom) <= 0) {
            if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e(TAG, "skipped");
                mTask.cancel(false);
//                return;
            }
            mLastMapGeometry.setLatitude(latLng.getLatitude());
            mLastMapGeometry.setLongitude(latLng.getLongitude());
            mLastMapGeometry.setZoomAndRadius(zoom, vMap.fromScreenLocation(new PointF(0, 0)));

            mTask = new UpdateSpotsTask(
                    vMap, mMapAssets, this
            );
            mTask.execute(mLastMapGeometry);
        } else {
            mIgnoreMinDistance = true;
            Snackbar.make(vMap, R.string.map_zoom_needed, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            vMap.setZoomLevel(Const.UiConfig.MIN_ZOOM, true);
                        }
                    }).show();
        }
    }

    private void moveToMyLocation(boolean animated) {
        if (!vMap.isMyLocationEnabled()) {
            vMap.setMyLocationEnabled(true);
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

}
