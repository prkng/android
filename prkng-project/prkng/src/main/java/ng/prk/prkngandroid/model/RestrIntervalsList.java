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
     * @param interval the interval to evaluate
     * @return always true
     */
    public boolean addMerge(RestrInterval interval) {
        ListIterator<RestrInterval> iter = listIterator();
        while (iter.hasNext()) {
            final RestrInterval another = iter.next();
            if (interval.isSameType(another)) {
                if (interval.abuts(another) || interval.overlaps(another)) {
                    iter.remove();
                    interval.join(another);
                }
            } else {
                if (interval.overlaps(another)) {
                    iter.remove();

                    final RestrIntervalsList join = joinMixed(interval, another);
                    final int size = join.size();

                    // Recursive call
                    for (int i = 0; i < size; i++) {
                        addMerge(join.get(i));
                    }
                    return true;
                }
            }
        }
        add(interval);

        return true;
    }

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
                            .timeMax(getMinTimemax(i1.getTimeMaxMinutes(), i2.getTimeMaxMinutes()))
                            .build()
            );
        }

        return intervalsList;
    }

    private static int getMinTimemax(int t1, int t2) {
        if (t1 == Const.UNKNOWN_VALUE) {
            return t2;
        } else if (t2 == Const.UNKNOWN_VALUE) {
            return t1;
        } else {
            return Math.min(t1, t2);
        }
    }

    public int findContainingIntervalToday(long time, int today) {
        int i = 0;
        for (RestrInterval interval : this) {
            if (interval.getDayOfWeek() != today) {
                // ArrayList is sorted starting today, so stop once we reach a different day
                break;
            } else if (interval.contains(time)) {
                return i;
            }
            i++;
        }

        return Const.UNKNOWN_VALUE;
    }

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

        // Return same index if none other are found
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

    public boolean isTwentyFourSevenRestr() {
        if (size() != CalendarUtils.WEEK_IN_DAYS) {
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
