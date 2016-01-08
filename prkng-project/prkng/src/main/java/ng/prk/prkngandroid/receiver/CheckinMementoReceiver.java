package ng.prk.prkngandroid.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.util.NotifyUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class CheckinMementoReceiver extends WakefulBroadcastReceiver implements
        Const.NotificationTypes {
    private final static String TAG = "MementoReceiver ";

    public static Intent newIntent(Context context, int type) {
        final Intent intent = new Intent(context, CheckinMementoReceiver.class);

        switch (type) {
            case EXPIRY:
                intent.setAction(Const.IntentActions.NOTIFY_EXPIRY);
                break;
            case SMART_REMINDER:
                intent.setAction(Const.IntentActions.NOTIFY_SMART_REMINDER);
                break;
        }

        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        final CheckinData checkin = PrkngPrefs.getInstance(context).getCheckinData();
        if (checkin != null) {
            if (Const.IntentActions.NOTIFY_EXPIRY.equals(action)) {
                NotifyUtils.notifyCheckinExpiry(context, checkin);
            } else if (Const.IntentActions.NOTIFY_SMART_REMINDER.equals(action)) {
                NotifyUtils.notifyCheckinSmartExpiry(context, checkin);
            }
        }
    }
}
