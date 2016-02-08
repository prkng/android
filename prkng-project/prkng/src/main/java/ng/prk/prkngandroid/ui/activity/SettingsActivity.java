package ng.prk.prkngandroid.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.fragment.SettingsFragment;
import ng.prk.prkngandroid.util.AnalyticsUtils;

public class SettingsActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Fragment fragment = SettingsFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment, Const.FragmentTags.SETTINGS)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtils.sendActivityView(this);
    }
}
