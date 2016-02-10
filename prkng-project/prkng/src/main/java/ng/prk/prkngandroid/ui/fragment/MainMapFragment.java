package ng.prk.prkngandroid.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.PrkngApp;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.io.UnsupportedAreaException;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.model.MapGeometry;
import ng.prk.prkngandroid.model.ui.MapAssets;
import ng.prk.prkngandroid.model.ui.SelectedFeature;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import ng.prk.prkngandroid.ui.activity.SearchActivity;
import ng.prk.prkngandroid.ui.thread.CarshareSpotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.CarshareVehiclesDownloadTask;
import ng.prk.prkngandroid.ui.thread.LotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.NearestLotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.SpotsDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.PrkngDataDownloadTask;
import ng.prk.prkngandroid.ui.view.RedSnackbar;
import ng.prk.prkngandroid.util.AnalyticsUtils;
import ng.prk.prkngandroid.util.CarshareUtils;
import ng.prk.prkngandroid.util.CityBoundsHelper;
import ng.prk.prkngandroid.util.ConnectionUtils;
import ng.prk.prkngandroid.util.MapUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class MainMapFragment extends Fragment implements
        SpotsDownloadTask.MapTaskListener,
        MapView.OnMapChangedListener,
        MapView.OnMapClickListener,
        MapView.OnMarkerClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "MainMapFragment";
    private final static double RADIUS_FIX = 1.4d;
    private static final long ANIMATION_DURATION = 400L; // Mapbox's anim is 300

    @Deprecated
    private final static boolean MY_LOCATION_ENABLED = true;

    private FloatingActionButton vFab;
    private CircleProgressBar vProgressBar;
    private MapView vMap;
    private MapAssets mapAssets;
    private MapGeometry mLastMapGeometry;
    private boolean mIgnoreMinDistance;
    private boolean mIsZoomTooLow;
    private PrkngDataDownloadTask mTask;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private MapCallbacks listener;
    private int mPrkngMapType;
    private HashMap<String, List<Annotation>> mFeatureAnnotsList;
    private List<Annotation> mSelectedAnnotsList;
    private SelectedFeature mSelectedFeature;
    private Snackbar mSnackbar;
    private boolean isDialogShown = false;
    private Bundle initialArguments;
    private City mCurrentCity;
    private Marker mSearchMarker;

    public static MainMapFragment newInstance() {
        return newInstance(null);
    }

    public static MainMapFragment newInstance(LatLngZoom center) {
        final MainMapFragment fragment = new MainMapFragment();

        Bundle bundle = new Bundle();
        if (center != null) {
            bundle.putDouble(Const.BundleKeys.LATITUDE, center.getLatitude());
            bundle.putDouble(Const.BundleKeys.LONGITUDE, center.getLongitude());
            bundle.putDouble(Const.BundleKeys.ZOOM, center.getZoom());
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    public interface MapCallbacks {
        void showMarkerInfo(Marker marker, int type);

        void hideMarkerInfo();

        void showDurationDialog();

        boolean showCitiesDialog(LatLng latLng);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MapCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MapCallbacks");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        initialArguments = (savedInstanceState != null) ? savedInstanceState
                : getArguments();

        PrkngPrefs.getInstance(getContext()).registerPrefsChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        vProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        vFab = (FloatingActionButton) view.findViewById(R.id.fab);
        vFab.setOnClickListener(new View.OnClickListener() {
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
        super.onResume();
        onMapResume();

        if (!ConnectionUtils.hasConnection(getActivity())) {
            showConnectionError();
        }

        AnalyticsUtils.sendFragmentView(this);
    }


    private void showConnectionError() {
        vMap.removeAllAnnotations();
        vMap.removeOnMapChangedListener(this);

        mSnackbar = RedSnackbar.make(vMap, R.string.snackbar_connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ConnectionUtils.hasConnection(getActivity())) {
                            vMap.addOnMapChangedListener(MainMapFragment.this);
                            forceUpdate(null);
                        } else {
                            vMap.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showConnectionError();
                                }
                            }, DateUtils.SECOND_IN_MILLIS);
                        }
                    }
                });
        mSnackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Const.RequestCodes.CITY_SELECTOR == requestCode) {
            isDialogShown = false;

            if (resultCode == Activity.RESULT_OK) {
                final String cityName = data.getStringExtra(Const.BundleKeys.CITY);
                final LatLng latLng = new LatLng(
                        data.getDoubleExtra(Const.BundleKeys.LATITUDE, Const.UNKNOWN_VALUE),
                        data.getDoubleExtra(Const.BundleKeys.LONGITUDE, Const.UNKNOWN_VALUE));

                // Update currently selected city
                mCurrentCity = null;
                if (cityName != null && !cityName.isEmpty()) {
                    mCurrentCity = CityBoundsHelper.getCityByName(getContext(), cityName);
                }
                if (mCurrentCity == null) {
                    mCurrentCity = CityBoundsHelper.getNearestCity(getContext(), latLng);
                }

                vMap.setLatLng(latLng);
                vMap.setZoom(Const.UiConfig.DEFAULT_ZOOM, true);
            }
        } else if ((Const.RequestCodes.AUTH_LOGIN == requestCode) && (resultCode == Activity.RESULT_OK)) {
            forceUpdate(null);
        } else if ((Const.RequestCodes.SEARCH == requestCode) && (resultCode == Activity.RESULT_OK)) {
            final LatLngZoom latLng = MapUtils.getBundleGeoPoint(data.getExtras());
            final String title = data.getStringExtra(Const.BundleKeys.MARKER_TITLE);
            if (latLng != null) {
                mSearchMarker = MapUtils.addSearchMarker(vMap, latLng, mapAssets.getSearchMarkerIcon());

                latLng.setZoom(Const.UiConfig.MY_LOCATION_ZOOM);
                setCenterCoordinate(latLng);
            }

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

        try {
            PrkngPrefs.getInstance(getActivity()).unregisterPrefsChangeListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putDouble(Const.BundleKeys.LATITUDE, vMap.getLatLng().getLatitude());
            outState.putDouble(Const.BundleKeys.LONGITUDE, vMap.getLatLng().getLongitude());
            outState.putDouble(Const.BundleKeys.ZOOM, vMap.getZoom());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        vMap.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.CHECKIN_ID.equals(key)) {
            try {
                MapUtils.removeCheckinMarker(vMap);
                final long id = sharedPreferences.getLong(key, Const.UNKNOWN_VALUE);
                if (Long.valueOf(Const.UNKNOWN_VALUE).compareTo(id) == 0) {
                    showCheckinInfo(null);
                } else {
                    MapUtils.addCheckinMarkerIfAvailable(vMap,
                            mapAssets.getCheckinMarkerIcon());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Const.PrefsNames.CITY.equals(key)) {
            final String cityName = sharedPreferences.getString(key, null);

            mCurrentCity = CityBoundsHelper.getCityByName(getContext(), cityName);
            if (mCurrentCity != null) {
                vMap.setLatLng(mCurrentCity.getLatLng());
                vMap.setZoom(Const.UiConfig.DEFAULT_ZOOM, true);
            }
        } else if (CarshareUtils.isCarsharePrefsChange(key)) {
            mapAssets.setCarshareCompanies(PrkngPrefs.getInstance(getActivity())
                    .getCarshareCompanies(getResources()));
        }
    }

    private void createMapIfNecessary(View view, Bundle savedInstanceState) {
        vMap = (MapView) view.findViewById(R.id.mapview);
        vMap.onCreate(savedInstanceState);

        // Load map assets and colors
        if (mapAssets == null) {
            mapAssets = new MapAssets(vMap);
        }
        mLastMapGeometry = new MapGeometry(vMap.getLatLng(), vMap.getZoom());
        final boolean isCarshare = PrkngPrefs.getInstance(getActivity()).isCharshareMode();
        mPrkngMapType = isCarshare ? Const.MapSections.CARSHARE_OFFSET : 0;
    }

    private void showCheckinInfo(CheckinData checkin) {
        if (!isResumed()) {
            return;
        }

        final FragmentManager fm = getFragmentManager();
        if (checkin == null) {
            final Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.CHECKIN_INFO);
            if (fragment != null) {
                fm.beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        } else {
            fm.beginTransaction()
                    .replace(R.id.checkin_frame,
                            CheckinInfoFragment.newInstance(checkin.getId()),
                            Const.FragmentTags.CHECKIN_INFO)
                    .commit();
        }
    }

    private void onMapResume() {
        if (vMap == null) {
            createMapIfNecessary(getView(), null);
        }

        vMap.onResume();

        vMap.addOnMapChangedListener(this);
        vMap.setOnMapClickListener(this);
        vMap.setOnMarkerClickListener(this);

        final CheckinData checkin = PrkngPrefs.getInstance(getActivity()).getCheckinData();
        showCheckinInfo(checkin);
    }

    private void onMapStop() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(false);
        }

        if (vMap != null) {
            vMap.onStop();

//            vMap.removeAllAnnotations();
//            vMap.removeAllAnnotations();
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
        if (!isResumed()) {
            return;
        }

        switch (change) {
            case MapView.REGION_DID_CHANGE:
                onMapRegionChangedByUser();
                break;
            case MapView.DID_FINISH_LOADING_MAP:
                onMapFinishedLoading();
                break;
            case MapView.REGION_DID_CHANGE_ANIMATED:
                onMapRegionChangedAnimated();
                break;
            case MapView.DID_FAIL_LOADING_MAP:
                onMapFailedLoading();
                break;
//            case MapView.REGION_WILL_CHANGE:
//            case MapView.REGION_WILL_CHANGE_ANIMATED:
//            case MapView.REGION_IS_CHANGING:
//            case MapView.WILL_START_LOADING_MAP:
//            case MapView.WILL_START_RENDERING_MAP:
//            case MapView.WILL_START_RENDERING_FRAME:
//                Log.v(TAG, "onMapChanged @ " + change);
//                break;
//            case MapView.DID_FINISH_RENDERING_FRAME:
//            case MapView.DID_FINISH_RENDERING_FRAME_FULLY_RENDERED:
//                Log.v(TAG, "onMapChanged @ " + change);
//                break;
//            case MapView.DID_FINISH_RENDERING_MAP:
//            case MapView.DID_FINISH_RENDERING_MAP_FULLY_RENDERED:
//                Log.v(TAG, "onMapChanged @ " + change);
//                break;
        }
    }

    private void onMapRegionChangedByUser() {
        if (isSignificantChange() || isIgnoreMinDistance()) {
            mIgnoreMinDistance = false;
            updateMapData(vMap.getLatLng(), vMap.getZoom());
        }
    }

    private void onMapRegionChangedAnimated() {
        if (isSignificantChange() || isIgnoreMinDistance()) {
            mIgnoreMinDistance = false;
            updateMapData(vMap.getLatLng(), vMap.getZoom());
        }
    }

    private void onMapFinishedLoading() {
        mIgnoreMinDistance = false;

        try {
            MapUtils.setInitialCenterCoordinates(vMap, initialArguments);

            updateMapData(vMap.getLatLng(), vMap.getZoom());
        } catch (UnsupportedAreaException e) {
            if (!isDialogShown) {
                isDialogShown = listener.showCitiesDialog(vMap.getLatLng());
            }
        }
    }

    private void onMapFailedLoading() {
        Log.e(TAG, "onMapFailedLoading");
    }

    /**
     * Implements MapView.OnMapClickListener
     * Called when the user clicks on the map view.
     *
     * @param point The projected map coordinate the user clicked on.
     */
    @Override
    public void onMapClick(@NonNull LatLng point) {
        unselectFeatureIfNecessary();
        final boolean markerClicked = onNearestMarkerClick(point);

        if (!markerClicked && listener != null) {
            listener.hideMarkerInfo();
        }
    }

    private boolean onNearestMarkerClick(@NonNull LatLng point) {
        final Annotation annotation = MapUtils.getNearestAnnotation(point, vMap.getAllAnnotations());

        if (annotation != null) {
            final double distance = MapUtils.distanceTo(point, annotation);

            // threshold ranges between 13-25 metres
            final double threshold = Const.UiConfig.MIN_CLICK_DISTANCE +
                    3 * (MapView.MAXIMUM_ZOOM - vMap.getZoom());

            if (Double.compare(distance, threshold) < 0) {
                if (annotation instanceof Marker) {
                    // Nearest is a Marker, trigger onMarkerClick()
                    onMarkerClick((Marker) annotation);
                    return true;
                } else if (annotation instanceof Polyline) {
                    // Nearest is a Polyline, find its associated Marker
                    for (Map.Entry<String, List<Annotation>> entry : mFeatureAnnotsList.entrySet()) {
                        if (entry.getValue().contains(annotation)) {
                            for (Annotation a : entry.getValue()) {
                                if (a instanceof Marker) {
                                    onMarkerClick((Marker) a);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
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

        if (MapUtils.MARKER_ID_CHECKIN.equals(featureId)) {
            startActivity(CheckinActivity.newIntent(getActivity()));
            return true;
        } else if (MapUtils.MARKER_ID_SEARCH.equals(featureId)) {
            return true;
        }

        switch (mPrkngMapType) {
            case Const.MapSections.ON_STREET:
            case Const.MapSections.OFF_STREET:
            case Const.MapSections.CARSHARE_SPOTS:
                unselectFeatureIfNecessary();
                selectFeature(marker.getSnippet());

                if (listener != null) {
                    try {
                        // For carshare: If we can cast the feature, it means it's a spot not lot
                        final long isNumber = Long.valueOf(featureId);
                        listener.showMarkerInfo(marker, mPrkngMapType);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
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
        try {
            vProgressBar.setVisibility(View.GONE);

            if (isResumed() && mSearchMarker != null) {
                mSearchMarker = MapUtils.addSearchMarker(vMap,
                        mSearchMarker.getPosition(),
                        mapAssets.getSearchMarkerIcon());
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
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

        if (annotations == null || annotations.isEmpty()) {
            onEmptyAnnotations();
        }
    }

    @Override
    public void onFailure(PrkngApiError e) {
        if (e.isUnauthorized()) {
            startActivityForResult(LoginActivity.newIntent(getActivity()), Const.RequestCodes.AUTH_LOGIN);
        } else if (e.isNotFound()) {
            onUnsupportedArea(R.string.snackbar_unsupported_area);
        } else {
            e.showSnackbar(vMap);
        }
    }

    private void onUnsupportedArea(int message) {
        mSnackbar = RedSnackbar.make(vMap, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_available_cities,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isDialogShown) {
                                    isDialogShown = listener.showCitiesDialog(vMap.getLatLng());
                                }
                            }
                        });
        mSnackbar.show();
    }

    private void onEmptyAnnotations() {
        if (Const.MapSections.ON_STREET == mPrkngMapType) {
            if (Double.compare(Const.UiConfig.SPOTS_MIN_ZOOM, vMap.getZoom()) < 0) {
                // First, check zoom level
                mSnackbar = RedSnackbar.make(vMap,
                        R.string.snackbar_on_street_zoom_out,
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_zoom_out,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        vMap.setZoom(Const.UiConfig.SPOTS_MIN_ZOOM, true);
                                    }
                                });
                mSnackbar.show();
            } else if (Float.compare(getDurationFilter(), Const.UiConfig.DEFAULT_DURATION) > 0) {
                // Second, check duration filter
                mSnackbar = RedSnackbar.make(vMap,
                        String.format(
                                getString(R.string.snackbar_on_street_duration_empty_result),
                                (int) getDurationFilter()),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_select_duration,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        listener.showDurationDialog();
                                    }
                                });
                mSnackbar.show();
            }
        } else if (Const.MapSections.OFF_STREET == mPrkngMapType) {
            if (Double.compare(Const.UiConfig.LOTS_MIN_ZOOM, vMap.getZoom()) < 0) {
                // First, check zoom level
                mSnackbar = RedSnackbar.make(vMap,
                        R.string.snackbar_off_street_zoom_out,
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_zoom_out,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        vMap.setZoom(Const.UiConfig.LOTS_MIN_ZOOM, true);
                                    }
                                });
                mSnackbar.show();
            } else {
                // Second, search for nearest lot
                mSnackbar = RedSnackbar.make(vMap,
                        R.string.snackbar_off_street_empty_result,
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_view_nearest,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        updateMapData(vMap.getLatLng(), vMap.getZoom(), true);
                                    }
                                });
                mSnackbar.show();
            }
        }
    }

    @Override
    public void setCenterCoordinate(LatLng center) {
        setCenterCoordinate(new LatLngZoom(
                center.getLatitude(),
                center.getLongitude(),
                vMap.getZoom()
        ));
    }

    public void setCenterCoordinate(final LatLngZoom center) {
        vMap.removeOnMapChangedListener(this);

        vMap.setLatLng(center, true);

        vMap.postDelayed(new Runnable() {
            @Override
            public void run() {
                vMap.addOnMapChangedListener(MainMapFragment.this);
                mLastMapGeometry.setLatitude(center.getLatitude());
                mLastMapGeometry.setLongitude(center.getLongitude());
                mLastMapGeometry.setZoomAndRadius(center.getZoom(), vMap.fromScreenLocation(new PointF(0, 0)));
            }
        }, ANIMATION_DURATION);
    }

    @Override
    public float getDurationFilter() {
// TODO refactor?
        return PrkngApp.getInstance(getActivity()).getMapDurationFilter();
    }

    public Location getUserLocation() {
        return vMap != null ? vMap.getMyLocation() : null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivityForResult(SearchActivity.newIntent(getActivity(), vMap.getLatLng()),
                    Const.RequestCodes.SEARCH);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestPermissionIfNeeded() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            mSnackbar = RedSnackbar.make(vMap, R.string.snackbar_location_permission_needed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermission();
                                }
                            });
            mSnackbar.show();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Const.RequestCodes.PERMISSION_ACCESS_LOCATION == requestCode) {
            if (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                vMap.setMyLocationEnabled(MY_LOCATION_ENABLED);
                vMap.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);

                vMap.setOnMyLocationChangeListener(new MapView.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(@Nullable Location location) {
                        if (location != null) {
                            vMap.setOnMyLocationChangeListener(null);
                            vMap.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()), true);
                        } else {
                            Log.e(TAG, "Location is null");
                        }
                    }
                });
            }
        }
    }

    public void requestPermission() {
        // Permission has not been granted yet. Request it directly.
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Const.RequestCodes.PERMISSION_ACCESS_LOCATION);
    }

    /**
     * Verifies if changes to Zoom (0.4) or distance to center (25m) are worth an update.
     * Also verifies if user has crossed over the SMALL_BUTTONS_ZOOM value while zooming in/out.
     *
     * @return true if changes are noteworthy
     */
    private boolean isSignificantChange() {
        double centerDistance = Math.round(mLastMapGeometry.distanceTo(vMap.getLatLng()));
        double zoomChange = mLastMapGeometry.getZoom() - vMap.getZoom();

        // Toggle if the two zooms are on the same "side" of SMALL_BUTTONS_ZOOM (17)
        // Ex1, TRUE:  compare(17, 15) * compare(17, 19) = 1 * -1 = -1
        // Ex2, FALSE: compare(17, 18) * compare(17, 19) = 1 *  1 =  1
        boolean toggleButtonsVisibility = (Double.compare(Const.UiConfig.SMALL_BUTTONS_ZOOM, mLastMapGeometry.getZoom())
                * Double.compare(Const.UiConfig.SMALL_BUTTONS_ZOOM, vMap.getZoom())) < 0;

        return Double.compare(zoomChange, 0.4d) >= 0 ||
                Double.compare(centerDistance, Const.UiConfig.MIN_UPDATE_DISTACE) >= 0 ||
                toggleButtonsVisibility;
    }

    private boolean isIgnoreMinDistance() {
        return mIgnoreMinDistance;
    }

    private boolean isOutsideCityArea(LatLng latLng) {
        if (mCurrentCity == null) {
            mCurrentCity = CityBoundsHelper.getNearestCity(getContext(), latLng);
            if (mCurrentCity != null) {
                PrkngPrefs.getInstance(getActivity())
                        .setCity(mCurrentCity.getName());
            }
        }

        return !mCurrentCity.containsInRadius(latLng);
    }

    public void forceUpdate(LatLngZoom point) {
        if (vMap == null) {
            return;
        }

        if (point != null) {
            updateMapData(point, point.getZoom(), true);
        } else {
            updateMapData(vMap.getLatLng(), vMap.getZoom(), true);
        }
    }

    private void updateMapData(LatLng latLng, double zoom) {
        updateMapData(latLng, zoom, false);
    }

    private void updateMapData(LatLng latLng, double zoom, boolean forced) {
        if (!isResumed()) {
            return;
        }

        if (!ConnectionUtils.hasConnection(getActivity())) {
            showConnectionError();
            return;
        }

        if (isOutsideCityArea(latLng) || Double.compare(zoom, Const.UiConfig.AVAILABLE_CITIES_MIN_ZOOM) < 0) {
            if (!isDialogShown) {
                isDialogShown = listener.showCitiesDialog(latLng);
            }
            return;
        } else if (Double.compare(zoom, Const.UiConfig.CLEAR_MAP_MIN_ZOOM) < 0) {
            vMap.removeAllAnnotations();
        }

        if (MapUtils.isMinZoom(zoom, mPrkngMapType)) {
            mIsZoomTooLow = false;
            if (mSnackbar != null) {
                mSnackbar.dismiss();
            }
            if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
                mTask.cancel(false);
            }

            mLastMapGeometry.setLatitude(latLng.getLatitude());
            mLastMapGeometry.setLongitude(latLng.getLongitude());
            mLastMapGeometry.setZoomAndRadius(zoom, vMap.fromScreenLocation(new PointF(0, 0)));

            switch (mPrkngMapType) {
                case Const.MapSections.OFF_STREET:
                    mTask = forced ? new NearestLotsDownloadTask(vMap, mapAssets, this) :
                            new LotsDownloadTask(vMap, mapAssets, this);
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

            mHandler.removeCallbacks(mRunnable);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    mTask.execute(mLastMapGeometry);
                }
            };
            mHandler.postDelayed(mRunnable, 500);

        } else if (!mIsZoomTooLow || (mSnackbar != null && !mSnackbar.isShown())) {
            mIsZoomTooLow = true;
            mIgnoreMinDistance = true;
            mSnackbar = RedSnackbar.make(vMap, R.string.snackbar_map_zoom_needed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mIsZoomTooLow = false;
                            vMap.setZoom(MapUtils.getMinZoomPerType(mPrkngMapType), true);
                        }
                    });
            mSnackbar.show();
        }
    }

    private void moveToMyLocation(boolean animated) {
        if (!vMap.isMyLocationEnabled()) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                vMap.setMyLocationEnabled(MY_LOCATION_ENABLED);
                vMap.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
            }
        }
        final Location myLocation = vMap.getMyLocation();
        if (myLocation != null) {
            final LatLng latLng = new LatLng(myLocation.getLatitude(),
                    myLocation.getLongitude());
            final City nearestCity = CityBoundsHelper.getNearestCity(getContext(),
                    new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            if (nearestCity.containsInRadius(latLng)) {
                mCurrentCity = nearestCity;
                vMap.setLatLng(new LatLngZoom(
                                latLng,
                                Math.max(Const.UiConfig.MY_LOCATION_ZOOM, vMap.getZoom())
                        ),
                        animated);
                vFab.clearColorFilter();
            } else {
                vFab.setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.anthracite2));
                onUnsupportedArea(R.string.snackbar_unsupported_my_location);
            }
        }
    }

    public void setMapType(int type) {
        if (type != mPrkngMapType) {
            vMap.removeAllAnnotations();
            mPrkngMapType = type;
            final double zoom = MapUtils.getMinZoomPerType(type);

            updateMapData(vMap.getLatLng(), zoom, true);
            vMap.setZoom(zoom, true);

            AnalyticsUtils.sendFragmentView(this, mPrkngMapType);
        }
    }

    private void selectFeature(String featureId) {
        if (mFeatureAnnotsList == null) {
            return;
        }

        // Store the selected feature's ID, for restore
        mSelectedFeature = new SelectedFeature(featureId);

        final List<Annotation> annotations = mFeatureAnnotsList.get(featureId);
        if (annotations == null) {
            return;
        }

        // Reset the selection array
        mSelectedAnnotsList = new ArrayList<>();

        Icon icon;
        switch (mPrkngMapType) {
            case Const.MapSections.OFF_STREET:
                icon = mapAssets.getLotMarkerIconSelected(Const.UNKNOWN_VALUE);
                break;
            case Const.MapSections.ON_STREET:
            default:
                icon = mapAssets.getMarkerIconSelected();
                break;
        }

        for (Annotation annot : annotations) {
            if (annot instanceof Marker) {
                final Marker m = ((Marker) annot);

                if (!m.getIcon().getId().equals(mapAssets.getMarkerIconTransparent().getId())) {
                    // Store the selected feature's marker Icon, for restore
                    mSelectedFeature.setMarkerIcon(m.getIcon());

                    // Change the marker's Icon
                    final Marker selectedMarker = vMap.addMarker(
                            MapUtils.extractMarkerOptions(m)
                                    .icon(icon)
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
