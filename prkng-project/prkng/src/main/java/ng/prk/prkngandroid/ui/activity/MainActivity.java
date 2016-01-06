package ng.prk.prkngandroid.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.ui.fragment.MainMapFragment;
import ng.prk.prkngandroid.ui.view.SlidingUpMarkerInfo;
import ng.prk.prkngandroid.util.Installation;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class MainActivity extends BaseActivity implements
        MainMapFragment.OnMapMarkerClickListener,
        TabLayout.OnTabSelectedListener {
    private static final String TAG = "MainActivity";

    private SlidingUpMarkerInfo vSlidingUpMarkerInfo;
    private MainMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_bar_main);

        helloApi();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_off_street), Const.MapSections.OFF_STREET);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_on_street), Const.MapSections.ON_STREET, true);
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_carshare_spots), Const.MapSections.CARSHARE_SPOTS);
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_carshare_vehicles), Const.MapSections.CARSHARE_VEHICLES);

        vSlidingUpMarkerInfo = (SlidingUpMarkerInfo) findViewById(R.id.sliding_layout);

        final FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MainMapFragment) fm.findFragmentByTag(Const.FragmentTags.MAP);
        if (mapFragment == null) {
            mapFragment = MainMapFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.content_frame, mapFragment, Const.FragmentTags.MAP)
                    .commit();
        }

        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Hide the SlidingPanel, if expanded
     */
    @Override
    public void onBackPressed() {
        if (vSlidingUpMarkerInfo.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            vSlidingUpMarkerInfo.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
            vSlidingUpMarkerInfo.setMarkerInfo(marker.getSnippet(), marker.getTitle(), type);
            if (vSlidingUpMarkerInfo.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                vSlidingUpMarkerInfo.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }
    }

    /**
     * Implements MainMapFragment.OnMapMarkerClickListener
     */
    @Override
    public void hideMarkerInfo() {
        vSlidingUpMarkerInfo.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
                    null);
        }
    }

}
