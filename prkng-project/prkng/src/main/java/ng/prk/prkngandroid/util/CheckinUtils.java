package ng.prk.prkngandroid.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Calendar;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.receiver.CheckinReminderReceiver;
import ng.prk.prkngandroid.service.CheckoutService;
import ng.prk.prkngandroid.ui.activity.MainActivity;

public class CheckinUtils {
    private final static String TAG = "NotifyUtils";

    public static void checkin(Context context, CheckinData checkin, String address, long remainingTime) {
        final boolean isWeekLong = CalendarUtils.isWeekLongDuration(remainingTime);
        final long endAt = System.currentTimeMillis() + remainingTime;
        if (isWeekLong) {
            PrkngPrefs.getInstance(context)
                    .setCheckin(checkin, address);
        } else {
            PrkngPrefs.getInstance(context)
                    .setCheckin(checkin, address, endAt);
            // Set alarm 30 min before expiry
            setAlarm(context,
                    endAt - Const.NotifationConfig.EXPIRY,
                    Const.NotificationTypes.EXPIRY);

            if (hasSmartReminder(endAt)) {
                // Set smart alarm the night before a morning checkout
                setAlarm(context,
                        getSmartReminderTime(endAt),
                        Const.NotificationTypes.SMART);
            }
        }

        final String endTime = isWeekLong ? null :
                CalendarUtils.getDurationFromMillis(context, remainingTime);
        notifyCheckinStart(context, checkin.getId(), endTime, checkin.getLatLng(), address);
    }

    public static void showExpiryReminder(Context context) {
        final CheckinData checkin = PrkngPrefs.getInstance(context).getCheckinData();
        if (checkin == null) {
            return;
        }

        notifyCheckinExpiry(context, checkin);
    }

    public static void showSmartReminder(Context context) {
        final CheckinData checkin = PrkngPrefs.getInstance(context).getCheckinData();
        if (checkin == null) {
            return;
        }

        notifyCheckinExpiry(context, checkin);
    }


    public static boolean checkout(Context context, long id) {
        try {
            final PrkngPrefs prefs = PrkngPrefs.getInstance(context);
            final String apiKey = prefs.getApiKey();

            prefs.setCheckout(id);
            removeNotifications(context);

            ApiClient.checkout(ApiClient.getServiceLog(), apiKey, id, null);

            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void notifyCheckinStart(Context context, long checkinId, String endTime, LatLng latLng, String address) {
        final Resources res = context.getResources();

        final String contentText = String.format(res.getString(R.string.notify_checkin_text), address);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        final PendingIntent clickIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        MainActivity.newIntent(context, latLng),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        final PendingIntent checkoutIntent =
                PendingIntent.getService(
                        context,
                        0,
                        CheckoutService.newIntent(context, checkinId),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.notify_color))
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setTicker(contentText)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_notify_checkin);
        if (endTime != null) {
            builder.setContentTitle("Jusqu’à " + endTime)
                    .setContentText(contentText);
        } else {
            builder.setContentTitle(contentText);
        }

        // Show the checkout button
        builder.addAction(R.drawable.ic_action_done,
                res.getString(R.string.btn_checkout),
                checkoutIntent);

        // Set the click action
        builder.setContentIntent(clickIntent);

        // Gets an instance of the NotificationManager service
        final NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        nm.notify(Const.RequestCodes.NOTIFY_CHECKIN, builder.build());
    }

    @Deprecated // TODO handle different types of notifs
    private static void notifyCheckinExpiry(Context context, CheckinData checkin) {
        final Resources res = context.getResources();

        final String contentTitle = res.getString(R.string.notify_checkout_text);
        final String contentText = String.format(res.getString(R.string.notify_checkin_text), checkin.getAddress());

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.notify_color))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setLocalOnly(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify_checkout)
                .setContentTitle(contentTitle)
                .setTicker(contentTitle)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        final Notification publicNotification = builder
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(contentText)
                .build();

        builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPublicVersion(publicNotification)
                .setContentText(contentText);

        // Gets an instance of the NotificationManager service
        final NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        nm.notify(Const.RequestCodes.NOTIFY_CHECKIN, builder.build());
    }

    private static void removeNotifications(Context context) {
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Const.RequestCodes.NOTIFY_CHECKIN);
    }

    private static void setAlarm(Context context, long endAt, int type) {
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                        Const.RequestCodes.CHECKIN_REMINDER,
                        CheckinReminderReceiver.newIntent(context, type),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        final AlarmManager am =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Const.NotificationTypes.SMART == type) {
            // The smart reminder doesn't need to be precise
            am.set(AlarmManager.RTC_WAKEUP,
                    endAt,
                    pendingIntent);
        } else {
            AlarmManagerCompat.setExact(am,
                    AlarmManager.RTC_WAKEUP,
                    endAt,
                    pendingIntent);
        }
    }

    public static boolean hasSmartReminder(long endAt) {
        final Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(endAt);

        if (calendarEnd.get(Calendar.HOUR_OF_DAY) < 12) {
            // Smart reminder only for intervals ending before Noon
            final Calendar calendarNow = Calendar.getInstance();

            if (calendarEnd.before(calendarNow)) {
                // Shouldn't happen
                return false;
            } else if (calendarNow.get(Calendar.DAY_OF_YEAR) != calendarEnd.get(Calendar.DAY_OF_YEAR)) {
                // Not on the same day
                if (calendarNow.get(Calendar.HOUR_OF_DAY) < 16) {
                    // Now is before 4pm
                    return true;
                }
                if (calendarEnd.getTimeInMillis() - calendarNow.getTimeInMillis() >=
                        DateUtils.DAY_IN_MILLIS) {
                    // More than 24 hours
                    return true;
                }
            }

        }

        return false;
    }

    private static long getSmartReminderTime(long time) {
        final Calendar calendar = Calendar.getInstance();
        // Set calendar to the previous day;
        calendar.setTimeInMillis(time - Const.NotifationConfig.SMART_DAY_OFFSET);

        // Set clock to 20:00
        calendar.set(Calendar.HOUR_OF_DAY, Const.NotifationConfig.SMART_HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, Const.NotifationConfig.SMART_MINUTE);

        // return new time
        return calendar.getTimeInMillis();
    }
}
