package ng.prk.prkngandroid.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.fragment.MainMapFragment;

public class MainActivity extends AppCompatActivity {

    private SlidingUpPanelLayout vSlidingUpPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_bar_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_off_street), Const.MapSections.OFF_STREET);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_on_street), Const.MapSections.ON_STREET);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_carshare), Const.MapSections.CARSHARE);

        vSlidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.MAP);
            if (fragment == null) {
                fragment = MainMapFragment.newInstance();
                fm.beginTransaction()
                        .replace(R.id.content_frame, fragment, Const.FragmentTags.MAP)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            vSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (item.getItemId() == R.id.action_about) {
            vSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }

        return super.onOptionsItemSelected(item);
    }
}
