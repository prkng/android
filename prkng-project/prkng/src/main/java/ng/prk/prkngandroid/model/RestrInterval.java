package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import org.joda.time.Duration;
import org.joda.time.Interval;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

/**
 * Restrictions Interval
 */
public class RestrInterval implements Comparable<RestrInterval> {
    private Interval interval;
    private int dayOfWeek;
    private int minuteOfDayStart;
    private int minuteOfDayEnd;
    private int type;

    /**
     * Constructor
     *
     * @param dayOfWeek 1-7
     * @param hourStart 0-24
     * @param hourEnd   0-24, must be greater than or equal to hourStart
     * @param type      Value
     */
    public RestrInterval(int dayOfWeek, float hourStart, float hourEnd, int type) {
        this.dayOfWeek = dayOfWeek;
        this.minuteOfDayStart = (int) (hourStart * CalendarUtils.HOUR_IN_MINUTES);
        this.minuteOfDayEnd = (int) (hourEnd * CalendarUtils.HOUR_IN_MINUTES);
        this.type = type;
        this.interval = new Interval(minuteOfDayStart * DateUtils.MINUTE_IN_MILLIS, minuteOfDayEnd * DateUtils.MINUTE_IN_MILLIS);
    }

    /**
     * Constructor for full-day restriction (24 hours)
     *
     * @param dayOfWeek
     * @param type
     */
    public RestrInterval(int dayOfWeek, int type) {
        this.dayOfWeek = dayOfWeek;
        this.type = type;
        this.minuteOfDayStart = Const.UNKNOWN_VALUE;
        this.minuteOfDayEnd = Const.UNKNOWN_VALUE;
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

    public int getType() {
        return type;
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
