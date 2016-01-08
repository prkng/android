package ng.prk.prkngandroid.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.util.CheckinHelper;

public class CheckoutService extends IntentService {
    private final static String TAG = "CheckoutService";

    private Handler mHandler;

    public static Intent newIntent(Context context, long id) {
        final Intent intent = new Intent(context, CheckoutService.class);

        final Bundle bundle = new Bundle();
        bundle.putLong(Const.BundleKeys.CHECKIN_ID, id);
        intent.putExtras(bundle);

        return intent;
    }

    public CheckoutService() {
        super(TAG);
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final boolean res = CheckinHelper.checkout(CheckoutService.this,
                intent.getLongExtra(Const.BundleKeys.CHECKIN_ID, Const.UNKNOWN_VALUE));

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CheckoutService.this,
                        res ? R.string.toast_checkout_ok : R.string.toast_checkout_error,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
