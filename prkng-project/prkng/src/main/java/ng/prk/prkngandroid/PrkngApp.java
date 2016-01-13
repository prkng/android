package ng.prk.prkngandroid;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class PrkngApp extends Application {
    private float mapDurationFilter;

    private static PrkngApp instance;

    public static PrkngApp getInstance(Context context) {
// TODO verify memory leaks
//        return (PrkngApp) context.getApplicationContext();
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        PrkngPrefs.setDefaults(this);

        mapDurationFilter = Const.UiConfig.DURATIONS[Const.UiConfig.DEFAULT_DURATION_INDEX];
    }

    public float getMapDurationFilter() {
        return mapDurationFilter;
    }

    public void setMapDurationFilter(float duration) {
        this.mapDurationFilter = duration;
        PrkngPrefs.getInstance(this).setDuration(duration);
    }
}
