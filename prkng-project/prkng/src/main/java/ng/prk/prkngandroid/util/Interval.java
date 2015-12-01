package ng.prk.prkngandroid.util;

import java.util.concurrent.TimeUnit;

public class Interval implements Comparable<Interval> {
    private final static String TAG = "Interval";

    protected long startMillis;
    protected long endMillis;

    public Interval(long start, long end) {
        if (Long.valueOf(start).compareTo(end) > 0) {
            throw new IllegalArgumentException("'start' cannot be greater than 'end'");
        }

        this.startMillis = start;
        this.endMillis = end;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public long getDuration() {
        return endMillis - startMillis;
    }

    /**
     * Join with another interval. Does not join if a gap exists.
     *
     * @param another Interval to join with, must abut or overlap
     */
    public void join(Interval another) {
        if (another == null) {
            return;
        }

        if (overlaps(another) || abuts(another)) {
            this.startMillis = Math.min(startMillis, another.startMillis);
            this.endMillis = Math.max(endMillis, another.endMillis);
        }
    }

    /**
     * Gets the overlap between this interval and another interval.
     * <p/>
     * Intervals are inclusive of the start instant and exclusive of the end.
     * An interval overlaps another if it shares some common part of the
     * datetime continuum. This method returns the amount of the overlap,
     * only if the intervals actually do overlap.
     * If the intervals do not overlap, then null is returned.
     * <p/>
     * When two intervals are compared the result is one of three states:
     * (a) they abut, (b) there is a gap between them, (c) they overlap.
     * The abuts state takes precedence over the other two, thus a zero duration
     * interval at the start of a larger interval abuts and does not overlap.
     * <p/>
     * The chronology of the returned interval is the same as that of
     * this interval (the chronology of the interval parameter is not used).
     * Note that the use of the chronology was only correctly implemented
     * in version 1.3.
     *
     * @param another the interval to examine, null means now
     * @return the overlap interval, null if no overlap
     */
    public Interval overlap(Interval another) {
        if (!overlaps(another)) {
            return null;
        } else {
            return new Interval(
                    Math.max(this.startMillis, another.getStartMillis()),
                    Math.min(this.endMillis, another.getEndMillis())
            );
        }
    }

    /**
     * Does this interval abut with the interval specified.
     * <p/>
     * Intervals are inclusive of the startMillis instant and exclusive of the endMillis.
     * An interval abuts if it starts immediately after, or ends immediately
     * before this interval without overlap.
     * A zero duration interval abuts with itself.
     * <p/>
     * When two intervals are compared the result is one of three states:
     * (a) they abut, (b) there is a gap between them, (c) they overlap.
     * The abuts state takes precedence over the other two, thus a zero duration
     * interval at the startMillis of a larger interval abuts and does not overlap.
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
    public boolean abuts(Interval another) {
        if (another == null) {
            final long now = System.currentTimeMillis();
            return Long.valueOf(this.startMillis).compareTo(now) == 0 ||
                    Long.valueOf(this.endMillis).compareTo(now) == 0;
        } else {
            return Long.valueOf(this.startMillis).compareTo(another.endMillis) == 0 ||
                    Long.valueOf(this.endMillis).compareTo(another.startMillis) == 0;
        }
    }

    /**
     * Does this time interval overlap the specified time interval.
     * <p/>
     * Intervals are inclusive of the startMillis instant and exclusive of the endMillis.
     * An interval overlaps another if it shares some common part of the
     * datetime continuum.
     * <p/>
     * When two intervals are compared the result is one of three states:
     * (a) they abut, (b) there is a gap between them, (c) they overlap.
     * The abuts state takes precedence over the other two, thus a zero duration
     * interval at the startMillis of a larger interval abuts and does not overlap.
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
    public boolean overlaps(Interval another) {
        if (another == null) {
            final long now = System.currentTimeMillis();
            return Long.valueOf(this.startMillis).compareTo(now) < 0 &&
                    Long.valueOf(now).compareTo(this.endMillis) < 0;
        } else {
            return Long.valueOf(this.startMillis).compareTo(another.endMillis) < 0 &&
                    Long.valueOf(another.startMillis).compareTo(endMillis) < 0;
        }
    }

    public boolean isBefore(long millis) {
        return Long.valueOf(this.endMillis).compareTo(millis) <= 0;
    }

    public boolean isBeforeNow() {
        return isBefore(System.currentTimeMillis());
    }

    public boolean isBefore(Interval another) {
        return (another == null) ? isBeforeNow() : isBefore(another.startMillis);
    }

    public boolean isAfter(long millis) {
        return Long.valueOf(this.startMillis).compareTo(millis) >= 0;
    }

    public boolean isAfterNow() {
        return isAfter(System.currentTimeMillis());
    }

    public boolean isAfter(Interval another) {
        return (another == null) ? isAfterNow() : isAfter(another.endMillis);
    }

    public boolean contains(long millis) {
        return Long.valueOf(this.startMillis).compareTo(millis) <= 0 &&
                Long.valueOf(this.endMillis).compareTo(millis) >= 0;
    }

    public boolean containsNow() {
        return contains(System.currentTimeMillis());
    }

    public boolean contains(Interval another) {
        if (another == null) {
            return containsNow();
        } else {
            return contains(another.startMillis) && contains(another.endMillis);
        }
    }

    public boolean startsBefore(Interval another) {
        if (another == null) {
            return false;
        } else {
            return another.isAfter(this.startMillis);
        }
    }

    public boolean startsAfter(Interval another) {
        if (another == null) {
            return false;
        } else {
            return isAfter(another.startMillis);
        }
    }

    public boolean endsBefore(Interval another) {
        if (another == null) {
            return false;
        } else {
            return isBefore(another.endMillis);
        }
    }

    public boolean endsAfter(Interval another) {
        if (another == null) {
            return false;
        } else {
            return another.isBefore(this.endMillis);
        }
    }

    public int compareTo(long millis) {
        return Long.valueOf(this.startMillis).compareTo(millis);
    }

    public int compareToNow() {
        return compareTo(System.currentTimeMillis());
    }

    /**
     * Implements Comparable.
     * Based on the startTime and ignoring endTime, which is sufficient to sort the List.
     * Special cases are handled by RestrIntervalsList#addMerge()
     *
     * @param another the time interval to compare to
     * @return true if starts before the other interval
     * @see ng.prk.prkngandroid.model.RestrIntervalsList#addMerge
     */
    @Override
    public int compareTo(Interval another) {
        return another == null ? compareToNow() : compareTo(another.startMillis);
    }

    @Override
    public String toString() {
        return "Interval{" +
                " hourStart=" + TimeUnit.MILLISECONDS.toHours(startMillis) +
                ", hourEnd=" + TimeUnit.MILLISECONDS.toHours(endMillis) +
                '}';
    }
}
