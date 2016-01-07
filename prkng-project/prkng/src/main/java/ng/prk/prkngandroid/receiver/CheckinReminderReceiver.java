package ng.prk.prkngandroid.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CheckinUtils;

public class CheckinReminderReceiver extends WakefulBroadcastReceiver implements
        Const.NotificationTypes {
    private final static String TAG = "CheckinReceiver";

    public static Intent newIntent(Context context, int type) {
        final Intent intent = new Intent(context, CheckinReminderReceiver.class);

        switch (type) {
            case EXPIRY:
                intent.setAction(Const.IntentActions.NOTIFY_EXPIRY);
                break;
            case SMART:
                intent.setAction(Const.IntentActions.NOTIFY_SMART);
                break;
        }

        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (Const.IntentActions.NOTIFY_EXPIRY.equals(action)) {
            CheckinUtils.showExpiryReminder(context);
        } else if (Const.IntentActions.NOTIFY_SMART.equals(action)) {
            CheckinUtils.showSmartReminder(context);
        }
    }
}
