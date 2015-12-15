package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class OnboardingActivity extends AppCompatActivity {

    public static Intent newIntent(Context context, boolean isInitial) {
        final Intent intent = new Intent(context, OnboardingActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putBoolean(Const.BundleKeys.IS_INITIAL_ONBOARDING, isInitial);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
    }
}
