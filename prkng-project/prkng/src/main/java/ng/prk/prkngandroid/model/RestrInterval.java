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
public class RestrInterval implements Comparable<RestrInterval>,
        Const.ParkingRestrType {
    private final static String TAG = "RestrInterval";

    private Interval interval;
    private int dayOfWeek;
    private int minuteOfDayStart;
    private int minuteOfDayEnd;
    private int type;
    private int timeMax;

    /**
     * Constructor for full-day no-restrictions (24 hours)
     *
     * @param dayOfWeek
     */
    public RestrInterval(int dayOfWeek) {
        this(dayOfWeek, Const.UNKNOWN_VALUE, Const.UNKNOWN_VALUE, NONE, Const.UNKNOWN_VALUE);
    }

    /**
     * Constructor, without timeMax
     *
     * @param dayOfWeek 1-7
     * @param hourStart 0-24
     * @param hourEnd   0-24, must be greater than or equal to hourStart
     * @param type      ParkingRestrType
     */
    public RestrInterval(int dayOfWeek, float hourStart, float hourEnd, int type) {
        this(dayOfWeek, hourStart, hourEnd, type, Const.UNKNOWN_VALUE);
    }

    /**
     * Constructor, without Interval
     *
     * @param dayOfWeek      1-7
     * @param interval       The Interval
     * @param type           ParkingRestrType
     * @param minutesTimeMax Max allowed parking duration. Applies for TIME_MAX and TIME_MAX_PAID
     */
    public RestrInterval(int dayOfWeek, Interval interval, int type, int minutesTimeMax) {
        this(dayOfWeek,
                (float) interval.getStartMillis() / DateUtils.HOUR_IN_MILLIS,
                (float) interval.getEndMillis() / DateUtils.HOUR_IN_MILLIS,
                type,
                minutesTimeMax);
    }

    /**
     * Constructor, full
     *
     * @param dayOfWeek      1-7
     * @param hourStart      0-24
     * @param hourEnd        0-24, must be greater than or equal to hourStart
     * @param type           ParkingRestrType
     * @param minutesTimeMax Max allowed parking duration. Applies for TIME_MAX and TIME_MAX_PAID
     */
    public RestrInterval(int dayOfWeek, float hourStart, float hourEnd, int type, int minutesTimeMax) {
        this.dayOfWeek = dayOfWeek;
        this.type = type;
        if (type == NONE) {
            this.minuteOfDayStart = Const.UNKNOWN_VALUE;
            this.minuteOfDayEnd = Const.UNKNOWN_VALUE;
            this.interval = null;
            this.timeMax = Const.UNKNOWN_VALUE;
        } else {
            this.minuteOfDayStart = (int) (hourStart * CalendarUtils.HOUR_IN_MINUTES);
            this.minuteOfDayEnd = (int) (hourEnd * CalendarUtils.HOUR_IN_MINUTES);
//            this.interval = new Interval(minuteOfDayStart * DateUtils.MINUTE_IN_MILLIS, minuteOfDayEnd * DateUtils.MINUTE_IN_MILLIS);
            this.interval = new Interval(minuteOfDayStart, minuteOfDayEnd, TimeUnit.MINUTES);
            this.timeMax = minutesTimeMax;
        }
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getMinuteStart() {
        return minuteOfDayStart;
    }

    public int getMinuteEnd() {
        return minuteOfDayEnd;
    }

    /**
     * @return Restriction type
     * @see ng.prk.prkngandroid.Const.ParkingRestrType
     */
    public int getType() {
        return type;
    }

    public int getTimeMax() {
        return timeMax;
    }

    /**
     * Compares type and timeMax
     *
     * @param another The interval to compare to
     * @return true if has same type and timeMax value
     */
    public boolean hasSameType(RestrInterval another) {
        return (this.type == another.getType()) && (this.timeMax == another.getTimeMax());
    }

    /**
     * Check if restriction applies all day (24 hours)
     *
     * @return true for all-day restriction
     */
    public boolean isAllDay() {
        if (minuteOfDayStart == Const.UNKNOWN_VALUE || minuteOfDayEnd == Const.UNKNOWN_VALUE) {
            return true;
        } else {
            return minuteOfDayEnd - minuteOfDayStart >= CalendarUtils.DAY_IN_MINUTES;
        }
    }

    /**
     * Get the Interval object for easier manipulation
     *
     * @return Interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Join this RestrInterval with another. Objects should be of same type,
     * and the Intervals abut or overlap.
     * The result is an interval with the least startTime and greatest endTime.
     *
     * @param another the interval to join
     */
    public void join(RestrInterval another) {
        if (another == null) {
            return;
        }

        // Update minutesOfDay
        minuteOfDayStart = Math.min(minuteOfDayStart, another.getMinuteStart());
        minuteOfDayEnd = Math.max(minuteOfDayEnd, another.getMinuteEnd());

        // Update the Interval
        interval.join(another.getInterval());
    }

    /**
     * Does this interval abut with the interval specified.
     * <p/>
     * Intervals are inclusive of the start instant and exclusive of the end.
     * An interval abuts if it starts immediately after, or ends immediately
     * before this interval without overlap.
     * A zero duration interval abuts with itself.
     * <p/>
     * When two intervals are compared the result is one of three states:
     * (a) they abut, (b) there is a gap between them, (c) they overlap.
     * The abuts state takes precedence over the other two, thus a zero duration
     * interval at the start of a larger interval abuts and does not overlap.
     * <p/>
     * For example:
     * <pre>
     * [09:00 to 10:00) abuts [08:00 to 08:30)  = false (completely before)
     * [09:00 to 10:00) abuts [08:00 to 09:00)  = true
     * [09:00 to 10:00) abuts [08:00 to 09:01)  = false (overlaps)
     *
     * [09:00 to 10:00) abuts [09:00 to 09:00)  = true
     * [09:00 to 10:00) abuts [09:00 to 09:01)  = false (overlaps)
     *
     * [09:00 to 10:00) abuts [10:00 to 10:00)  = true
     * [09:00 to 10:00) abuts [10:00 to 10:30)  = true
     *
     * [09:00 to 10:00) abuts [10:30 to 11:00)  = false (completely after)
     *
     * [14:00 to 14:00) abuts [14:00 to 14:00)  = true
     * [14:00 to 14:00) abuts [14:00 to 15:00)  = true
     * [14:00 to 14:00) abuts [13:00 to 14:00)  = true
     * </pre>
     *
     * @param another the interval to examine, null means now
     * @return true if the interval abuts
     */
    public boolean abuts(RestrInterval another) {
        return interval.abuts(another.getInterval());
    }

    /**
     * Does this time interval overlap the specified time interval.
     * <p/>
     * Intervals are inclusive of the start instant and exclusive of the end.
     * An interval overlaps another if it shares some common part of the
     * datetime continuum.
     * <p/>
     * When two intervals are compared the result is one of three states:
     * (a) they abut, (b) there is a gap between them, (c) they overlap.
     * The abuts state takes precedence over the other two, thus a zero duration
     * interval at the start of a larger interval abuts and does not overlap.
     * <p/>
     * For example:
     * <pre>
     * [09:00 to 10:00) overlaps [08:00 to 08:30)  = false (completely before)
     * [09:00 to 10:00) overlaps [08:00 to 09:00)  = false (abuts before)
     * [09:00 to 10:00) overlaps [08:00 to 09:30)  = true
     * [09:00 to 10:00) overlaps [08:00 to 10:00)  = true
     * [09:00 to 10:00) overlaps [08:00 to 11:00)  = true
     *
     * [09:00 to 10:00) overlaps [09:00 to 09:00)  = false (abuts before)
     * [09:00 to 10:00) overlaps [09:00 to 09:30)  = true
     * [09:00 to 10:00) overlaps [09:00 to 10:00)  = true
     * [09:00 to 10:00) overlaps [09:00 to 11:00)  = true
     *
     * [09:00 to 10:00) overlaps [09:30 to 09:30)  = true
     * [09:00 to 10:00) overlaps [09:30 to 10:00)  = true
     * [09:00 to 10:00) overlaps [09:30 to 11:00)  = true
     *
     * [09:00 to 10:00) overlaps [10:00 to 10:00)  = false (abuts after)
     * [09:00 to 10:00) overlaps [10:00 to 11:00)  = false (abuts after)
     *
     * [09:00 to 10:00) overlaps [10:30 to 11:00)  = false (completely after)
     *
     * [14:00 to 14:00) overlaps [14:00 to 14:00)  = false (abuts before and after)
     * [14:00 to 14:00) overlaps [13:00 to 15:00)  = true
     * </pre>
     *
     * @param another , null means a zero length interval now
     * @return true if the time intervals overlap
     */
    public boolean overlaps(RestrInterval another) {
        return interval.overlaps(another.getInterval());
    }

    /**
     * Check if restriction rule is stronger.
     * Priority order is ALL_TIMES then TIME_MAX_PAID.
     * For PAID, TIME_MAX a merge is needed.
     *
     * @param another The interval to examine
     * @return true if rule is stronger
     */
    public boolean overrules(RestrInterval another) {
        // TODO incomplete, adding other types can improve performance
        if (type == ALL_TIMES) {
            return true;
        } else if (type == TIME_MAX_PAID) {
            if (another.getType() == PAID) {
                return true;
            } else if (another.getType() == TIME_MAX) {
                if (getTimeMax() <= another.getTimeMax()) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Subtract an interval from the current.
     * When the other interval contains the current, result is empty.
     * When the other interval is contained, result is 2 intervals with a gap.
     *
     * @param another the Interval to examine
     * @return List of intervals, can have a gap
     */
    public RestrIntervalsList subtract(RestrInterval another) {
        final RestrIntervalsList intervalsList = new RestrIntervalsList();

        if (another.getInterval().contains(interval)) {
            // The subtracted interval is bigger than current, return empty result.
            return intervalsList;
        } else if (interval.contains(another.getInterval())) {
            // Split current into 2 parts, surrounding the other interval
            if (Float.compare(minuteOfDayStart, another.getMinuteStart()) < 0) {
                // The first (leading) part
                intervalsList.add(new RestrInterval(
                        dayOfWeek,
                        (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES,
                        (float) another.getMinuteStart() / CalendarUtils.HOUR_IN_MINUTES,
                        type,
                        timeMax
                ));
            }

            if (Float.compare(another.getMinuteEnd(), this.minuteOfDayEnd) < 0) {
                // The last (trailing) part
                intervalsList.add(new RestrInterval(
                        dayOfWeek,
                        (float) another.getMinuteEnd() / CalendarUtils.HOUR_IN_MINUTES,
                        (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES,
                        type,
                        timeMax
                ));
            }
        } else if (interval.startsBefore(another.getInterval())) {
            // Keep the first (leading) part only
            if (Float.compare(minuteOfDayStart, another.getMinuteStart()) < 0) {
                intervalsList.add(new RestrInterval(
                        dayOfWeek,
                        (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES,
                        (float) another.getMinuteStart() / CalendarUtils.HOUR_IN_MINUTES,
                        type,
                        timeMax
                ));
            }
        } else if (interval.startsAfter(another.getInterval())) {
            // Keep the last (trailing) part only
            if (Float.compare(another.getMinuteEnd(), this.minuteOfDayEnd) < 0) {
                intervalsList.add(new RestrInterval(
                        dayOfWeek,
                        (float) another.getMinuteEnd() / CalendarUtils.HOUR_IN_MINUTES,
                        (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES,
                        type,
                        timeMax
                ));
            }
        }

        return intervalsList;
    }

    /**
     * Implements Comparable.
     * Based on the startTime and ignoring endTime, which is sufficient to sort the List.
     * Special cases are handled by RestrIntervalsList#addMerge()
     *
     * @param another the time interval to compare to
     * @return true if starts before the other interval
     * @see ng.prk.prkngandroid.model.RestrIntervalsList#addMerge(RestrInterval interval)
     */
    @Override
    public int compareTo(@NonNull RestrInterval another) {
        return interval.compareTo(another.getInterval());
    }

    @Override
    public String toString() {
        return "RestrInterval{" +
                "type=" + type +
                ", hourStart=" + String.format("%.2f", (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES) +
                ", hourEnd=" + String.format("%.2f", (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES) +
                '}';
    }
}
