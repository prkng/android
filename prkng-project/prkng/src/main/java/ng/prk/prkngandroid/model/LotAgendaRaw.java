package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotAgendaRaw {
    @SerializedName(Const.ApiValues.AGENDA_DAY_MONDAY)
    private List<LotAgendaPeriod> monday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_TUESDAY)
    private List<LotAgendaPeriod> tuesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_WEDNESDAY)
    private List<LotAgendaPeriod> wednesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_THURSDAY)
    private List<LotAgendaPeriod> thursday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_FRIDAY)
    private List<LotAgendaPeriod> friday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SATURDAY)
    private List<LotAgendaPeriod> saturday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SUNDAY)
    private List<LotAgendaPeriod> sunday;

    public List<LotAgendaPeriod> getMonday() {
        return monday;
    }

    public List<LotAgendaPeriod> getTuesday() {
        return tuesday;
    }

    public List<LotAgendaPeriod> getWednesday() {
        return wednesday;
    }

    public List<LotAgendaPeriod> getThursday() {
        return thursday;
    }

    public List<LotAgendaPeriod> getFriday() {
        return friday;
    }

    public List<LotAgendaPeriod> getSaturday() {
        return saturday;
    }

    public List<LotAgendaPeriod> getSunday() {
        return sunday;
    }

    /**
     * Get the agenda of the the selected day
     *
     * @param day 1-7, starting on Monday
     * @return
     */
    public List<LotAgendaPeriod> getDay(int day) {
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

}
