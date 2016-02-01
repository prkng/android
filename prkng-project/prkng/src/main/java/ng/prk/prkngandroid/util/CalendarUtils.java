package ng.prk.prkngandroid.util;


import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class CalendarUtils {
    private final static String TAG = "CalendarUtils";

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
    public final static String DATE_FORMAT_DAY_OF_WEEK = "EEEE";

//    public static final String DATE_FORMAT_ISO_8601_MICROS = DATE_FORMAT_ISO_8601 + ".SSSSSS";

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

    public static boolean areConsecutiveDaysOfWeekLooped(int d1, int d2) {
        final int diff = d2 - d1;

        return (diff == 1) || (diff == (MONDAY - SUNDAY));
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

    public static String getTimeFromMillis(Context context, long millis) {
        if ((int) millis == Const.UNKNOWN_VALUE) {
            return null;
        }

        return DateFormat
                .getTimeFormat(context)
                .format(new Date(millis - getTimezoneOffsetMillis()));
    }

    public static boolean isWeekLongDuration(long millis) {
        return Long.valueOf(millis).compareTo(DateUtils.WEEK_IN_MILLIS) >= 0;
    }

    /**
     * Get the absolute number of days between two DaysOfWeek
     *
     * @param day1 the day to subtract from
     * @param day2 the the day subtracted
     * @return absolute number of days
     */
    public static int subtractDaysOfWeekLooped(int day1, int day2) {
        if (day1 < day2) {
            return subtractDaysOfWeekLooped(day1 + WEEK_IN_DAYS, day2);
        }

        return day1 - day2;
    }

    public static String getIsoTimestamp() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO_8601, Locale.getDefault());

        return sdf.format(new Date());
    }

    public static long parseIsoTimestamp(String timestamp) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO_8601, Locale.getDefault());
        return sdf.parse(timestamp).getTime();
    }

    /**
     * Get the current daytime in MilliSeconds, compatible with Intervals
     *
     * @return Today's milliseconds (since midnight)
     */
    public static long todayMillis() {
        final Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());

        return cal.get(Calendar.HOUR_OF_DAY) * DateUtils.HOUR_IN_MILLIS
                + cal.get(Calendar.MINUTE) * DateUtils.MINUTE_IN_MILLIS;
    }

    public static long getTimezoneOffsetMillis() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        return mTimeZone.getRawOffset();
    }
}
