package ng.prk.prkngandroid.model;

import android.util.Log;

import java.util.ArrayList;

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
        Log.v(TAG, "addMerge");
        final RestrIntervalsList mixedSubstractions = new RestrIntervalsList();
        final RestrIntervalsList duplicates = new RestrIntervalsList();
        for (RestrInterval another : this) {
            if (interval.getType() == another.getType()) {
                if (interval.abuts(another) || interval.overlaps(another)) {
                    duplicates.add(another);
                    interval.join(another);
                }
            } else {
                if (interval.overlaps(another)) {
                    duplicates.add(another);
                    mixedSubstractions.addAll(joinMixed(interval, another));
                } else {
                    add(interval);
                }
            }
        }
        removeAll(duplicates);

        if (!mixedSubstractions.isEmpty()) {
            addAll(mixedSubstractions);
        } else {
            add(interval);
        }

        return true;
    }

    private static RestrIntervalsList joinMixed(RestrInterval i1, RestrInterval i2) {
        Log.v(TAG, "joinMixed");
        final RestrIntervalsList intervalsList = new RestrIntervalsList();

        if (i1.overrules(i2)) {
            Log.v(TAG, "i1 overrules");
            intervalsList.add(i1);
            intervalsList.addAll(i2.subtract(i1));
        } else if (i2.overrules(i1)) {
            Log.v(TAG, "i2 overrules");
            intervalsList.add(i2);
            intervalsList.addAll(i1.subtract(i2));
        } else {
            Log.v(TAG, "no overrule");

        }

        return intervalsList;
    }

}
