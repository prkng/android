package ng.prk.prkngandroid.model;

import java.util.ArrayList;
import java.util.ListIterator;

import ng.prk.prkngandroid.Const;

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
                    .timeMax(getMinTimemax(i1.getTimeMax(), i2.getTimeMax()))
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
}
