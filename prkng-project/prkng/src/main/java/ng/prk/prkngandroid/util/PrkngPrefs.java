package ng.prk.prkngandroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.LoginObject;

public class PrkngPrefs implements
        Const.PrefsNames,
        Const.PrefsValues {
    private static final String TAG = "PrkngPrefs";

    private static PrkngPrefs instance;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    private PrkngPrefs(Context appContext) {
        mPrefs = appContext.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static PrkngPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new PrkngPrefs(context);
        }
        return instance;
    }

    public static void setDefaults(Context context) {
        PreferenceManager.setDefaultValues(context,
                Const.APP_PREFS_NAME,
                Context.MODE_PRIVATE,
                R.xml.prefs_defaults,
                false);
    }

    public String getApiKey() {
        return mPrefs.getString(AUTH_API_KEY, null);
    }

    public String getAuthUserName() {
        return mPrefs.getString(AUTH_USER_NAME, null);
    }

    public String getAuthUserEmail() {
        return mPrefs.getString(AUTH_USER_EMAIL, null);
    }

    /**
     * Authentication data is stored using commit() to avoid delay when leaving login activity
     *
     * @param user
     */
    public void setAuthUser(LoginObject user) {
        if (user == null || user.getApikey() == null || user.getApikey().isEmpty()) {
            edit().putString(AUTH_API_KEY, null)
                    .putString(AUTH_USER_NAME, null)
                    .putString(AUTH_USER_EMAIL, null)
                    .putString(AUTH_USER_PICTURE, null)
                    .commit();
        } else {
            edit().putString(AUTH_API_KEY, user.getApikey())
                    .putString(AUTH_USER_NAME, user.getName())
                    .putString(AUTH_USER_EMAIL, user.getEmail())
                    .putString(AUTH_USER_PICTURE, user.getImageUrl())
                    .commit();
        }
    }

    public void setCheckout(long id) {
        edit().putLong(CHECKIN_ID, Const.UNKNOWN_VALUE)
                .putString(CHECKIN_ADDRESS, null)
                .putLong(CHECKIN_START_AT, Const.UNKNOWN_VALUE)
                .putLong(CHECKIN_END_AT, Const.UNKNOWN_VALUE)
                .putLong(CHECKIN_SMART_REMINDER, Const.UNKNOWN_VALUE)
                .commit();
        edit().remove(CHECKIN_ID)
                .remove(CHECKIN_ADDRESS)
                .remove(CHECKIN_START_AT)
                .remove(CHECKIN_END_AT)
                .remove(CHECKIN_SMART_REMINDER)
                .apply();
    }

    public void setCheckin(CheckinData checkin, String address) {
        setCheckin(checkin, address, Const.UNKNOWN_VALUE);
    }

    public void setCheckin(CheckinData checkin, String address, long endAt) {
        edit().putLong(CHECKIN_ID, checkin.getId())
                .putString(CHECKIN_ADDRESS, address)
                .putLong(CHECKIN_LAT, Double.doubleToRawLongBits(checkin.getLatitude()))
                .putLong(CHECKIN_LNG, Double.doubleToRawLongBits(checkin.getLongitude()));

        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(checkin.getCheckinAt())) {
            edit().remove(CHECKIN_START_AT);
        } else {
            edit().putLong(CHECKIN_START_AT, checkin.getCheckinAt());
        }
        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(endAt)) {
            // For week-long durations (allowed at all times), ignore end time
            edit().remove(CHECKIN_END_AT);
        } else {
            edit().putLong(CHECKIN_END_AT, endAt);
        }

        edit().apply();
    }

    public CheckinData getCheckinData() {
        final long id = mPrefs.getLong(CHECKIN_ID, Const.UNKNOWN_VALUE);
        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(id)) {
            return null;
        }
        final double lat = Double.longBitsToDouble(mPrefs.getLong(CHECKIN_LAT, Const.UNKNOWN_VALUE));
        final double lng = Double.longBitsToDouble(mPrefs.getLong(CHECKIN_LNG, Const.UNKNOWN_VALUE));

        return new CheckinData(id,
                mPrefs.getLong(CHECKIN_START_AT, Const.UNKNOWN_VALUE),
                mPrefs.getLong(CHECKIN_END_AT, Const.UNKNOWN_VALUE),
                mPrefs.getString(CHECKIN_ADDRESS, null),
                new LatLng(lat, lng)
        );
    }

    private SharedPreferences.Editor edit() {
        if (mPrefsEditor == null) {
            mPrefsEditor = mPrefs.edit();
        }

        return mPrefsEditor;
    }

    public boolean isCharshareMode() {
        return mPrefs.getBoolean(CARSHARE_MODE, true);
    }

    public void setCarshareMode(boolean enabled) {
        edit().putBoolean(CARSHARE_MODE, enabled)
                .apply();
    }

    public String[] getCarshareCompanies(Resources res) {
        final List<String> companies = new ArrayList<>();
        final String presKeyTemplate = res.getString(R.string.prefs_carshare_template);

        for (String company : res.getStringArray(R.array.carshare_companies)) {
            final boolean enabled = mPrefs.getBoolean(String.format(presKeyTemplate, company), false);
            if (enabled) {
                companies.add(company);
            }
        }

        if (companies.size() > 0) {
            return companies.toArray(new String[companies.size()]);
        }

        return null;
    }

    public boolean isOnboarding() {
        final boolean isInitialLaunch = mPrefs.getBoolean(IS_ONBOARDING, true);

        edit().putBoolean(IS_ONBOARDING, false)
                .apply();

        return isInitialLaunch;
    }

    public boolean isLoggedIn() {
        final String apiKey = getApiKey();

        return (apiKey != null) && !apiKey.isEmpty();
    }

    public float getDuration() {
        return mPrefs.getFloat(DURATION, Const.UiConfig.DEFAULT_DURATION);
    }

    public String getCity() {
        return mPrefs.getString(CITY, Const.UiConfig.DEFAULT_CITY);
    }

    public void setCity(String city) {
        edit().putString(CITY, city).apply();
    }

    public void setDuration(float duration) {
        edit().putFloat(DURATION, duration).apply();
    }

    public boolean hasNotifications() {
        return mPrefs.getBoolean(NOTIFY_EXPIRY, true);
    }

    public boolean hasSmartReminder() {
        return mPrefs.getBoolean(NOTIFY_SMART_REMINDER, true);
    }

    public void setSmartReminder(boolean enabled) {
        edit().putBoolean(NOTIFY_SMART_REMINDER, enabled)
                .commit();
    }

    public void registerPrefsChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterPrefsChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

}
