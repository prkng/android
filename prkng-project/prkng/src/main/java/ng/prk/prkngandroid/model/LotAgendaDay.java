package ng.prk.prkngandroid.model;

import java.util.List;

import ng.prk.prkngandroid.Const;

public class LotAgendaDay {
    private final static int INDEX_START = 0;
    private final static int INDEX_END = 1;

    private Float daily;
    private Float hourly;
    private Float max;
    private List<Float> hours;

    public float getMainPrice(int today, long now) {
        if (max != null) {
            return max;
        } else if (daily != null) {
            return daily;
        } else if (hourly != null) {
            // TODO return hourly price x remaining time of current period
            return 123.45f;
        }

        // Free
        return 0f;
    }

    public float getDaily() {
        return daily == null ? Const.UNKNOWN_VALUE : daily;
    }

    public float getHourly() {
        return hourly == null ? Const.UNKNOWN_VALUE : hourly;
    }

    public float getMax() {
        return max == null ? Const.UNKNOWN_VALUE : max;
    }

    public float getStartHour() {
        try {
            return hours.get(INDEX_START);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return Const.UNKNOWN_VALUE;
    }

    public float getEndHour() {
        try {
            return hours.get(INDEX_END);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return Const.UNKNOWN_VALUE;
    }

    public boolean isClosed() {
        return (daily == null) && (hourly == null) && (max == null);
    }

    public boolean isFree() {
        return (hourly != null) && (hourly.compareTo(0f) == 0);
    }

    public int getType() {
        if (isClosed()) {
            return Const.LotAgendaType.CLOSED;
//        } else if (isFree()) {
//            return Const.LotAgendaType.FREE;
        }

        return Const.LotAgendaType.OPEN;
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
