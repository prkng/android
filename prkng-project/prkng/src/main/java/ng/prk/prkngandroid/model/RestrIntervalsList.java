package ng.prk.prkngandroid.model;

import java.util.ArrayList;
import java.util.ListIterator;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

/**
 * Restriction Interval ArrayList
 */
public class RestrIntervalsList extends ArrayList<RestrInterval> {
    private final static String TAG = "RestrIntervalsList";

    /**
     * Add interval to a day's intervals list. Should not be used with an array of mixed days.
     * Intervals of same types are merged if they abut or overlap.
     * Intervals of different types are subtracted following their rules' priorities
     *
     * @param interval the interval to add-merge to current list
     * @return always true
     */
    public boolean addMerge(RestrInterval interval) {
        ListIterator<RestrInterval> iter = listIterator();
        while (iter.hasNext()) {
            final RestrInterval another = iter.next();
            if (interval.isSameType(another)) {
                // Same type intervals
                if (interval.abuts(another) || interval.overlaps(another)) {
                    // remove `another` from the list
                    iter.remove();
                    // merge both intervals, will be added to list later
                    interval.join(another);
                }
            } else {
                // Different type intervals
                if (interval.overlaps(another)) {
                    // remove `another` from the list
                    iter.remove();

                    // Get the result of merging the two intervals
                    final RestrIntervalsList join = joinMixed(interval, another);
                    final int size = join.size();

                    // Recursive call, adding the merge result to the original list.
                    for (int i = 0; i < size; i++) {
                        addMerge(join.get(i));
                    }
                    // End after the recursive call. The interval (arg) will be added to the list
                    // in the recursive call, from the joinMixed() results.
                    return true;
                }
            }
        }
        // Add the interval to the list, after possible merge with other same-type intervals
        add(interval);

        return true;
    }

    /**
     * Join intervals of different types into a new RestrIntervalsList.
     * Comparing rules, adds stronger intervals, subtracting others.
     * Result can contain gaps as result of subtraction.
     * Can als add TIME_MAX_PAID intervals as result of joining time-restricted intervals,
     * handling shortest durations.
     *
     * @param i1 the interval to add
     * @param i2 the interval to add, of different type than i1
     * @return The result RestrIntervalsList, with merged and subracted intervals
     */
    private static RestrIntervalsList joinMixed(RestrInterval i1, RestrInterval i2) {
        final RestrIntervalsList intervalsList = new RestrIntervalsList();

        if (i1.overrules(i2)) {
            intervalsList.add(i1);
            intervalsList.addAll(i2.subtract(i1));
        } else if (i2.overrules(i1)) {
            intervalsList.addAll(i1.subtract(i2));
            intervalsList.add(i2);
        } else {
            intervalsList.addAll(i1.subtract(i2));
            intervalsList.addAll(i2.subtract(i1));

            // Build new interval
            intervalsList.add(new RestrInterval.Builder(i1.getDayOfWeek())
                            .interval(i1.overlap(i2))
                            .type(Const.ParkingRestrType.TIME_MAX_PAID)
                            .timeMax(RestrInterval.getMinTimemax(i1.getTimeMaxMinutes(), i2.getTimeMaxMinutes()))
                            .hourlyRate(i1.getHourlyRate())
                            .build()
            );
        }

        return intervalsList;
    }

    /**
     * Get the index of today's interval tha contains the timestamp.
     * For performance reason (avoid useless looping), this method cannot be used to search
     * for intervals of a day other than the first day of the current week
     *
     * @param time  the current daytime timestamp
     * @param today Today's dayOfWeek
     * @return int the index of today's interval tha contains today's timestamp
     */
    public int findContainingIntervalToday(long time, int today) {
        if (size() == 0) {
            return Const.UNKNOWN_VALUE;
        }

        int i = 0;
        for (RestrInterval interval : this) {
            if (interval.getDayOfWeek() != today) {
                // ArrayList is sorted starting today, so reaching a different day means not-found
                break;
            } else if (interval.contains(time)) {
                return i;
            }
            i++;
        }

        return Const.UNKNOWN_VALUE;
    }

    /**
     * Get the index of today's restricting interval (not FreeParking) that stars after the timestamp.
     * For performance reason (avoid useless looping), this method cannot be used to search
     * for intervals of a day other than the first day of the current week
     *
     * @param time  the current daytime timestamp
     * @param today Today's dayOfWeek
     * @return int the index of the restricting interval the follows today's timestamp
     */
    public int findNextRestrIntervalToday(long time, int today) {
        if (size() == 0) {
            return Const.UNKNOWN_VALUE;
        }

        int i = 0;
        for (RestrInterval interval : this) {
            if ((interval.getDayOfWeek() != today) || interval.isAfter(time)) {
                if (interval.getType() != Const.ParkingRestrType.NONE) {
                    return i;
                }
            }
            i++;
        }

        return Const.UNKNOWN_VALUE;
    }

    /**
     * Get the index of the last interval over the (non-looped) week that abuts specified interval,
     * of same type. Returns same index if none found.
     * Does not handle looping week because the algorithm takes care of that case
     * in {@link #isTwentyFourSevenRestr()}
     *
     * @param index of the interval to examine
     * @return int index of the week's last interval that abuts the specified interval
     */
    public int findLastAbuttingInterval(int index) {
        if (index == Const.UNKNOWN_VALUE) {
            return Const.UNKNOWN_VALUE;
        }

        final int s = size();
        if (index >= s) {
            throwIndexOutOfBoundsException(index, s);
        }

        if (1 + index > s) {
            // Index provided is that of the last Interval
            return index;
        }

        // Default to same index if none is found
        int indexFound = index;

        RestrInterval previous = get(index);
        for (int i = 1 + index; i < s; i++) {
            final RestrInterval current = get(i);
            if (current.isSameType(previous) && current.abutsOvernight(previous)) {
                indexFound = i;
                previous = current;
            } else {
                break;
            }
        }

        return indexFound;
    }

    /**
     * Check if the week holds a single restriction type applied 24/7.
     *
     * @return true if all days are 24-hour interval of same type
     */
    public boolean isTwentyFourSevenRestr() {
        if (size() != CalendarUtils.WEEK_IN_DAYS) {
            // Having more than 7 intervals means mixed types, so return false
            return false;
        }

        int firstType = get(0).getType();
        for (RestrInterval interval : this) {
            if (!interval.isAllDay() || interval.getType() != firstType) {
                return false;
            }
        }

        return true;
    }

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }
}
