package ng.prk.prkngandroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
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


    private SharedPreferences.Editor edit() {
        if (mPrefsEditor == null) {
            mPrefsEditor = mPrefs.edit();
        }

        return mPrefsEditor;
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

    public void registerPrefsChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterPrefsChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
