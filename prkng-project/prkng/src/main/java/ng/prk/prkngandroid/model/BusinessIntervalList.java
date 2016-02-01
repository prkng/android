package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.ListIterator;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;


public class BusinessIntervalList extends ArrayList<BusinessInterval> {
    private final static String TAG = "BusinessIntervalList";

    /**
     * Add an interval to the current list, merging it with other similar intervals of the same day.
     * Merges OPEN and FREE.
     * 24-hours (allDay) are added as is.
     * Does not filter out CLOSED, should be filtered beforehand.
     * Does not merge intervals that abut overnight.
     *
     * @param interval the interval to add/merge
     * @return always true
     */
    public boolean addMerge(@NonNull BusinessInterval interval) {
        // 24-hours intervals are added without merging
        if (!interval.isAllDay()) {
            ListIterator<BusinessInterval> iter = listIterator(size());

            while (iter.hasPrevious()) {
                final BusinessInterval another = iter.previous();
                if (another.isAllDay()) {
                    // Don't merge with a 24-hours interval
                    continue;
                }

                if (interval.isSameDay(another)) {
                    // Same type intervals
                    if (interval.abuts(another) || interval.overlaps(another)) {
                        // remove `another` from the list
                        iter.remove();
                        // merge both intervals, the new merged interval will be added later
                        interval.join(another);
                    }
                } else {
                    if (interval.abutsOvernight(another)) {
                        // remove `another` from the list
                        iter.remove();
                        // merge both intervals, the new merged interval will be added later
                        interval.joinOvernight(another);
                    } else {
                        break;
                    }
                }
            }
        }

        // Add the interval to the list, after possible merge with other same-type intervals
        return add(interval);
    }

    /**
     * Get a clean business day agenda with merged items, including overnight and week-loop merges.
     * In some special cases, a weekday can have 2 items.
     *
     * @return Business day agenda
     */
    public BusinessIntervalList getMergedItems() {
        final BusinessIntervalList addMergedList = addMergeListItems(this);

        return mergeWeekLoopItems(addMergedList);
    }

    /**
     * Get a list with add/merged items, where items of the same day/type are merged.
     * Also excludes items of type CLOSED (if not full-day).
     * This is a 1st-pass, where the list still needs a smart-merge (2nd pass) for
     * items that about overnight.
     *
     * @param list the list with items to merge
     * @return List with add/merged items, merge first-pass.
     */
    private static BusinessIntervalList addMergeListItems(BusinessIntervalList list) {
        final BusinessIntervalList mergeResult = new BusinessIntervalList();

        final int size = list.size();
        for (int i = 0; i < size; i++) {
            final BusinessInterval interval = list.get(i);
            if (interval == null) {
                continue;
            }

            if (interval.isAllDay() || !interval.isClosed()) {
                if (size > i + 1) {
                    final BusinessInterval nextInterval = list.get(i + 1);
                    if (interval.isNaturalMerge(nextInterval)) {
                        nextInterval.join(interval);

                        list.set(i + 1, nextInterval);
                        continue;
                    }
                }
                mergeResult.addMerge(interval);
            }
        }

        return mergeResult;
    }

    /**
     * @param list
     * @return
     */
    private static BusinessIntervalList mergeWeekLoopItems(BusinessIntervalList list) {
        final BusinessInterval firstInterval = list.get(0);

        if (firstInterval.isBefore(CalendarUtils.todayMillis())) {
            final BusinessInterval lastInterval = list.get(list.size() - 1);

            if (!lastInterval.isAllDay() && lastInterval.abutsOvernight(firstInterval)) {
                lastInterval.joinOvernight(firstInterval);
                list.set(list.size() - 1, lastInterval);
                list.remove(0);
            }
        }

        return list;
    }

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

        BusinessInterval previous = get(index);
        for (int i = 1 + index; i < s; i++) {
            final BusinessInterval current = get(i);
            if (current.isSameType(previous) && current.isSamePrice(previous) && current.abutsOvernight(previous)) {
                indexFound = i;
                previous = current;
            } else {
                break;
            }
        }

        return indexFound;
    }

    public boolean isTwentyFourSevenRestr() {
        BusinessInterval firstInterval = get(0);
        for (BusinessInterval interval : this) {
            if (!interval.isAllDay() || !firstInterval.isSameType(interval) || !firstInterval.isSamePrice(interval)) {
                return false;
            }
        }

        return true;
    }

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }
}
