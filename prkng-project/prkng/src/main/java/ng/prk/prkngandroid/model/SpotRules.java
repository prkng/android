package ng.prk.prkngandroid.model;


import android.text.format.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.ApiSimulator;
import ng.prk.prkngandroid.util.ArrayUtils;
import ng.prk.prkngandroid.util.CalendarUtils;

public class SpotRules {
    private final static String TAG = "SpotRules";
    private final static int INDEX_START = 0;
    private final static int INDEX_END = 1;

    private List<SpotRule> rules;

    public SpotRules(List<SpotRule> rules) {
        this.rules = rules;
    }

    public int getSize() {
        return rules == null ? 0 : rules.size();
    }

    /**
     * Get a clean list of the week's restriction intervals, merged and sorted by day and time.
     *
     * @return List of the week's restriction intervals
     */
    public RestrIntervalsList getParkingAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

//        final Map<Integer, RestrIntervalsList> dailyIntervals = getDailyIntervals(rules, today);
        final Map<Integer, RestrIntervalsList> dailyIntervals = ApiSimulator.getTestScenarios();

        return getWeekIntervals(dailyIntervals, today);
    }

    /**
     * Merge the spot rules into a RestrIntervalsList, mapped per day for the whole week.
     * Each day has merged and sorted intervals.
     * Can contain empty items for days without restrictions
     *
     * @param rules Api agenda data
     * @param today Today's dayOfWeek
     * @return The week's restriction intervals lists
     */
    private static Map<Integer, RestrIntervalsList> getDailyIntervals(List<SpotRule> rules, int today) {

        final Map<Integer, RestrIntervalsList> daysMap = new HashMap<>();
        // Loop over each rule
        for (SpotRule rule : rules) {
            final SpotRuleAgenda agenda = rule.getAgenda();
            // Loop over days, for current rule
            for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                List<List<Float>> state = agenda.getDay(dayOfWeek);

                RestrIntervalsList restrList = daysMap.get(i);
                if (restrList == null) {
                    // Initialize the day's array if necessary
                    restrList = new RestrIntervalsList();
                    daysMap.put(i, restrList);
                }

                int type;
                int timeMax = Const.UNKNOWN_VALUE;
                if (rule.isTypeTimeMaxPaid()) {
                    type = Const.ParkingRestrType.TIME_MAX_PAID;
                    timeMax = rule.getTimeMaxParking();
                } else if (rule.isTypeTimeMax()) {
                    type = Const.ParkingRestrType.TIME_MAX;
                    timeMax = rule.getTimeMaxParking();
                } else if (rule.isTypePaid()) {
                    type = Const.ParkingRestrType.PAID;
                } else {
                    type = Const.ParkingRestrType.ALL_TIMES;
                }

                // Loop over periods, for current rule and dayOfWeek
                for (List<Float> restriction : state) {
                    // Add-Merge the day's interval, comparing it to other intervals of the same day
                    restrList.addMerge(new RestrInterval.Builder(dayOfWeek)
                            .startHour(restriction.get(INDEX_START))
                            .endHour(restriction.get(INDEX_END))
                            .type(type)
                            .timeMax(timeMax)
                            .build());
                }
            }
        }

        return daysMap;
    }

    /**
     * Merge the week's daily intervals into a single List.
     * Fills empty days with a full-day parking-allowed interval
     *
     * @param dailyIntervals The week's intervals, mapped for each day
     * @param today          Today's dayOfWeek
     * @return The week's intervals list
     */
    private static RestrIntervalsList getWeekIntervals(Map<Integer, RestrIntervalsList> dailyIntervals, int today) {

        final RestrIntervalsList mergedList = new RestrIntervalsList();
        // Loop over the week days. Using index (1-7) to get the HashMap in ascending days order
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            RestrIntervalsList restrList = dailyIntervals.get(i);
            if (restrList.isEmpty()) {
                // Add a 24hrs no-restriction rule
                final int day = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                restrList.add(new RestrInterval.Builder(day)
                                .type(Const.ParkingRestrType.NONE)
                                .allDay()
                                .build()
                );
            } else {
                Collections.sort(restrList);
            }

            // Add the day's clean list to the week's merged array
            mergedList.addAll(restrList);
        }

        return mergedList;
    }


    public long getRemainingTimeXX(RestrIntervalsList intervals, long now) {
        if (intervals == null || intervals.isEmpty()) {
            return DateUtils.WEEK_IN_MILLIS;
        }

        int indexCurrentInterval = intervals.findContainingInterval(now);

        if (indexCurrentInterval != Const.UNKNOWN_VALUE) {
            indexCurrentInterval = intervals.findLastAbuttingInterval(indexCurrentInterval);
        }



        return 0;
    }

    /**
     * Get the time remaining (in millis) before the end of the current interval,
     * or the beginning of the next restriction interval.
     * The duration takes handles multi-day intervals and the looping week.
     *
     * @param intervals The agenda's intervals, sorted and starting today
     * @param now       the daytime current timestamp
     * @return Millis remaining before the end of current interval. Max is WEEK_IN_MILLIS
     */
    public long getRemainingTime(RestrIntervalsList intervals, long now) {
        if (intervals == null || intervals.isEmpty()) {
            return DateUtils.WEEK_IN_MILLIS;
        }

        final int today = CalendarUtils.getIsoDayOfWeek();
        // Begin with a default Interval: Parking allowed till midnight
        // Stores the interval that best describes the end of the current period
        RestrInterval currentInterval = new RestrInterval.Builder(today)
                .type(Const.ParkingRestrType.NONE)
                .startMillis(now)
                .endHour(24f)
                .build();

        int idx = Const.UNKNOWN_VALUE;  // Index of the following interval
        int size = intervals.size();
        for (int i = 0; i < size; i++) {
            final RestrInterval interval = intervals.get(i);
            if (interval.getDayOfWeek() == today) {
                // Same day
                if (interval.contains(now)) {
                    // This is the current interval, containing `now`
                    currentInterval = interval;
                } else if (interval.isAfter(now)) {
                    // Found the next interval on the same day, so retain index and interrupt loop
                    // No need to check other intervals or other days
                    idx = i;
                    break;
                }
            } else if (currentInterval.abutsOvernight(interval) &&
                    currentInterval.isSameType(interval)) {
                // If the next day starts with an interval of the same type (multi-day 
                // restriction), we retain this interval instead
                currentInterval = interval;
            } else {
                // Next day, any other type of restriction-interval interrupts the loop.
                idx = i;
                break;
            }
        }

        if (currentInterval.getType() != Const.ParkingRestrType.NONE) {
            if (currentInterval.getType() == Const.ParkingRestrType.ALL_TIMES) {
                // Special case when viewing a NoParking spot.
                // UX doesn't normally display SpotInfo for such spots.
                return 0;
            }

            // Found the containing interval, and it's not initial default Interval
            // Return the time between now/today and the interval's end
            final long time = (currentInterval.getEndMillis() - now) + (DateUtils.DAY_IN_MILLIS *
                    CalendarUtils.subtractDaysOfWeekLooped(currentInterval.getDayOfWeek(), today)
            );

            if (currentInterval.getType() == Const.ParkingRestrType.TIME_MAX_PAID) {
                // For TimeMaxPaid, time cannot be greater than TimeMax duration
                return Math.min(time, currentInterval.getTimeMax() * DateUtils.MINUTE_IN_MILLIS);
            } else if (currentInterval.getType() == Const.ParkingRestrType.TIME_MAX) {
                final Long timeMaxMillis = currentInterval.getTimeMax() * DateUtils.MINUTE_IN_MILLIS;
                if (timeMaxMillis.compareTo(time) < 0) {
                    return timeMaxMillis;
                } else {
                    // TODO calculate end in next TimeMax interval
                    return 0;
                }
            }

            return time;
        } else if (idx != Const.UNKNOWN_VALUE) {
            // Found the following interval with restriction.
            // Current interval is of type NONE (parking allowed)
            // Return the time between now/today and the interval's beginning
            final RestrInterval nextInterval = intervals.get(idx);
            return (nextInterval.getStartMillis() - now) + (DateUtils.DAY_IN_MILLIS *
                    CalendarUtils.subtractDaysOfWeekLooped(nextInterval.getDayOfWeek(), today)
            );
        } else {
            // Didn't find current/next intervals, so check if wraps around week.
            // The week's first interval should interrupt the current interval
            final RestrInterval firstInterval = intervals.get(0);
            if (!firstInterval.contains(now)) {
                // Whole week, minus difference between now and the interval's beginning
                // If contains now, it means that the whole week is allowed.
                return DateUtils.WEEK_IN_MILLIS -
                        (now - firstInterval.getStartMillis());
            }
        }

        // Whole week, free parking!
        return DateUtils.WEEK_IN_MILLIS;
    }

    @Override
    public String toString() {
        return "SpotRules{size: " + getSize() +
                ", desc: " + getAllDescriptions() +
                "}";
    }

    /**
     * Used for debug or {@link #toString()}
     *
     * @return Codes of all the rules
     */
    private String getAllCodes() {
        final int size = getSize();
        if (size > 0) {
            String[] codes = new String[size];
            if (rules != null) {
                int i = 0;
                for (SpotRule rule : rules) {
                    codes[i++] = rule.getCode();
                }
            }
            return ArrayUtils.join(codes);
        }

        return null;
    }

    /**
     * Used for debug or {@link #toString()}
     *
     * @return Description of all the rules
     */
    private String getAllDescriptions() {
        final int size = getSize();
        if (size > 0) {
            String[] descriptions = new String[size];
            if (rules != null) {
                int i = 0;
                for (SpotRule rule : rules) {
                    descriptions[i++] = rule.getDescription();
                }
            }
            return ArrayUtils.join(descriptions, Const.LINE_SEPARATOR);
        }

        return null;
    }
}
