package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotAgenda {
    private final static String TAG = "LotAgenda";


    private LotAgendaRaw agenda;

    public LotAgenda(@NonNull LotAgendaRaw agenda) {
        this.agenda = agenda;
    }

    public RestrIntervalsList getParkingSpotAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

        final Map<Integer, RestrIntervalsList> dailyIntervals = getDailyIntervals(agenda, today);

        return getWeekIntervals(dailyIntervals, today);
    }

    private static Map<Integer, RestrIntervalsList> getDailyIntervals(LotAgendaRaw agendaRaw, int today) {

        final Map<Integer, RestrIntervalsList> daysMap = new HashMap<>();

        // Initialize the days' arrays
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            daysMap.put(i, new RestrIntervalsList());
        }

        // Loop over days, for different states
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
            List<LotAgendaDay> dailyAgenda = agendaRaw.getDay(dayOfWeek);

            final RestrIntervalsList restrList = daysMap.get(i);

            for (LotAgendaDay agendaDay : dailyAgenda) {
                if (agendaDay.getType() != Const.LotAgendaType.CLOSED) {
                    // Add-Merge the day's interval, comparing it to other intervals of the same day
                    restrList.addMerge(new RestrInterval.Builder(dayOfWeek)
                            .startHour(agendaDay.getStartHour())
                            .endHour(agendaDay.getEndHour())
                            .type(agendaDay.getType())
                            .build());
                }
            }
        }

        return daysMap;
    }

    private static RestrIntervalsList getWeekIntervals(Map<Integer, RestrIntervalsList> dailyIntervals, int today) {

        final RestrIntervalsList mergedList = new RestrIntervalsList();
        // Loop over the week days. Using index (1-7) to get the HashMap in ascending days order
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            RestrIntervalsList restrList = dailyIntervals.get(i);
            if (restrList.isEmpty()) {
                // Add a closed state for 24hrs
                final int day = CalendarUtils.getIsoDayOfWeekLooped(i, today);
                restrList.add(new RestrInterval.Builder(day)
                        .type(Const.LotAgendaType.CLOSED)
                        .allDay()
                        .build());
            }

            Collections.sort(restrList);

            // Add the day's clean list to the week's merged array
            mergedList.addAll(restrList);
        }

        return mergedList;
    }

//    private static RestrIntervalsList getWeekAgenda(LotAgendaRaw agendaRaw) {
//        final int today = CalendarUtils.getIsoDayOfWeek();
//
//        final RestrIntervalsList intervals = new RestrIntervalsList();
//        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
//            final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
//            List<LotAgendaDay> dailyAgenda = agendaRaw.getDay(dayOfWeek);
//            if (dailyAgenda == null || dailyAgenda.isEmpty()) {
//                // is closed
//                intervals.add(new RestrInterval.Builder(dayOfWeek)
//                        .type(Const.ParkingRestrType.ALL_TIMES)
//                        .allDay()
//                        .build());
//            } else {
//                // add and sort the day's intervals, before adding to the week's list
//                final RestrIntervalsList dailyIntervals = new RestrIntervalsList();
//
//                for (LotAgendaDay agendaDay : dailyAgenda) {
//                    dailyIntervals.add(new RestrInterval.Builder(dayOfWeek)
//                            .type(Const.ParkingRestrType.PAID)
//                            .startHour(agendaDay.getStartHour())
//                            .endHour(agendaDay.getEndHour())
//                            .build());
//                }
//                Collections.sort(dailyIntervals);
//                intervals.addAll(dailyIntervals);
//            }
//        }
//
//        return intervals;
//    }
}
