package ng.prk.prkngandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

import ng.prk.prkngandroid.Const;

public class PrkngPrefs implements
        Const.PrefsNames,
        Const.PrefsValues {
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

    public String getApiKey() {
        return mPrefs.getString(API_KEY, null);
    }

    public void setApiKey(String apiKey) {
        edit().putString(API_KEY, apiKey)
                .apply();
    }

    private SharedPreferences.Editor edit() {
        if (mPrefsEditor == null) {
            mPrefsEditor = mPrefs.edit();
        }

        return mPrefsEditor;
    }
}
