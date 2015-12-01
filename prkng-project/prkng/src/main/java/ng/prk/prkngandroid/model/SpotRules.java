package ng.prk.prkngandroid.model;


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

        final Map<Integer, RestrIntervalsList> dailyIntervals = getDailyIntervals(rules, today);
//        final Map<Integer, RestrIntervalsList> dailyIntervals = ApiSimulator.getTestScenarios();

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
