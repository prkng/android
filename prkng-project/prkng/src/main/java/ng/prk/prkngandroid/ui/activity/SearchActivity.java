package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.model.base.SearchItem;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.ui.adapter.GeocoderAdapter;
import ng.prk.prkngandroid.util.CityBoundsHelper;
import ng.prk.prkngandroid.util.MapUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class SearchActivity extends BaseActivity implements
        AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "SearchActivity";

    private AppCompatAutoCompleteTextView vAutoComplete;
    private GeocoderAdapter mAdapter;

    public static Intent newIntent(Context context, LatLng proximity) {
        final Intent intent = new Intent(context, SearchActivity.class);

        final Bundle extras = new Bundle();
        extras.putDouble(Const.BundleKeys.LATITUDE, proximity.getLatitude());
        extras.putDouble(Const.BundleKeys.LONGITUDE, proximity.getLongitude());
        intent.putExtras(extras);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        setupAutocomplete();

        findViewById(R.id.btn_nav_back).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        vAutoComplete.requestFocus();
    }

    /**
     * Implements AutoCompleteTextView.OnItemClickListener
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onSearchItemSelected(mAdapter.getItem(position));
    }

    private void onSearchItemSelected(SearchItem item) {
        vAutoComplete.setText(item.getName());

        final Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_TITLE, item.getName());
        bundle.putDouble(Const.BundleKeys.LATITUDE, item.getLatLng().getLatitude());
        bundle.putDouble(Const.BundleKeys.LONGITUDE, item.getLatLng().getLongitude());
        data.putExtras(bundle);

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupAutocomplete() {
        mAdapter = new GeocoderAdapter(this, getProximityPoint());

        vAutoComplete = (AppCompatAutoCompleteTextView) findViewById(R.id.autocomplete);
        vAutoComplete.setLines(1);
        vAutoComplete.setAdapter(mAdapter);
        vAutoComplete.setOnItemClickListener(this);

        vAutoComplete.setOnEditorActionListener(this);
    }

    private LatLng getProximityPoint() {
        final Bundle extras = getIntent().getExtras();

        LatLng proximity = null;
        if (extras != null) {
            proximity = MapUtils.getBundleGeoPoint(extras);
        }

        if (proximity == null) {
            final City city = CityBoundsHelper.getCityByName(this,
                    PrkngPrefs.getInstance(this).getCity());
            if (city != null) {
                proximity = city.getLatLng();
            }
        }

        return proximity;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_nav_back) {
            onBackPressed();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
            onSearchItemSelected(mAdapter.getItem(0));
            return true;
        }

        return false;
    }
}
