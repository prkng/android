package ng.prk.prkngandroid.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import ng.prk.prkngandroid.util.PrkngPrefs;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        // Launch Onboarding once only
        if (savedInstanceState == null
                && isLoginRequired() && !PrkngPrefs.getInstance(this).isLoggedIn()) {
            startActivityForResult(LoginActivity.newIntent(this), Const.RequestCodes.AUTH_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Const.RequestCodes.AUTH_LOGIN)
                && (resultCode != Activity.RESULT_OK)) {
            this.finish();
        }
    }

    /**
     * When opening an app via a deep link, the app should provide users with a
     * First Click Free experience. App deep links should take users directly
     * to the content without any prompts, interstitial pages, splash screens,
     * login screens, etc., interrupting the content from being shown.
     *
     * @param intent
     * @return
     */
    protected boolean isFirstClickFreeIntent(Intent intent) {
        Log.v(TAG, "isFirstClickFreeIntent "
                + String.format("intent = %s", intent.getAction()));

        return ((intent != null) &&
                Intent.ACTION_VIEW.equals(intent.getAction()));
    }

    protected boolean isLoginRequired() {
        return true;
    }
}
