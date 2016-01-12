package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class CheckinActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CheckinActivity";

    private long checkinId;

    public static Intent newIntent(Context context) {
        return new Intent(context, CheckinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkin);

        findViewById(R.id.fab).setOnClickListener(this);

        final CheckinData checkin = PrkngPrefs.getInstance(this).getCheckinData();
        fillCheckinData(checkin);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            CheckinHelper.checkout(this, checkinId);
            finish();
        }
    }

    private void fillCheckinData(CheckinData checkin) {
        Log.v(TAG, "fillCheckinData "
                + String.format("checkin = %s", checkin));
        checkinId = checkin.getId();

        ((TextView) findViewById(R.id.address)).setText(checkin.getAddress());
        final TextView vRemainingTime = (TextView) findViewById(R.id.remaining_time);

        final Long checkoutAt = checkin.getCheckoutAt();
        if (checkoutAt == null) {
            findViewById(R.id.expiry).setVisibility(View.GONE);
            vRemainingTime.setText(R.string.allowed_all_week);
        } else {
            final long remaining = checkoutAt - System.currentTimeMillis();
            vRemainingTime.setText(CalendarUtils.getTimeFromMillis(this, remaining));
        }

    }
}
