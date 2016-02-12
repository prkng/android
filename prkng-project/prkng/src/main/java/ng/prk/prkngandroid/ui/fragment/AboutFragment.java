package ng.prk.prkngandroid.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import hotchemi.android.rate.AppRate;
import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.TutorialActivity;
import ng.prk.prkngandroid.ui.activity.WebViewActivity;

public class AboutFragment extends PreferenceFragment implements
        Const.PrefsNames, Preference.OnPreferenceClickListener {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PreferenceManager pm = this.getPreferenceManager();
        pm.setSharedPreferencesName(Const.APP_PREFS_NAME);
        pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.prefs_about);

        findPreference(ONBOARDING).setOnPreferenceClickListener(this);
        findPreference(TERMS).setOnPreferenceClickListener(this);
        findPreference(FAQ).setOnPreferenceClickListener(this);
        findPreference(PRIVACY).setOnPreferenceClickListener(this);
        findPreference(RATE_APP).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        if (ONBOARDING.equals(key)) {
            startActivity(TutorialActivity.newIntent(getActivity(), false));
        } else if (RATE_APP.equals(key)) {
            AppRate.setRateDialogAgreed(getActivity());
            /**
             * Launch Playstore to rate app
             */
            final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(getResources().getString(R.string.url_playstore)));
            startActivity(viewIntent);
        } else if (TERMS.equals(key) || FAQ.equals(key) || PRIVACY.equals(key)) {
            startActivity(WebViewActivity.newIntent(getActivity(), key));
        }

        return false;
    }
}
