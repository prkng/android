package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

public class SpotRuleAgenda {

    @SerializedName(Const.ApiValues.AGENDA_DAY_MONDAY)
    private List<List<Float>> monday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_TUESDAY)
    private List<List<Float>> tuesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_WEDNESDAY)
    private List<List<Float>> wednesday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_THURSDAY)
    private List<List<Float>> thursday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_FRIDAY)
    private List<List<Float>> friday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SATURDAY)
    private List<List<Float>> saturday;
    @SerializedName(Const.ApiValues.AGENDA_DAY_SUNDAY)
    private List<List<Float>> sunday;

    public List<List<Float>> getMonday() {
        return monday;
    }

    public List<List<Float>> getTuesday() {
        return tuesday;
    }

    public List<List<Float>> getWednesday() {
        return wednesday;
    }

    public List<List<Float>> getThursday() {
        return thursday;
    }

    public List<List<Float>> getFriday() {
        return friday;
    }

    public List<List<Float>> getSaturday() {
        return saturday;
    }

    public List<List<Float>> getSunday() {
        return sunday;
    }

    /**
     * Get the agenda of the the selected day
     *
     * @param day 1-7, starting on Monday
     * @return
     */
    public List<List<Float>> getDay(int day) {
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
