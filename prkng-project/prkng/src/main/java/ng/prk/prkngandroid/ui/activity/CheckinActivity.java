package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class CheckinActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CheckinActivity";

    private long checkinId;
    private CheckBox vSmartReminder;

    public static Intent newIntent(Context context) {
        return new Intent(context, CheckinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkin);

        vSmartReminder = (CheckBox) findViewById(R.id.smart_reminder);

        final PrkngPrefs prefs = PrkngPrefs.getInstance(this);
        final CheckinData checkin = prefs.getCheckinData();
        final boolean hasSmartReminder = prefs.hasSmartReminder();

        fillCheckinData(checkin, hasSmartReminder);

        findViewById(R.id.fab).setOnClickListener(this);
        vSmartReminder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.fab) {
            CheckinHelper.checkout(this, checkinId);
            finish();
        } else if (id == R.id.smart_reminder) {
            PrkngPrefs.getInstance(this)
                    .setSmartReminder(((CheckBox) v).isChecked());
        }
    }

    private void fillCheckinData(CheckinData checkin, boolean hasSmartReminder) {
        checkinId = checkin.getId();

        ((TextView) findViewById(R.id.address)).setText(checkin.getAddress());
        final TextView vRemainingTime = (TextView) findViewById(R.id.remaining_time);

        final long checkoutAt = checkin.getCheckoutAt();
        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(checkoutAt)) {
            findViewById(R.id.expiry).setVisibility(View.GONE);
            vRemainingTime.setText(R.string.allowed_all_week);
        } else {
            final long remaining = checkoutAt - System.currentTimeMillis();
            vRemainingTime.setText(CalendarUtils.getTimeFromMillis(this, remaining));
        }

        if (!hasSmartReminder && !CheckinHelper.hasSmartReminder(checkoutAt)) {
            vSmartReminder.setVisibility(View.GONE);
        } else {
            vSmartReminder.setVisibility(View.VISIBLE);
            vSmartReminder.setChecked(hasSmartReminder);
        }
    }
}
