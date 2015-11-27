package ng.prk.prkngandroid.model;


import java.util.ArrayList;
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

    public List<RestrInterval> getParkingAgenda() {
        int today = CalendarUtils.getIsoDayOfWeek();

        final Map<Integer, IntervalsList> dailyIntervals = getDailyIntervalsList(rules, today);
//        final Map<Integer, List<RestrInterval>> dailyRestrMap = getDailyRestrMap(rules, today);

//        final List<ParkingRestrPeriod> mergedDailyStates = new ArrayList<>();
//        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
//            List<ParkingRestrPeriod> dayStates = dailyRestrMap.get(i);
//            if (dayStates.isEmpty()) {
//                dayStates.add(new ParkingRestrPeriod(i, Const.ParkingRestrType.NONE));
//            }
//            Collections.sort(dayStates);
//            mergedDailyStates.addAll(dayStates);
//        }

//        return mergeSimilarDailyRestr(dailyRestrMap, today);
        return getIntervalsList(dailyIntervals, today);
    }

    /**
     * Group restrictions by day, unsorted and unmerged
     *
     * @param rules
     * @return
     */
    @Deprecated
    private static Map<Integer, List<RestrInterval>> getDailyRestrMap(List<SpotRule> rules, int today) {

        final Map<Integer, List<RestrInterval>> daysMap = new HashMap<>();
        // Loop over each rule
        for (SpotRule rule : rules) {
            final SpotRuleAgenda agenda = rule.getAgenda();
            // Loop over days, for current rule
            for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                List<List<Float>> state = agenda.getDay(dayOfWeek);

                List<RestrInterval> restrList = daysMap.get(i);
                if (restrList == null) {
                    restrList = new ArrayList<>();
                    daysMap.put(i, restrList);
                }

                int type;
                if (rule.isTypeTimeMaxPaid()) {
                    type = Const.ParkingRestrType.TIME_MAX_PAID;
                } else if (rule.isTypeTimeMax()) {
                    type = Const.ParkingRestrType.TIME_MAX;
                } else if (rule.isTypePaid()) {
                    type = Const.ParkingRestrType.PAID;
                } else {
                    type = Const.ParkingRestrType.ALL_TIMES;
                }
                // Loop over periods, for current rule and dayOfWeek
                for (List<Float> restriction : state) {
                    restrList.add(
                            new RestrInterval(dayOfWeek,
                                    restriction.get(INDEX_START),
                                    restriction.get(INDEX_END),
                                    type)
                    );
                }
            }
        }

        return daysMap;
    }

    private static Map<Integer, IntervalsList> getDailyIntervalsList(List<SpotRule> rules, int today) {

        final Map<Integer, IntervalsList> daysMap = new HashMap<>();
        // Loop over each rule
        for (SpotRule rule : rules) {
            final SpotRuleAgenda agenda = rule.getAgenda();
            // Loop over days, for current rule
            for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                List<List<Float>> state = agenda.getDay(dayOfWeek);

                IntervalsList restrList = daysMap.get(i);
                if (restrList == null) {
                    restrList = new IntervalsList();
                    daysMap.put(i, restrList);
                }

                int type;
                if (rule.isTypeTimeMaxPaid()) {
                    type = Const.ParkingRestrType.TIME_MAX_PAID;
                } else if (rule.isTypeTimeMax()) {
                    type = Const.ParkingRestrType.TIME_MAX;
                } else if (rule.isTypePaid()) {
                    type = Const.ParkingRestrType.PAID;
                } else {
                    type = Const.ParkingRestrType.ALL_TIMES;
                }
                // Loop over periods, for current rule and dayOfWeek
                for (List<Float> restriction : state) {
                    restrList.add(
                            new RestrInterval(dayOfWeek,
                                    restriction.get(INDEX_START),
                                    restriction.get(INDEX_END),
                                    type)
                    );
                }
            }
        }

        return daysMap;
    }

    /**
     * Merge similar restriction periods (for each day) into a single large period.
     * Also adds a 24hrs no-restriction for empty days.
     *
     * @param daysMap
     * @return
     */
    @Deprecated
    private static List<RestrInterval> mergeSimilarDailyRestr(Map<Integer, List<RestrInterval>> daysMap, int today) {

        final List<RestrInterval> mergedDaysMap = new ArrayList<>();
        // Loop over the week days. Using index (1-7) to get the HashMap in ascending days order
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            List<RestrInterval> restrList = daysMap.get(i);
            if (restrList.isEmpty()) {
                // Add a 24hrs no-restriction rule
                restrList.add(new RestrInterval(
                        CalendarUtils.getIsoDayOfWeekLooped(i, today),
                        Const.ParkingRestrType.NONE));
            } else {
                Collections.sort(restrList);

                List<RestrInterval> restrListMerged = new ArrayList<>();
                // Loop over the day's restrictions, to merge similar intersecting one
//                while (!restrList.isEmpty()) {
//                    Log.v(TAG, "remove");
//                    ParkingRestrPeriod restrCurrent = restrList.remove(0);
//                    Log.v(TAG, "loop");
//                    for (ParkingRestrPeriod restr : restrList) {
//                        Log.v(TAG, "aa ");
//                        if (restrCurrent.abuts(restr)) {
//                            Log.v(TAG, "abuts ");
//
//                        }
//                    }
//                }
            }


            // Add the day's clean list to the week's merged array
            mergedDaysMap.addAll(restrList);
        }

        return mergedDaysMap;
    }

    private static List<RestrInterval> getIntervalsList(Map<Integer, IntervalsList> daysMap, int today) {

        final List<RestrInterval> mergedList = new ArrayList<>();
        // Loop over the week days. Using index (1-7) to get the HashMap in ascending days order
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            List<RestrInterval> restrList = daysMap.get(i);
            if (restrList.isEmpty()) {
                // Add a 24hrs no-restriction rule
                restrList.add(new RestrInterval(
                        CalendarUtils.getIsoDayOfWeekLooped(i, today),
                        Const.ParkingRestrType.NONE));
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
//        getParkingAgenda();
        return "SpotRules{ size : " + getSize() +
                ", codes : " + getAllCodes() +
                " }";
    }
}
