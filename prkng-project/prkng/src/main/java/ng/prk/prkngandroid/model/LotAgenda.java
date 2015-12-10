package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.io.ApiSimulator;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.Interval;

public class LotAgenda {
    private final static String TAG = "LotAgenda";


    private LotAgendaRaw agenda;
    private BusinessIntervalList businessIntervals;

    public LotAgenda(@NonNull LotAgendaRaw agenda) {
        this.agenda = agenda;
    }

    private static Map<Integer, BusinessIntervalList> getDailyIntervals(LotAgendaRaw agendaRaw, int today) {
Log.v(TAG, "getDailyIntervals");

        final Map<Integer, BusinessIntervalList> daysMap = new HashMap<>();

        // Loop over days, for different states
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
            List<LotAgendaPeriod> dailyAgenda = agendaRaw.getDay(dayOfWeek);

            final BusinessIntervalList businessIntervals = new BusinessIntervalList();

            for (LotAgendaPeriod period : dailyAgenda) {
                businessIntervals.add(new BusinessInterval(dayOfWeek, period));
            }

            Collections.sort(businessIntervals);
            daysMap.put(i, businessIntervals);
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
                        .type(Const.BusinnessHourType.CLOSED)
                        .allDay()
                        .build());
            }

            Collections.sort(restrList);

            // Add the day's clean list to the week's merged array
            mergedList.addAll(restrList);
        }

        return mergedList;
    }

    private void buildBusinessIntervalsIfNecessary(int today) {
        if (businessIntervals == null) {
//            final Map<Integer, BusinessIntervalList> dailyIntervals = getDailyIntervals(agenda, today);
        final Map<Integer, BusinessIntervalList> dailyIntervals = ApiSimulator.getBusinessDays();

            // Free resources
            agenda = null;

            // Convert Map to ArrayList, maintaining order
            businessIntervals = new BusinessIntervalList();
            for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
                businessIntervals.addAll(dailyIntervals.get(i));
            }
        }
    }

    /**
     *
     * @return
     */
    public BusinessIntervalList getParkingSpotAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

        buildBusinessIntervalsIfNecessary(today);

        return businessIntervals.getMergedItems();
    }


    private LotCurrentStatus getFreeLotStatus(Interval current, int indexNext, long now) {
        Log.v(TAG, "getFreeLotStatus "
                + String.format("current = %s, indexNext = %s, now = %s", current, indexNext, now));

        if (indexNext >= businessIntervals.size()) {
            return null;
        }
        final Interval next = businessIntervals.get(indexNext);

        return null;
    }

    /**
     *
     * @param now
     * @return
     */
    public LotCurrentStatus getLotCurrentStatus(long now) {
        final int today = CalendarUtils.getIsoDayOfWeek();

        buildBusinessIntervalsIfNecessary(today);

        int index = 0;
        for (BusinessInterval interval : businessIntervals) {
            Log.v(TAG, interval.toString());
            if (interval.contains(now)) {
                switch (interval.getType()) {
                    case Const.BusinnessHourType.CLOSED:
                        return null;
                    case Const.BusinnessHourType.FREE:
                        return getFreeLotStatus(
                                interval,
                                1 + index,
                                now);
                    case Const.BusinnessHourType.OPEN:
                        return new LotCurrentStatus(
                                interval.getMainPrice(),
                                interval.getHourlyPrice(),
                                interval.getEndMillis() - now
                        );
                }
            }
            index++;
        }

        return null;
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
