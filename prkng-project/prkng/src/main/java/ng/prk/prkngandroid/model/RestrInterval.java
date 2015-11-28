package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import org.joda.time.Duration;
import org.joda.time.Interval;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

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
        this.dayOfWeek = dayOfWeek;
        this.type = NONE;
        this.minuteOfDayStart = Const.UNKNOWN_VALUE;
        this.minuteOfDayEnd = Const.UNKNOWN_VALUE;
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
     * Constructor, full
     *
     * @param dayOfWeek 1-7
     * @param hourStart 0-24
     * @param hourEnd   0-24, must be greater than or equal to hourStart
     * @param type      ParkingRestrType
     * @param timeMax   1-24 hours
     */
    public RestrInterval(int dayOfWeek, float hourStart, float hourEnd, int type, int timeMax) {
        this.dayOfWeek = dayOfWeek;
        this.type = type;
        if (type == NONE) {
            this.minuteOfDayStart = Const.UNKNOWN_VALUE;
            this.minuteOfDayEnd = Const.UNKNOWN_VALUE;
            this.interval = null;
            this.timeMax = Const.UNKNOWN_VALUE;
        } else {
            this.minuteOfDayStart = (int) ((hourStart - 2f) * CalendarUtils.HOUR_IN_MINUTES);
            this.minuteOfDayEnd = (int) ((hourEnd + 2f) * CalendarUtils.HOUR_IN_MINUTES);
            this.interval = new Interval(minuteOfDayStart * DateUtils.MINUTE_IN_MILLIS, minuteOfDayEnd * DateUtils.MINUTE_IN_MILLIS);
            this.timeMax = timeMax;
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
     * Check if restriction applies all day (24 hours)
     *
     * @return true for all-day restriction
     */
    public boolean isAllDay() {
        return (interval == null) ||
                interval.toDuration().compareTo(new Duration(DateUtils.DAY_IN_MILLIS)) >= 0;
    }

    /**
     * Get the Interval object for easier manipulation
     *
     * @return
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
        interval = new Interval(
                minuteOfDayStart * DateUtils.MINUTE_IN_MILLIS,
                minuteOfDayEnd * DateUtils.MINUTE_IN_MILLIS);
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
     * @since 1.1
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

    public boolean overrules(RestrInterval another) {
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


    public RestrIntervalsList subtract(RestrInterval another) {
        Log.v(TAG, "--- subtract");
        Log.v(TAG, interval.toString() + " vs " + another.getInterval().toString());

        final RestrIntervalsList intervalsList = new RestrIntervalsList();

        if (another.getInterval().contains(interval)) {
            Log.v(TAG, "is contained");
            // The subtracted interval is bigger than current, return empty result.
            return intervalsList;
        } else if (interval.contains(another.getInterval())) {
            Log.v(TAG, "contains");

            // Split current into 2 parts, surrounding the other interval
            final RestrInterval before = new RestrInterval(
                    dayOfWeek,
                    (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES,
                    (float) another.getMinuteStart() / CalendarUtils.HOUR_IN_MINUTES,
                    type,
                    timeMax
            );
            intervalsList.add(before);

            final RestrInterval after = new RestrInterval(
                    dayOfWeek,
                    (float) another.getMinuteEnd() / CalendarUtils.HOUR_IN_MINUTES,
                    (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES,
                    type,
                    timeMax
            );
            intervalsList.add(after);
        } else if (interval.isBefore(another.getInterval())) {
            Log.v(TAG, "isBefore");
            // Keep the first (leading) part only
            final RestrInterval before = new RestrInterval(
                    dayOfWeek,
                    (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES,
                    (float) another.getMinuteStart() / CalendarUtils.HOUR_IN_MINUTES,
                    type,
                    timeMax
            );
            intervalsList.add(before);
        } else if (interval.isAfter(another.getInterval())) {
            Log.v(TAG, "isAfter");

            // Keep the last (trailing) part only
            final RestrInterval after = new RestrInterval(
                    dayOfWeek,
                    (float) another.getMinuteEnd() / CalendarUtils.HOUR_IN_MINUTES,
                    (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES,
                    type,
                    timeMax
            );
            intervalsList.add(after);
        } else {
            Log.e(TAG, "skipped?!");
        }

//        intervalsList.add(this);
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
        return interval.getStart().compareTo(another.getInterval().getStart());
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
