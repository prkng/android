package ng.prk.prkngandroid;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class PrkngApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        PrkngPrefs.setDefaults(this);
    }
}
