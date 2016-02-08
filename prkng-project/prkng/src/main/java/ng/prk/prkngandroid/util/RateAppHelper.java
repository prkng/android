package ng.prk.prkngandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import ng.prk.prkngandroid.R;

public class RateAppHelper {

    public static boolean showRateDialog(final Activity activity) {
        AppRate.with(activity)
                .setDialogStyle(R.style.PrkngDialogStyle)
                .setStoreType(AppRate.StoreType.GOOGLEPLAY)
                .setInstallDays(7) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10 times.
                .setRemindInterval(5) // default 1 day.
                .setShowLaterButton(true) // default true.
                .setDebug(false) // default false.
                .setCancelable(true) // default false.
                .setTitle(R.string.rate_dialog_title)
                .setMessage(R.string.rate_dialog_message)
                .setTextLater(R.string.rate_dialog_later_btn)
                .setTextNever(R.string.rate_dialog_never_btn)
                .setTextRateNow(R.string.rate_dialog_rate_now_btn)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            try {
                                activity.startActivity(getFeedbackIntent(activity));
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        }
                    }
                })

                .monitor();

        return AppRate.showRateDialogIfMeetsConditions(activity);
    }

    private static Intent getFeedbackIntent(Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(context.getString(R.string.mail_to_contact)));

        return intent;
    }
}
