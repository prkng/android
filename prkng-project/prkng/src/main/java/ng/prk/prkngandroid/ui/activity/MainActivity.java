package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiCallback;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.ui.fragment.LotInfoFragment;
import ng.prk.prkngandroid.ui.fragment.MainMapFragment;
import ng.prk.prkngandroid.ui.fragment.SpotInfoFragment;
import ng.prk.prkngandroid.util.Installation;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class MainActivity extends BaseActivity implements
        OnMarkerInfoClickListener,
        MainMapFragment.OnMapMarkerClickListener,
        TabLayout.OnTabSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";

    private MainMapFragment mapFragment;

    public static Intent newIntent(Context context, LatLng center) {
        return newIntent(context, new LatLngZoom(center, Const.UiConfig.MY_LOCATION_ZOOM));
    }

    public static Intent newIntent(Context context, LatLngZoom center) {
        final Intent intent = new Intent(context, MainActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putDouble(Const.BundleKeys.LATITUDE, center.getLatitude());
        bundle.putDouble(Const.BundleKeys.LONGITUDE, center.getLongitude());
        bundle.putDouble(Const.BundleKeys.ZOOM, center.getZoom());
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        helloApi();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_on_street), Const.MapSections.ON_STREET, true);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_off_street), Const.MapSections.OFF_STREET);
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_carshare_spots), Const.MapSections.CARSHARE_SPOTS);
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_carshare_vehicles), Const.MapSections.CARSHARE_VEHICLES);

        final FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MainMapFragment) fm.findFragmentByTag(Const.FragmentTags.MAP);
        if (mapFragment == null) {
            final double latitude = getIntent().getDoubleExtra(Const.BundleKeys.LATITUDE, Const.UNKNOWN_VALUE);
            final double longitude = getIntent().getDoubleExtra(Const.BundleKeys.LONGITUDE, Const.UNKNOWN_VALUE);
            final double zoom = getIntent().getDoubleExtra(Const.BundleKeys.ZOOM, Const.UNKNOWN_VALUE);

            if (Double.valueOf(Const.UNKNOWN_VALUE).equals(latitude) || Double.valueOf(Const.UNKNOWN_VALUE).equals(longitude)) {
                mapFragment = MainMapFragment.newInstance();
            } else {
                mapFragment = MainMapFragment.newInstance(new LatLngZoom(latitude, longitude, zoom));
            }
            fm.beginTransaction()
                    .replace(R.id.map_frame, mapFragment, Const.FragmentTags.MAP)
                    .commit();
        }

        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrkngPrefs.getInstance(this).registerPrefsChangeListener(this);
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();

        PrkngPrefs.getInstance(this).unregisterPrefsChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_user_activity)
                .setIcon(getUserActivityIcon());

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.CHECKIN_ID.equals(key)) {
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(Fragment fragment) {
        Fragment clone;
        if (fragment instanceof SpotInfoFragment) {
            clone = SpotInfoFragment.clone((SpotInfoFragment) fragment);
        } else if (fragment instanceof LotInfoFragment) {
            clone = LotInfoFragment.clone((LotInfoFragment) fragment);
        } else {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.expand_up, R.anim.collapse_down,
                        R.anim.expand_up, R.anim.collapse_down)
                .replace(android.R.id.content, clone, Const.FragmentTags.MAP_INFO_EXPANDED)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.MAP_INFO_EXPANDED);
        if (fragment != null) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Implements MainMapFragment.OnMapMarkerClickListener
     *
     * @param marker
     */
    @Override
    public void showMarkerInfo(Marker marker, int type) {
        if (marker == null) {
            hideMarkerInfo();
        } else {
//setMarkerInfo(marker.getSnippet(), marker.getTitle(), type)
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();

            Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.MAP_INFO);
            if (fragment == null) {
                ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            }

            switch (type) {
                case Const.MapSections.OFF_STREET:
                    fragment = LotInfoFragment.newInstance(
                            marker.getSnippet(), marker.getTitle());
                    break;
                case Const.MapSections.ON_STREET:
                    fragment = SpotInfoFragment.newInstance(
                            marker.getSnippet(), marker.getTitle());
                    break;
                default:
                    fragment = null;
                    break;
            }

            ft.replace(R.id.map_info_frame, fragment, Const.FragmentTags.MAP_INFO)
                    .commit();

            Log.e(TAG, "TODO showMarkerInfo");
        }
    }

    /**
     * Implements MainMapFragment.OnMapMarkerClickListener
     */
    @Override
    public void hideMarkerInfo() {
        Log.e(TAG, "TODO hideMarkerInfo");
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.MAP_INFO);
        if (fragment != null && fragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .remove(fragment)
                    .commit();
        }
    }

    /**
     * Implements TabLayout.OnTabSelectedListener
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mapFragment.setMapType(tab.getPosition());
    }

    /**
     * Implements TabLayout.OnTabSelectedListener
     *
     * @param tab
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        hideMarkerInfo();
    }

    /**
     * Implements TabLayout.OnTabSelectedListener
     *
     * @param tab
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void helloApi() {
        final String apiKey = PrkngPrefs.getInstance(this).getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            final String deviceId = Installation.id(this);
            ApiClient.hello(
                    ApiClient.getService(),
                    apiKey,
                    deviceId,
                    new ApiCallback<Void>(this, findViewById(R.id.root_view)));
        }
    }

    private int getUserActivityIcon() {
        final CheckinData checkin = PrkngPrefs.getInstance(this)
                .getCheckinData();

        return checkin == null ? R.drawable.ic_action_user_activity_none
                : R.drawable.ic_action_user_activity;
    }
}
