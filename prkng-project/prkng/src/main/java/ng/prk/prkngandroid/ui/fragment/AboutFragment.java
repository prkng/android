package ng.prk.prkngandroid.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();

        if (ONBOARDING.equals(key)) {
            startActivity(TutorialActivity.newIntent(getActivity(), false));
        } else if (TERMS.equals(key) || FAQ.equals(key)) {
            startActivity(WebViewActivity.newIntent(getActivity(), key));
        }

        return false;
    }
}
