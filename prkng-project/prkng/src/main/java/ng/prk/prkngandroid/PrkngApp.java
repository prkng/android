package ng.prk.prkngandroid;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class PrkngApp extends Application {
    private static final String TAG = "PrkngApp";
    private float mapDurationFilter;
    private Tracker mTracker;


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

        if (BuildConfig.USE_CRASHLYTICS) {
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

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getAnalyticsTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            analytics.enableAutoActivityReports(this);
            mTracker = analytics.newTracker("UA-63856349-2");
            mTracker.enableAutoActivityTracking(true);
            mTracker.setAppId("prkngandroid");
        }
        return mTracker;
    }
}
