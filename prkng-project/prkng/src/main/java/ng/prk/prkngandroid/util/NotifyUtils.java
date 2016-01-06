package ng.prk.prkngandroid.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.MainActivity;

public class NotifyUtils {

    public static void setReminder(Context context, long time, String address) {
        final Resources res = context.getResources();

        Intent resultIntent = new Intent(context, MainActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        final String notifyText = String.format(res.getString(R.string.notify_checkin_text), address);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notify_prkng)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentTitle("Jusqu’à mardi 18h30")
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setContentText(notifyText)
                        .setContentIntent(resultPendingIntent);
        mBuilder.addAction(R.drawable.ic_action_done, res.getString(R.string.btn_checkout), null);


        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
