package ng.prk.prkngandroid.util;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.receiver.CheckinReminderReceiver;
import ng.prk.prkngandroid.ui.activity.MainActivity;

public class CheckinUtils {
    private final static String TAG = "NotifyUtils";

    public static void startCheckin(Context context, CheckinData checkin, String address, long time) {
        if (CalendarUtils.isWeekLongDuration(time)) {
            PrkngPrefs.getInstance(context)
                    .setCheckin(checkin, address);
        } else {
            PrkngPrefs.getInstance(context)
                    .setCheckin(checkin, address, time);
        }

        String endTime = CalendarUtils.getDurationFromMillis(context, time);
        notifyCheckinStart(context, endTime, checkin.getLatLng(), address);
        setAlarm(context, time);
    }

    public static void notifyCheckinStart(Context context, String endTime, LatLng latLng, String address) {
        Log.v(TAG, "notifyCheckinStart");
        final Resources res = context.getResources();

        final String contentText = String.format(res.getString(R.string.notify_checkin_text), address);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        MainActivity.newIntent(context, latLng),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_notify_checkin);
        if (endTime != null) {
            builder.setContentTitle("Jusqu’à " + endTime)
                    .setContentText(contentText);
        } else {
            builder.setContentTitle(contentText);
        }

        // Show the checkout button
        builder.addAction(R.drawable.ic_action_done, res.getString(R.string.btn_checkout), null);

        // Set the click action
        builder.setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(Const.RequestCodes.NOTIFY_CHECKIN, builder.build());
    }

    public static void startCheckout(Context context) {
        final CheckinData checkin = PrkngPrefs.getInstance(context).getCheckinData();
        if (checkin == null) {
            return;
        }

        notifyCheckinEnd(context, checkin);
    }

    public static void notifyCheckinEnd(Context context, CheckinData checkin) {
        final Resources res = context.getResources();

        final String contentTitle = res.getString(R.string.notify_checkout_text);
        final String contentText = String.format(res.getString(R.string.notify_checkin_text), checkin.getAddress());

        NotificationCompat.Builder builder = getNotificationBuilder(context);
        builder.setSmallIcon(R.drawable.ic_notify_checkout)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(Const.RequestCodes.NOTIFY_CHECKIN, builder.build());
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context) {
        return new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.notify_color))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setAlarm(Context context, long time) {
        Log.v(TAG, "setAlarm");

        long end = System.currentTimeMillis() + (DateUtils.MINUTE_IN_MILLIS);

        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                        Const.RequestCodes.NOTIFY_CHECKIN,
                        CheckinReminderReceiver.newIntent(context),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        final AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, end, pendingIntent);
    }
}
