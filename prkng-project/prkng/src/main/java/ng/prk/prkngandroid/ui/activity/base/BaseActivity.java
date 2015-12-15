package ng.prk.prkngandroid.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.activity.LoginEmailActivity;
import ng.prk.prkngandroid.ui.activity.OnboardingActivity;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
Log.v(TAG, "onCreate");

        // Launch Onboarding once only
        if (savedInstanceState == null && !isFirstClickFreeIntent(getIntent())) {
            final PrkngPrefs prkngPrefs = PrkngPrefs.getInstance(this);
            if (prkngPrefs.isOnboarding()) {
                startActivityForResult(OnboardingActivity.newIntent(this, true), Const.RequestCodes.ONBOARDING);
            } else if (isLoginRequired() && !prkngPrefs.isLoggedIn()) {
                startActivityForResult(LoginEmailActivity.newIntent(this), Const.RequestCodes.AUTH_LOGIN);
            }
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
