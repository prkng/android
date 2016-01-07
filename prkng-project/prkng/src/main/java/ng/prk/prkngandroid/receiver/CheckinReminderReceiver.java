package ng.prk.prkngandroid.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ng.prk.prkngandroid.util.CheckinUtils;

public class CheckinReminderReceiver extends WakefulBroadcastReceiver {
    private final static String TAG = "CheckinReceiver";

    public static Intent newIntent(Context context) {
        return new Intent(context, CheckinReminderReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        CheckinUtils.startCheckout(context);
    }

    public CheckinReminderReceiver() {
        super();
        Log.v(TAG, "CheckinReminderReceiver");
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        Log.v(TAG, "peekService");

        return super.peekService(myContext, service);
    }
}
