package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.prk.prkngandroid.Const;
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

    private void buildBusinessIntervalsIfNecessary(int today) {
        if (businessIntervals == null) {
            final Map<Integer, BusinessIntervalList> dailyIntervals = getDailyIntervals(agenda, today);
//        final Map<Integer, BusinessIntervalList> dailyIntervals = ApiSimulator.getBusinessDays();

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
    public BusinessIntervalList getLotAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

        buildBusinessIntervalsIfNecessary(today);

        return businessIntervals.getMergedItems();
    }


    private LotCurrentStatus getFreeLotStatus(Interval current, int indexNext, long now) {
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
}
