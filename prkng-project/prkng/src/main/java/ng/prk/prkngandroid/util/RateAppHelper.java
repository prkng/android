package ng.prk.prkngandroid.util;

import android.app.Activity;
import android.util.Log;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.MainActivity;

public class RateAppHelper {

    public static boolean showRateDialog(Activity activity) {
        AppRate.with(activity)
                .setDialogStyle(R.style.PrkngDialogStyle)
                .setStoreType(AppRate.StoreType.GOOGLEPLAY)
                .setInstallDays(3) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10 times.
                .setRemindInterval(2) // default 1 day.
                .setShowLaterButton(true) // default true.
                .setDebug(true) // default false.
                .setCancelable(true) // default false.
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .setTitle(R.string.rate_dialog_title)
                .setMessage(R.string.rate_dialog_message)
                .setTextLater(R.string.rate_dialog_later_btn)
                .setTextNever(R.string.rate_dialog_never_btn)
                .setTextRateNow(R.string.rate_dialog_rate_now_btn)
                .monitor();

        return AppRate.showRateDialogIfMeetsConditions(activity);
    }
}
