package ng.prk.prkngandroid.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.service.CheckoutService;
import ng.prk.prkngandroid.ui.activity.MainActivity;

public class NotifyUtils implements Const.NotificationTypes {

    public static void removeNotifications(Context context) {
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Const.RequestCodes.NOTIFY_CHECKIN);
    }


    public static void notifyCheckinStart(Context context, CheckinData checkin) {
        showCheckinNotification(context, checkin, START);
    }


    public static void notifyCheckinExpiry(Context context, CheckinData checkin) {
        showCheckinNotification(context, checkin, EXPIRY);
    }

    public static void notifyCheckinSmartExpiry(Context context, CheckinData checkin) {
        showCheckinNotification(context, checkin, SMART_REMINDER);
    }

    private static void showCheckinNotification(Context context, CheckinData checkin, int type) {
        if (!PrkngPrefs.getInstance(context).hasNotifications()) {
            return;
        }

        if (checkin == null) {
            return;
        }

        final NotificationCompat.Builder builder = getNotificationBuilder(context, checkin, type);

        // Set the click action
        builder.setContentIntent(getClickIntent(context, checkin.getLatLng()));

        // Show the checkout button
        if (hasCheckoutButton(type)) {
            builder.addAction(getButtonAction(context, checkin.getId()));
        }


        // Gets an instance of the NotificationManager service
        final NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        nm.notify(Const.RequestCodes.NOTIFY_CHECKIN, builder.build());
    }

    private static PendingIntent getClickIntent(Context context, LatLng latLng) {
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        return PendingIntent.getActivity(
                context,
                0,
                MainActivity.newIntent(context, latLng),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static NotificationCompat.Action getButtonAction(Context context, long checkinId) {
        final PendingIntent checkoutIntent = PendingIntent.getService(
                context,
                0,
                CheckoutService.newIntent(context, checkinId),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Action(R.drawable.ic_action_done,
                context.getResources().getString(R.string.btn_checkout),
                checkoutIntent);
    }

    private static boolean hasCheckoutButton(int type) {
        return type != SMART_REMINDER;
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context, CheckinData checkin, int type) {
        final Resources res = context.getResources();

        String contentTitle = null;
        String contentText = String.format(res.getString(R.string.notify_checkin_text), checkin.getAddress());

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        switch (type) {
            case START: {
                // Week-long checkins don't have a subtitle,
                // and don't have any smart_reminder or expiry notifications
                final boolean isWeekLong = CalendarUtils.isWeekLongDuration(checkin.getDuration());
                contentTitle = isWeekLong ? contentText :
                        CalendarUtils.getDurationFromMillis(context, checkin.getDuration());

                builder.setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
//                        .setDefaults()
                        .setSmallIcon(R.drawable.ic_notify_checkin)
                        .setLocalOnly(true);
                if (isWeekLong) {
                    contentText = null;
                }
                break;
            }
            case SMART_REMINDER:
                contentTitle = res.getString(R.string.notify_expiry_smart_reminder_text);
                builder.setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_notify_checkin)
                        .setLocalOnly(false);
                break;
            case EXPIRY:
                contentTitle = res.getString(R.string.notify_expiry_reminder_text);
                builder.setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_notify_checkout)
                        .setLocalOnly(false);
                break;
        }

        builder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.notify_color))
                .setContentTitle(contentTitle)
                .setTicker(contentTitle);

        if (type == EXPIRY || type == SMART_REMINDER) {
            final Notification publicNotification = builder
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            // Set public version, and "restore" the visibility
            builder.setPublicVersion(publicNotification)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        }

        // ContentText is added after building the publicVersion
        builder.setContentText(contentText);

        return builder;
    }
}
