package ng.prk.prkngandroid;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class PrkngApp extends Application {
    private static final String TAG = "PrkngApp";
    private float mapDurationFilter;

    public static PrkngApp getInstance(Context context) {
        if (context instanceof PrkngApp) {
            return (PrkngApp) context;
        } else {
            return (PrkngApp) context.getApplicationContext();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        PrkngPrefs.setDefaults(this);

        mapDurationFilter = Const.UiConfig.DEFAULT_DURATION;
    }

    public float getMapDurationFilter() {
        return mapDurationFilter;
    }

    public void setMapDurationFilter(float duration) {
        this.mapDurationFilter = duration;
        PrkngPrefs.getInstance(this).setDuration(duration);
    }
}
