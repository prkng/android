package ng.prk.prkngandroid.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.LoginEmailActivity;

public class SettingsFragment extends PreferenceFragment implements
        Const.PrefsNames,
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private SharedPreferences mPrefs;
    private Preference pToggleLogin;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PreferenceManager pm = this.getPreferenceManager();
        pm.setSharedPreferencesName(Const.APP_PREFS_NAME);
        pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.prefs_settings);

        mPrefs = pm.getSharedPreferences();
        pToggleLogin = findPreference(TOGGLE_LOGIN);

        // Listeners
        pToggleLogin.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Set up a listener whenever a key changes
         */
        if (mPrefs!= null) {
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        /**
         * Remove the listener onPause
         */
        if (mPrefs != null) {
            mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }


    /**
     * Implements OnPreferenceClickListener
     *
     * @param preference
     * @return
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        if (key == null) {
            return false;
        }

        if (TOGGLE_LOGIN.equals(key)) {
            startActivity(LoginEmailActivity.newIntent(getActivity()));
            return true;
        }

        return false;
    }

    /**
     * Implements OnSharedPreferenceChangeListener
     *
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
