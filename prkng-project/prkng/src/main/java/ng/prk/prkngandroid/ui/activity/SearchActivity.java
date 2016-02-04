package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.mapbox.geocoder.service.models.GeocoderFeature;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.ui.adapter.GeocoderAdapter;

public class SearchActivity extends BaseActivity implements
        AdapterView.OnItemClickListener {

    private static final String TAG = "SearchActivity";

    private AppCompatAutoCompleteTextView vAutoComplete;
    private GeocoderAdapter mAdapter;

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, SearchActivity.class);
//        final Bundle extras = new Bundle();
//        extras.putString(Const.BundleKeys.PAGE, page);
//        intent.putExtras(extras);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        mAdapter = new GeocoderAdapter(this);

        vAutoComplete = (AppCompatAutoCompleteTextView) findViewById(R.id.autocomplete);
        vAutoComplete.setLines(1);
        vAutoComplete.setAdapter(mAdapter);
        vAutoComplete.setOnItemClickListener(this);
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
        Log.v(TAG, "onItemClick, position: " + position
                + ", id: " + id);

        GeocoderFeature result = mAdapter.getItem(position);
        vAutoComplete.setText(result.getText());
    }
}
