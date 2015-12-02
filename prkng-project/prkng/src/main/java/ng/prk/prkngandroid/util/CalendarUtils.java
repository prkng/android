package ng.prk.prkngandroid.util;


import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class CalendarUtils {

    /**
     * ISO calendar days, since Android's Calendar.MONDAY=2
     */
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int SUNDAY = 7;

    public static final int HOUR_IN_MINUTES = 60;
    public static final int DAY_IN_MINUTES = 60 * 24;
    public static final int WEEK_IN_DAYS = 7;
    public static final int FIRST_WEEK_IN_DAY = 1; // Replacing by zero could break loops

    public static final String TIMEZONE_UTC = "UTC";
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Get ISO day of week
     *
     * @return 1 to 7 (Monday to Sunday)
     */
    public static int getIsoDayOfWeek() {
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        return (day == Calendar.SUNDAY) ? 7 : (day - 1);
    }

    /**
     * Get ISO day of looped week.
     *
     * @param dayOfWeek
     * @param offset    1 to 7. Should be equal to Today
     * @return
     */
    public static int getIsoDayOfWeekLooped(int dayOfWeek, int offset) {
        int day = dayOfWeek + offset - 1;
        if (day > 7) {
            day -= 7;
        }

        return day;
    }

    /**
     * Get the Name of the dayOfWeek
     *
     * @param res
     * @param dayOfWeek 1-7
     * @return Monday-Sunday
     */
    public static String getDayOfWeekName(Resources res, int dayOfWeek) {
        final String[] week = res.getStringArray(R.array.days_of_week);

        return week[dayOfWeek - 1];
    }

    // TODO use locale to handle AM/PM and 12/24
//    public static String getTimeFromMinutesOfDay(Resources res, int minuteOfDay) {
//        if (minuteOfDay == Const.UNKNOWN_VALUE) {
//            return null;
//        }
//
//        final int hours = (int) Math.floor(minuteOfDay / 60);
//        final int minutes = minuteOfDay % 60;
//
//        return String.format(
//                res.getString(R.string.hour_minutes),
//                hours,
//                minutes);
//    }

    public static String getTimeFromMillis(Context context, long millis) {
        if ((int) millis == Const.UNKNOWN_VALUE) {
            return null;
        }

        return DateFormat
                .getTimeFormat(context)
                .format(new Date(millis - getTimezoneOffsetMillis()));
    }


    public static String getDurationFromMillis(Context context, long millis) {
        if ((int) millis == Const.UNKNOWN_VALUE) {
            return null;
        }

        return String.format("%.2f", (float) millis / DateUtils.HOUR_IN_MILLIS);
    }

    public static String getIsoTimestamp() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO_8601, Locale.getDefault());

        return sdf.format(new Date());
    }

    public static long getTimezoneOffsetMillis() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        return mTimeZone.getRawOffset();
    }
}
