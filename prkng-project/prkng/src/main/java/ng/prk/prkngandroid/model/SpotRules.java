package ng.prk.prkngandroid.model;


import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.ArrayUtils;
import ng.prk.prkngandroid.util.CalendarUtils;

public class SpotRules {
    private final static String TAG = "SpotRules";
    private final static int INDEX_START = 0;
    private final static int INDEX_END = 1;

    private List<SpotRule> rules;

    public SpotRules(@NonNull List<SpotRule> rules) {
        this.rules = rules;
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

        // Initialize the days' arrays
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            daysMap.put(i, new RestrIntervalsList());
        }

        // Loop over each rule
        for (SpotRule rule : rules) {
            final SpotRuleAgenda agenda = rule.getAgenda();
            // Loop over days, for current rule
            for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                List<List<Float>> state = agenda.getDay(dayOfWeek);

                final RestrIntervalsList restrList = daysMap.get(i);

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
                            .hourlyRate(rule.getPaidHourlyRate())
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

    /**
     * @param intervals The agenda's intervals, sorted and starting today
     * @param today     Today's dayOfWeek
     * @param now       the daytime current timestamp, should be in a FreeParking interval
     * @return RestrInterval
     */
    private static RestrInterval getNextRestrIntervalWeekLooped(RestrIntervalsList intervals, int today, long now) {
        int indexNext = intervals.findNextRestrIntervalToday(now, today);
        if (indexNext == Const.UNKNOWN_VALUE) {
            indexNext = 0;
        }

        return intervals.get(indexNext);
    }

    /**
     * Current interval is of type NONE (parking allowed)
     *
     * @param intervals The agenda's intervals, sorted and starting today
     * @param today     Today's dayOfWeek
     * @param now       the daytime current timestamp
     * @return Millis remaining before the end of current interval
     */
    private static long getFreeParkingRemainingTime(RestrIntervalsList intervals, int today, long now) {
        final RestrInterval nextRestrInterval = getNextRestrIntervalWeekLooped(intervals, today, now);

        final boolean isWeekLoop = (intervals.get(0) == nextRestrInterval && nextRestrInterval.isBefore(now));
        final int nbDays = isWeekLoop ? CalendarUtils.WEEK_IN_DAYS :
                CalendarUtils.subtractDaysOfWeekLooped(nextRestrInterval.getDayOfWeek(), today);

        final long timeMaxStartOffset = (nextRestrInterval.getType() == Const.ParkingRestrType.TIME_MAX) ?
                nextRestrInterval.getTimeMaxMillis() : 0;

        return (nextRestrInterval.getStartMillis() - now)
                + timeMaxStartOffset
                + (DateUtils.DAY_IN_MILLIS * nbDays);
    }

    /**
     * For a 24/7 interval, return remaining parking time.
     * For NONE and PAID, duration is a full week.
     * For time-based restrictions, duration is TimeMax.
     *
     * @param intervals The agenda's intervals, sorted and starting today
     * @return Millis remaining following the 24/7 interval rule
     */
    private static long getFullWeekRemainingTime(RestrIntervalsList intervals) {
        final RestrInterval interval = intervals.get(0);

        switch (interval.getType()) {
            case Const.ParkingRestrType.ALL_TIMES:
                // Special case when viewing a NoParking spot.
                // UX doesn't normally display SpotInfo for such spots.
                return 0;
            case Const.ParkingRestrType.TIME_MAX:
            case Const.ParkingRestrType.TIME_MAX_PAID:
                return interval.getTimeMaxMillis();
            case Const.ParkingRestrType.PAID:
            case Const.ParkingRestrType.NONE:
            default:
                return DateUtils.WEEK_IN_MILLIS;
        }
    }

    public int getSize() {
        return rules == null ? 0 : rules.size();
    }

    /**
     * Get a clean list of the week's restriction intervals, merged and sorted by day and time.
     *
     * @return List of the week's restriction intervals
     */
    public RestrIntervalsList getParkingSpotAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

        final Map<Integer, RestrIntervalsList> dailyIntervals = getDailyIntervals(rules, today);
//        final Map<Integer, RestrIntervalsList> dailyIntervals = ApiSimulator.getTestScenarios();

        return getWeekIntervals(dailyIntervals, today);
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
    public static long getRemainingTime(RestrIntervalsList intervals, long now) {
        if (intervals == null || intervals.isEmpty()) {
            return DateUtils.WEEK_IN_MILLIS;
        }

        if (intervals.isTwentyFourSevenRestr()) {
            return getFullWeekRemainingTime(intervals);
        }

        final int today = CalendarUtils.getIsoDayOfWeek();

        int index = intervals.findLastAbuttingInterval(
                intervals.findContainingIntervalToday(now, today)
        );

        if (index != Const.UNKNOWN_VALUE) {
            final RestrInterval currentInterval = intervals.get(index);

            // Time between now/today and the interval's end
            final long timeRemaining = (currentInterval.getEndMillis() - now) +
                    (CalendarUtils.subtractDaysOfWeekLooped(currentInterval.getDayOfWeek(), today)
                            * DateUtils.DAY_IN_MILLIS
                    );

            switch (currentInterval.getType()) {
                case Const.ParkingRestrType.PAID:
                    return timeRemaining;
                case Const.ParkingRestrType.NONE:
                    return getFreeParkingRemainingTime(intervals, today, now);
                case Const.ParkingRestrType.TIME_MAX:
                    final Long timeMaxMillis = currentInterval.getTimeMaxMillis();
                    if (timeMaxMillis.compareTo(timeRemaining) < 0) {
                        return timeMaxMillis;
                    } else {
                        return getFreeParkingRemainingTime(intervals, today, now);
                    }
                case Const.ParkingRestrType.TIME_MAX_PAID:
                    // For TimeMaxPaid, time cannot be greater than TimeMax duration
                    return Math.min(timeRemaining, currentInterval.getTimeMaxMillis());
                case Const.ParkingRestrType.ALL_TIMES:
                    // Special case when viewing a NoParking spot.
                    // UX doesn't normally display SpotInfo for such spots.
                    return 0;
            }
        } else {
            return getFreeParkingRemainingTime(intervals, today, now);
        }

        // Free parking all week long!
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
//    private String getAllCodes() {
//        final int size = getSize();
//        if (size > 0) {
//            String[] codes = new String[size];
//            if (rules != null) {
//                int i = 0;
//                for (SpotRule rule : rules) {
//                    codes[i++] = rule.getCode();
//                }
//            }
//            return ArrayUtils.join(codes);
//        }
//
//        return null;
//    }

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
