package ng.prk.prkngandroid.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.PrkngApp;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.AboutActivity;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import ng.prk.prkngandroid.ui.activity.SettingsActivity;
import ng.prk.prkngandroid.ui.dialog.DurationDialog;
import ng.prk.prkngandroid.util.PrkngPrefs;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private boolean hasLoggedOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch Onboarding once only
        if (savedInstanceState == null
                && isLoginRequired() && !PrkngPrefs.getInstance(this).isLoggedIn()) {
            startActivityForResult(LoginActivity.newIntent(this), Const.RequestCodes.AUTH_LOGIN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listener != null) {
            /**
             * Unregister the prefsChangeListener, and finish() if user logged-out
             * while in Settings
             */
            PrkngPrefs.getInstance(this).unregisterPrefsChangeListener(listener);
            listener = null;

            if (hasLoggedOut) {
                finish();
            }
        }
    }

//    @Override
//    public void setTitle(CharSequence title) {
//        super.setTitle(title);
//        TypefaceHelper.setTitle(this, (Toolbar) findViewById(R.id.toolbar), title);
//    }
//
//    @Override
//    public void setTitle(int titleId) {
//        super.setTitle(titleId);
//        TypefaceHelper.setTitle(this, toolbar, titleId);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id  == R.id.action_settings) {

            /**
             * Register a prefsChangeListener to check if user logged-out while in Settings
             */
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (Const.PrefsNames.AUTH_API_KEY.equals(key)) {
                        final String apiKey = sharedPreferences.getString(key, null);
                        hasLoggedOut = (apiKey == null || apiKey.isEmpty());
                    }
                }
            };
            PrkngPrefs.getInstance(this).registerPrefsChangeListener(listener);

            startActivity(SettingsActivity.newIntent(this));

            return true;
        } else if (id == R.id.action_about) {
            startActivity(AboutActivity.newIntent(this));

            return true;
        } else if (id == R.id.action_timer) {
            showDurationDialog();
            return true;
        } else if (id == R.id.action_user_activity) {
            startActivity(CheckinActivity.newIntent(this));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isLoginRequired() && (requestCode == Const.RequestCodes.AUTH_LOGIN)
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

    protected void showDurationDialog() {
        final DurationDialog dialog = DurationDialog.newInstance(
                PrkngApp.getInstance(this).getMapDurationFilter()
        );

        dialog.show(getSupportFragmentManager(), Const.FragmentTags.DIALOG_DURATIONS);
    }
}
