package ng.prk.prkngandroid.model;

import java.util.ArrayList;

/**
 * Restriction Interval ArrayList
 */
public class RestrIntervalsList extends ArrayList<RestrInterval> {
    private final static String TAG = "RestrIntervalsList";

    public boolean addMerge(RestrInterval interval) {
        final RestrIntervalsList duplicates = new RestrIntervalsList();
        for (RestrInterval another : this) {
            if (interval.getType() == another.getType()) {
                if (interval.abuts(another) || interval.overlaps(another)) {
                    duplicates.add(another);
                    interval.join(another);
                }
            }
        }
        removeAll(duplicates);

        return super.add(interval);
    }
}
