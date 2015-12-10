package ng.prk.prkngandroid.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;


public class BusinessIntervalList extends ArrayList<BusinessInterval> {
    private final static String TAG = "BusinessIntervalList";

    public boolean addMerge(BusinessInterval interval) {
        if (interval == null) {
            return false;
        }

        // 24-hours intervals are added without merging
        if (!interval.isAllDay()) {
            if (interval.isClosed()) {
                // Closed intervals are skipped, unless if they are All-Day
                return false;
            }

            ListIterator<BusinessInterval> iter = listIterator();
            while (iter.hasNext()) {
                final BusinessInterval another = iter.next();
                if (another.isAllDay()) {
                    // Don't merge with a 24-hours interval
                    continue;
                }

                if (interval.isSameDay(another)) {
                    // Same type intervals
                    if (interval.abuts(another) || interval.overlaps(another)) {
                        // remove `another` from the list
                        iter.remove();
                        // merge both intervals, will be added to list later
                        interval.join(another);
                    }
                } else {
Log.v(TAG, "not same day");
                }
            }
        }

        // Add the interval to the list, after possible merge with other same-type intervals
        add(interval);

        return true;
    }

    public BusinessIntervalList getMergedItems() {
        return mergeListItems(this);
    }

    private static BusinessIntervalList mergeListItems(BusinessIntervalList list) {
        BusinessIntervalList mergedList = new BusinessIntervalList();
        for (BusinessInterval interval: list) {
            if (interval.isClosed()) {
                continue;
            }
            Log.v(TAG, interval.toString());

            mergedList.addMerge(interval);
        }

        return mergedList;
    }
}
