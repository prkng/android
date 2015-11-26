package ng.prk.prkngandroid.util;


import java.util.Calendar;

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
    public static final int WEEK_IN_DAYS = 7;
    public static final int FIRST_WEEK_IN_DAY = MONDAY;

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
     * @param offset 1 to 7. Should be equal to Today
     * @return
     */
    public static int getIsoDayOfWeekLooped(int dayOfWeek, int offset) {
        int day = dayOfWeek + offset - 1;
        if (day > 7) {
            day -= 7;
        }

        return day;
    }
}
