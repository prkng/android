package ng.prk.prkngandroid.util;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

import ng.prk.prkngandroid.Const;

public class AlarmManagerCompat {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setExact(AlarmManager am, int type, long triggerAtMillis, PendingIntent operation) {
        if (Const.SUPPORTS_KITKAT) {
            am.setExact(type, triggerAtMillis, operation);
        } else {
            am.set(type, triggerAtMillis, operation);
        }
    }
}
