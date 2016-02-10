package ng.prk.prkngandroid.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import ng.prk.prkngandroid.util.CarshareUtils;
import ng.prk.prkngandroid.util.CityBoundsHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class SettingsFragment extends PreferenceFragment implements
        Const.PrefsNames,
        Const.PrefsValues,
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    private static final String TAG = "SettingsFragment";

    private SharedPreferences mPrefs;
    private Preference pToggleLogin;
    private Preference pCity;
    private SwitchPreference pCarshareMode;

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
        pCity = findPreference(CITY);
        pCarshareMode = (SwitchPreference) findPreference(CARSHARE_MODE);

        // Listeners
        pToggleLogin.setOnPreferenceClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        setupSummaries();
        setupCarshareCompanies();

        /**
         * Set up a listener whenever a key changes
         */
        if (mPrefs != null) {
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
            final String apiKey = mPrefs.getString(AUTH_API_KEY, null);
            if (apiKey == null || apiKey.isEmpty()) {
                startActivityForResult(LoginActivity.newIntent(getActivity()), Const.RequestCodes.AUTH_LOGIN);
            } else {
                PrkngPrefs.getInstance(getActivity()).setAuthUser(null);
            }
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

        if (CITY.equals(key)) {
            pCity.setSummary(getCitySummary(getResources(), mPrefs.getString(CITY, null)));
            setupCarshareCompanies();
        } else if (AUTH_USER_EMAIL.equals(key)) {
            pToggleLogin.setSummary(mPrefs.getString(AUTH_USER_EMAIL, null));
        } else if (AUTH_API_KEY.equals(key)) {
            final String apiKey = mPrefs.getString(AUTH_API_KEY, null);
            final boolean isLoggedIn = apiKey != null && !apiKey.isEmpty();
            pToggleLogin.setTitle(isLoggedIn ? R.string.prefs_logout_title : R.string.prefs_login_title);
        } else if (CarshareUtils.isCarsharePrefsChange(key)) {
            if (isEmptyCarshareMode()) {
                pCarshareMode.setChecked(false);
                ((CheckBoxPreference) findPreference(key)).setChecked(true);
            }
        }
    }

    private void setupSummaries() {
        pCity.setSummary(getCitySummary(getResources(), mPrefs.getString(CITY, null)));

        setLoginTitleSummary();
    }

    private void setupCarshareCompanies() {
        try {
            final String presKeyTemplate = getResources().getString(R.string.prefs_carshare_template);

            final City city = CityBoundsHelper.getCityByName(getActivity(), mPrefs.getString(CITY, null));
            final List<String> cityCompanies = city.getCarshare();

            for (String company : getResources().getStringArray(R.array.carshare_companies)) {
                findPreference(String.format(presKeyTemplate, company)).
                        setEnabled(cityCompanies.contains(company));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmptyCarshareMode() {
        try {
            final String presKeyTemplate = getResources().getString(R.string.prefs_carshare_template);

            for (String company : getResources().getStringArray(R.array.carshare_companies)) {
                CheckBoxPreference checkBox = (CheckBoxPreference) findPreference(String.format(presKeyTemplate, company));
                if (checkBox.isEnabled() && checkBox.isChecked()) {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void setLoginTitleSummary() {
        final String apiKey = mPrefs.getString(AUTH_API_KEY, null);
        if (apiKey == null || apiKey.isEmpty()) {
            pToggleLogin.setTitle(getResources().getString(R.string.prefs_login_title));
            pToggleLogin.setSummary(null);
        } else {
            pToggleLogin.setTitle(getResources().getString(R.string.prefs_logout_title));
            pToggleLogin.setSummary(
                    mPrefs.getString(AUTH_USER_EMAIL, null));
        }
    }

    private String getCitySummary(Resources res, String value) {
        switch (value) {
            case CITY_MONTREAL:
                return res.getString(R.string.city_montreal);
            case CITY_NEW_YORK:
                return res.getString(R.string.city_new_york);
            case CITY_QUEBEC:
                return res.getString(R.string.city_quebec);
            case CITY_SEATTLE:
                return res.getString(R.string.city_seattle);
        }

        return null;
    }

}
