package ng.prk.prkngandroid.model.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.util.CalendarUtils;

public class HumanDuration implements
        Const.MapSections {
    private final static String TAG = "HumanDuration";

    private Context context;
    private long millis;
    private String prefix;
    private String expiry;

    public HumanDuration(Context context, long millis, int type) {
        this.context = context;
        this.millis = millis;

        if (type == OFF_STREET) {
            initializeLotDuration();
        } else {
            initializeSpotDuration();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getExpiry() {
        return expiry;
    }

    private void initializeDuration() {
        final Resources res = context.getResources();

        final Calendar now = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        end.setTimeInMillis(end.getTimeInMillis() + millis);

        final int dayDiff = end.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
        final int minuteDiff = (int) Math.ceil((end.getTimeInMillis() - now.getTimeInMillis()) / DateUtils.MINUTE_IN_MILLIS);

        String day;
        String timeOfDay;

        switch (dayDiff) {
            case 0:
                day = null;
                break;
            case 1:
                day = res.getString(R.string.expiry_day_tomorrow);
                break;
            default:
                SimpleDateFormat dayFormat = new SimpleDateFormat(CalendarUtils.DATE_FORMAT_DAY_OF_WEEK, Locale.getDefault());
                day = dayFormat.format(end.getTime());
                break;
        }


        if (dayDiff == 0 && minuteDiff <= 2 * CalendarUtils.HOUR_IN_MINUTES) {
            // Ends today in 2hrs or less
            final int hours = minuteDiff / CalendarUtils.HOUR_IN_MINUTES;
            final int minutes = minuteDiff % CalendarUtils.HOUR_IN_MINUTES;
            if (hours == 0) {
                timeOfDay = String.format(
                        res.getQuantityString(R.plurals.expiry_minutes, minutes),
                        minutes);
            } else if (minutes == 0) {
                timeOfDay = String.format(
                        res.getQuantityString(R.plurals.expiry_hours, minutes),
                        hours);
            } else {
                timeOfDay = String.format(
                        res.getString(R.string.expiry_hours_minutes),
                        hours,
                        minutes);
            }
        } else {
            timeOfDay = DateFormat
                    .getTimeFormat(context)
                    .format(end.getTime());
        }

        if (day == null) {
            this.expiry = timeOfDay;
        } else {
            this.expiry = String.format(
                    res.getString(R.string.expiry_day_time_of_day),
                    day,
                    timeOfDay);
        }
    }

    private void initializeLotDuration() {
        initializeDuration();
    }

    private void initializeSpotDuration() {
        initializeDuration();

    }

    @Override
    public String toString() {
        return "HumanDuration{" +
                "prefix='" + prefix + '\'' +
                ", expiry='" + expiry + '\'' +
                '}';
    }
}
