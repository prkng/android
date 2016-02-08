package ng.prk.prkngandroid.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ng.prk.prkngandroid.PrkngApp;

public class AnalyticsUtils {
    private static final String TAG = "AnalyticsUtils";

    public static void sendScreenView(Context context, String screenName) {
        Log.v(TAG, "sendScreenView, " + screenName);
        try {
            final Tracker tracker = PrkngApp.getInstance(context).getAnalyticsTracker();
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
