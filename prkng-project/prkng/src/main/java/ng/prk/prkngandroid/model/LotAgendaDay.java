package ng.prk.prkngandroid.model;

import java.util.List;

import ng.prk.prkngandroid.Const;

public class LotAgendaDay {
    private float daily;
    private float hourly;
    private List<Float> hours;
    private Integer max;

    public float getDaily() {
        return daily;
    }

    public float getHourly() {
        return hourly;
    }

    public List<Float> getHours() {
        return hours;
    }

    public int getMax() {
        return max == null ? Const.UNKNOWN_VALUE : max;
    }

    @Override
    public String toString() {
        return "LotAgendaDay{" +
                "daily=$" + daily +
                ", hourly=$" + hourly +
                ", max=" + max +
                '}';
    }
}
