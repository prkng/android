package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotAgenda {
    @SerializedName(Const.ApiValues.AGENDA_DAY_MONDAY)
    private List<LotAgendaDay> monday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_TUESDAY)
    private List<LotAgendaDay> tuesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_WEDNESDAY)
    private List<LotAgendaDay> wednesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_THURSDAY)
    private List<LotAgendaDay> thursday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_FRIDAY)
    private List<LotAgendaDay> friday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SATURDAY)
    private List<LotAgendaDay> saturday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SUNDAY)
    private List<LotAgendaDay> sunday;

    public List<LotAgendaDay> getMonday() {
        return monday;
    }

    public List<LotAgendaDay> getTuesday() {
        return tuesday;
    }

    public List<LotAgendaDay> getWednesday() {
        return wednesday;
    }

    public List<LotAgendaDay> getThursday() {
        return thursday;
    }

    public List<LotAgendaDay> getFriday() {
        return friday;
    }

    public List<LotAgendaDay> getSaturday() {
        return saturday;
    }

    public List<LotAgendaDay> getSunday() {
        return sunday;
    }

    /**
     * Get the agenda of the the selected day
     *
     * @param day 1-7, starting on Monday
     * @return
     */
    public List<LotAgendaDay> getDay(int day) {
        switch (day) {
            case CalendarUtils.MONDAY:
                return getMonday();
            case CalendarUtils.TUESDAY:
                return getTuesday();
            case CalendarUtils.WEDNESDAY:
                return getWednesday();
            case CalendarUtils.THURSDAY:
                return getThursday();
            case CalendarUtils.FRIDAY:
                return getFriday();
            case CalendarUtils.SATURDAY:
                return getSaturday();
            case CalendarUtils.SUNDAY:
                return getSunday();
        }

        throw new ArrayIndexOutOfBoundsException();
    }

    public RestrIntervalsList getWeekAgenda() {
        final int today = CalendarUtils.getIsoDayOfWeek();

        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(i, today);
            List<LotAgendaDay> dailyAgenda = getDay(dayOfWeek);
            if (dailyAgenda == null || dailyAgenda.isEmpty()) {
                // is closed
            } else {

            }
        }

        return null;
    }
}
