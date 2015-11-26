package ng.prk.prkngandroid.model;


import android.util.Log;

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

    public void getParkingAgenda() {
        int today = CalendarUtils.getIsoDayOfWeek();

        final Map<Integer, List<ParkingPeriod>> weekStates = new HashMap<>();
        for (SpotRule rule : rules) {
            final SpotRuleAgenda agenda = rule.getAgenda();
            for (int i = CalendarUtils.FIRST_WEEK_IN_DAY; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                List<List<Float>> state = agenda.getDay(CalendarUtils.getIsoDayOfWeekLooped(i, today));

                List<ParkingPeriod> dayStates = weekStates.get(i);
                if (dayStates == null) {
                    dayStates = new ArrayList<>();
                    weekStates.put(i, dayStates);
                }

                if (state == null || state.isEmpty() || state.get(0) == null) {
                    continue;
                }

                if (rule.isTypeTimeMaxPaid()) {
                    dayStates.add(new ParkingPeriod(
                                    state.get(0).get(0),
                                    state.get(0).get(1),
                                    Const.ParkingRestrictionType.TIME_MAX_PAID)
                    );
                } else if (rule.isTypeTimeMax()) {
                    dayStates.add(new ParkingPeriod(
                                    state.get(0).get(0),
                                    state.get(0).get(1),
                                    Const.ParkingRestrictionType.TIME_MAX)
                    );
                } else if (rule.isTypePaid()) {
                    dayStates.add(new ParkingPeriod(
                                    state.get(0).get(0),
                                    state.get(0).get(1),
                                    Const.ParkingRestrictionType.PAID)
                    );
                } else {
                    dayStates.add(new ParkingPeriod(
                                    state.get(0).get(0),
                                    state.get(0).get(1),
                                    Const.ParkingRestrictionType.FORBIDDEN)
                    );
                }
            }
        }

        for (int i = CalendarUtils.FIRST_WEEK_IN_DAY; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            List<ParkingPeriod> dayStates = weekStates.get(i);
            Collections.sort(dayStates);

            for (ParkingPeriod state : dayStates) {
                Log.v(TAG, state.toString());
            }
        }
    }

    @Override
    public String toString() {
        getParkingAgenda();
        return "SpotRules{ size : " + getSize() +
                ", codes : " + getAllCodes() +
                " }";
    }

    public static class ParkingPeriod implements Comparable<ParkingPeriod> {
        int minuteStart;
        int minuteEnd;
        int type;

        public ParkingPeriod(float hourStart, float hourEnd, int type) {
            this.minuteStart = (int) (hourStart * CalendarUtils.HOUR_IN_MINUTES);
            this.minuteEnd = (int) (hourEnd * CalendarUtils.HOUR_IN_MINUTES);
            this.type = type;
        }

        public float getMinuteStart() {
            return minuteStart;
        }

        public float getMinuteEnd() {
            return minuteEnd;
        }

        public int getType() {
            return type;
        }

        @Override
        public String toString() {
            return "ParkingPeriod{" +
                    "type=" + type +
                    ", minuteStart=" + String.format("%.2f", (float) (minuteStart / CalendarUtils.HOUR_IN_MINUTES)) +
                    ", minuteEnd=" + String.format("%.2f", (float) (minuteEnd / CalendarUtils.HOUR_IN_MINUTES)) +
                    '}';
        }

        @Override
        public int compareTo(ParkingPeriod another) {
            if (getMinuteStart() < another.getMinuteStart()) {
                return -1;
            } else if (getMinuteEnd() > another.getMinuteEnd()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
