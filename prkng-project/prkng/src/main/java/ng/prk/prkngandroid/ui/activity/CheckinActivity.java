package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckinActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "CheckinActivity";

    private long checkinId;
    private Button vSmartReminderBtn;
    private Button vCheckoutBtn;
    private long mRemainingTime;
    private String mTitle;


    private final Callback checkinCallback = new Callback<CheckinData>() {
        @Override
        public void onResponse(Call<CheckinData> call, Response<CheckinData> response) {
            Log.v(TAG, "onResponse");

            try {
                final CheckinData checkin = response.body();
                if (checkin != null) {
                    checkin.fixTimezones();
                }

                final Context context = CheckinActivity.this;
                CheckinHelper.checkin(context,
                        checkin,
                        mTitle,
                        mRemainingTime);
                getIntent().putExtras(new Bundle());
                final boolean hasSmartReminder = PrkngPrefs.getInstance(CheckinActivity.this).hasSmartReminder();

                fillCheckinData(checkin, hasSmartReminder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<CheckinData> call, Throwable t) {
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, CheckinActivity.class);
    }

    public static Intent newIntent(Context context, String markerId, String title, long duration) {
        final Intent intent = new Intent(context, CheckinActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_ID, markerId);
        bundle.putString(Const.BundleKeys.MARKER_TITLE, title);
        bundle.putLong(Const.BundleKeys.DURATION, duration);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String markerId = getIntent().getStringExtra(Const.BundleKeys.MARKER_ID);
        mTitle = getIntent().getStringExtra(Const.BundleKeys.MARKER_TITLE);
        mRemainingTime = getIntent().getLongExtra(Const.BundleKeys.DURATION, Const.UNKNOWN_VALUE);

        final PrkngPrefs prefs = PrkngPrefs.getInstance(this);
        final CheckinData checkin = prefs.getCheckinData();
        final boolean hasSmartReminder = prefs.hasSmartReminder();

        if (checkin != null || markerId != null) {
            setContentView(R.layout.activity_checkin);
            vSmartReminderBtn = (Button) findViewById(R.id.smart_reminder);
            vCheckoutBtn = (Button) findViewById(R.id.btn_checkout);

            if (markerId != null) {
                doCheckin(markerId);
            } else {
                fillCheckinData(checkin, hasSmartReminder);
            }

            vCheckoutBtn.setOnClickListener(this);
            vSmartReminderBtn.setOnClickListener(this);
        } else {
            setContentView(R.layout.activity_checkin_none);
        }

        findViewById(R.id.btn_nav_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_checkout) {
            CheckinHelper.checkout(this, checkinId);
            finish();
        } else if (id == R.id.smart_reminder) {
            final boolean isActivated = !vSmartReminderBtn.isActivated();

            PrkngPrefs.getInstance(this).setSmartReminder(isActivated);
            toggleSmartReminder(isActivated);
        } else if (id == R.id.btn_nav_back) {
            finish();
        }
    }

    private void fillCheckinData(CheckinData checkin, boolean hasSmartReminder) {
        findViewById(R.id.progress).setVisibility(View.GONE);
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        vCheckoutBtn.setVisibility(View.VISIBLE);

        checkinId = checkin.getId();

        ((TextView) findViewById(R.id.address)).setText(checkin.getAddress());
        final TextView vRemainingTime = (TextView) findViewById(R.id.remaining_time);

        final long checkoutAt = checkin.getCheckoutAt();
        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(checkoutAt)) {
            findViewById(R.id.expiry).setVisibility(View.GONE);
            vRemainingTime.setText(R.string.allowed_all_week);
        } else {
            final long remaining = checkoutAt - System.currentTimeMillis();
            vRemainingTime.setText(CalendarUtils.getDurationFromMillis(this, remaining));
        }

        if (!CheckinHelper.hasSmartReminder(checkoutAt)) {
            vSmartReminderBtn.setVisibility(View.GONE);
        } else {
            vSmartReminderBtn.setVisibility(View.VISIBLE);
            toggleSmartReminder(hasSmartReminder);
        }
    }

    private void toggleSmartReminder(boolean activated) {
        final int icon = activated ?
                R.drawable.ic_smart_reminder : R.drawable.ic_smart_reminder_off;
        vSmartReminderBtn.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        vSmartReminderBtn.setActivated(activated);
        vSmartReminderBtn.setAlpha(activated ? 1f : 0.5f);
    }

    private void doCheckin(String slotId) {
        final String apiKey = PrkngPrefs.getInstance(this).getApiKey();

        ApiClient.checkin(
                ApiClient.getServiceLog(),
                apiKey,
                slotId,
                checkinCallback);
    }
}
