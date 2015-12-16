package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_login);

        PrkngPrefs.getInstance(this).setAuthUser(null);

        setupListeners();
    }

    @Override
    protected boolean isLoginRequired() {
        return false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_facebook) {

        } else if (id == R.id.btn_google) {

        } else if (id == R.id.btn_email) {
            startActivityForResult(LoginEmailActivity.newIntent(this), Const.RequestCodes.AUTH_LOGIN_EMAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult @ " + resultCode);

        if ((requestCode == Const.RequestCodes.AUTH_LOGIN_EMAIL)
                && (resultCode == Activity.RESULT_OK)) {
            setResult(Activity.RESULT_OK);
            this.finish();
        }
    }

    public void setupListeners() {
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_email).setOnClickListener(this);
    }
}
