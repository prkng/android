package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.concurrent.TimeUnit;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.Interval;

/**
 * Restriction Interval
 */
public class RestrInterval extends Interval implements
        Const.ParkingRestrType {
    private final static String TAG = "RestrInterval";

    private int dayOfWeek;
    private int type;
    private int timeMax;

    /**
     * Private constructor to be used with Builder
     *
     * @param builder the RestrInterval.Builder
     */
    private RestrInterval(Builder builder) {
        super(builder.startMillis, builder.endMillis);

        this.dayOfWeek = builder.dayOfWeek;
        this.type = builder.type;
        this.timeMax = builder.timeMax;
    }

    /**
     * Check if the first interval ends at the same midnight where the second interval starts.
     * This is regardless of type
     *
     * @param firstInterval  The interval to examine
     * @param secondInterval The interval to examine
     * @return true if the both intervals abut at midnight, following specific order
     */
    private static boolean abutsOvernight(RestrInterval firstInterval, RestrInterval secondInterval) {
        return (Float.compare(firstInterval.getEndMillis(), DateUtils.DAY_IN_MILLIS) == 0)
                && (Float.compare(secondInterval.getStartMillis(), 0) == 0);
    }

    /**
     * Subtract an interval from the current.
     * When the other interval contains the current, result is empty.
     * When the current interval contains the other, result is 2 intervals with a gap.
     * When the current interval starts before, result is the first part (leading)
     * When the current interval starts after, result is the second (trailing)
     *
     * @param current the interval to subtract from
     * @param another the Interval to subtract
     * @return List of intervals, can have a gap
     */
    private static RestrIntervalsList subtract(RestrInterval current, RestrInterval another) {
        final RestrIntervalsList intervalsList = new RestrIntervalsList();

        if (another.contains(current)) {
            // The subtracted interval is bigger than current, return empty result.
            return intervalsList;
        }

        if (current.contains(another) || current.startsBefore(another)) {
            // The first (leading) part, if its remaining duration > 0
            if (Float.compare(current.getStartMillis(), another.getStartMillis()) < 0) {
                intervalsList.add(new Builder(current.getDayOfWeek())
                                .startMillis(current.getStartMillis())
                                .endMillis(another.getStartMillis())
                                .type(current.getType())
                                .timeMax(current.getTimeMaxMinutes())
                                .build()
                );
            }
        }

        if (current.contains(another) || current.startsAfter(another)) {
            if (Float.compare(another.getEndMillis(), current.getEndMillis()) < 0) {
                // The last (trailing) part, if its remaining duration > 0
                intervalsList.add(new Builder(current.getDayOfWeek())
                                .startMillis(another.getEndMillis())
                                .endMillis(current.getEndMillis())
                                .type(current.getType())
                                .timeMax(current.getTimeMaxMinutes())
                                .build()
                );
            }
        }

        return intervalsList;
    }

    /**
     * Get the shortest TimeMax, ignoring empty values (without time restriction)
     *
     * @param t1 TimeMax minutes
     * @param t2 TimeMax minutes
     * @return int shortest TimeMaxMinutes period
     */
    public static int getMinTimemax(int t1, int t2) {
        if (t1 == Const.UNKNOWN_VALUE) {
            return t2;
        } else if (t2 == Const.UNKNOWN_VALUE) {
            return t1;
        } else {
            return Math.min(t1, t2);
        }
    }

    /**
     * Get ISO day of week
     *
     * @return int day of week
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @return Restriction type
     * @see ng.prk.prkngandroid.Const.ParkingRestrType
     */
    public int getType() {
        return type;
    }

    /**
     * Get TimeMax value, in minutes
     *
     * @return int TimeMax minutes
     */
    public int getTimeMaxMinutes() {
        return timeMax;
    }

    /**
     * Get TimeMax value, in milliseconds
     *
     * @return long TimeMax millis
     */
    public long getTimeMaxMillis() {
        return timeMax * DateUtils.MINUTE_IN_MILLIS;
    }

    /**
     * Compares type and timeMax
     *
     * @param another The interval to examine
     * @return true if has same type and timeMax value
     */
    public boolean isSameType(RestrInterval another) {
        return (this.type == another.getType()) && (this.timeMax == another.getTimeMaxMinutes());
    }

    /**
     * @param another The interval to examine
     * @return true if intervals are on the same day
     */
    public boolean isSameDay(RestrInterval another) {
        return this.dayOfWeek == another.getDayOfWeek();
    }

    /**
     * Check if restriction applies all day (24 hours)
     *
     * @return true for all-day restriction
     */
    public boolean isAllDay() {
        return endMillis - startMillis >= DateUtils.DAY_IN_MILLIS;
    }

    /**
     * Check if the interval abuts the current at midnight. Also handles week-loop.
     * This is regardless of type or order.
     *
     * @param another The interval to examine
     * @return true if the other interval abuts at previous/following midnight
     */
    public boolean abutsOvernight(@NonNull RestrInterval another) {

        if (CalendarUtils.areConsecutiveDaysOfWeekLooped(this.dayOfWeek, another.getDayOfWeek())) {
            return abutsOvernight(this, another);
        } else if (CalendarUtils.areConsecutiveDaysOfWeekLooped(another.getDayOfWeek(), this.dayOfWeek)) {
            return abutsOvernight(another, this);
        }

        return false;
    }

    /**
     * Check if restriction rule is stronger.
     * Priority order is ALL_TIMES then TIME_MAX_PAID. The weakest being NONE.
     * For PAID, TIME_MAX a merge is needed, so none overrules.
     *
     * @param another The interval to examine
     * @return true if rule is stronger
     */
    public boolean overrules(RestrInterval another) {
        if (type == ALL_TIMES || another.getType() == NONE) {
            return true;
        } else if (type == TIME_MAX_PAID) {
            if (another.getType() == PAID) {
                return true;
            } else if (another.getType() == TIME_MAX) {
                if (getTimeMaxMinutes() <= another.getTimeMaxMinutes()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Subtract an interval from the current.
     * When the other interval contains the current, result is empty.
     * When the current interval contains the other, result is 2 intervals with a gap.
     * When the current interval starts before, result is the first part (leading)
     * When the current interval starts after, result is the second (trailing)
     *
     * @param another the Interval to subtract
     * @return List of intervals, can have a gap
     */
    public RestrIntervalsList subtract(RestrInterval another) {
        return subtract(this, another);
    }

    @Override
    public String toString() {
        return "RestrInterval{" +
                "type=" + type +
                " hourStart=" + TimeUnit.MILLISECONDS.toHours(startMillis) +
                ", hourEnd=" + TimeUnit.MILLISECONDS.toHours(endMillis) +
                '}';
    }

    /**
     * Builder class
     */
    public static class Builder {
        private int dayOfWeek;
        private int type;
        private int timeMax;
        private long startMillis;
        private long endMillis;

        public Builder(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            this.type = NONE;
            this.timeMax = Const.UNKNOWN_VALUE;
            this.startMillis = Const.UNKNOWN_VALUE;
            this.endMillis = Const.UNKNOWN_VALUE;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder timeMax(int minutes) {
            this.timeMax = minutes;
            return this;
        }

        public Builder startMillis(long millis) {
            this.startMillis = millis;
            return this;
        }

        public Builder endMillis(long millis) {
            this.endMillis = millis;
            return this;
        }

        public Builder startHour(float hour) {
            this.startMillis = (long) (hour * DateUtils.HOUR_IN_MILLIS);
            return this;
        }

        public Builder endHour(float hour) {
            this.endMillis = (long) (hour * DateUtils.HOUR_IN_MILLIS);
            return this;
        }

        public Builder interval(Interval interval) {
            this.startMillis = interval.getStartMillis();
            this.endMillis = interval.getEndMillis();
            return this;
        }

        public Builder allDay() {
            this.startMillis = 0;
            this.endMillis = DateUtils.DAY_IN_MILLIS;
            return this;
        }

        public RestrInterval build() {
            return new RestrInterval(this);
        }
    }
}
