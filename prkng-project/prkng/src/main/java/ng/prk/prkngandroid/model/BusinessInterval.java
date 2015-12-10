package ng.prk.prkngandroid.model;

import android.text.format.DateUtils;

import java.util.concurrent.TimeUnit;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.Interval;

public class BusinessInterval extends Interval implements
        Const.BusinnessHourType {

    private int type;
    private int dayOfWeek;
    private float mainPrice;
    private float hourlyPrice;

    public BusinessInterval(int day, LotAgendaPeriod period) {
        super(period.getStartMillis(), period.getEndMillis());

        this.type = period.getType();
        this.dayOfWeek = day;
        this.mainPrice = period.getMainPrice();
        this.hourlyPrice = period.getHourly();
    }

    /**
     * Private constructor to be used with Builder
     *
     * @param builder the RestrInterval.Builder
     */
    private BusinessInterval(Builder builder) {
        super(builder.startMillis, builder.endMillis);

        this.dayOfWeek = builder.dayOfWeek;
        this.type = builder.type;
    }

    /**
     * Check if restriction applies all day (24 hours)
     *
     * @return true for all-day restriction
     */
    public boolean isAllDay() {
        return endMillis - startMillis >= DateUtils.DAY_IN_MILLIS;
    }

    public float getHourlyPrice() {
        return hourlyPrice;
    }

    public float getMainPrice() {
        return mainPrice;
    }

    public float getMainPrice(int today, long now) {
//        if (max != null) {
//            return max;
//        } else if (daily != null) {
//            return daily;
//        } else if (hourly != null) {
//            // TODO return hourly price x remaining time of current period
//            return 123.45f;
//        }

        // Free
        return 0f;
    }

    public int getType() {
        return type;
    }

    public boolean isSameType(BusinessInterval another) {
        return this.type == another.getType();
    }

    public boolean isSameDay(BusinessInterval another) {
        return this.dayOfWeek == another.getDayOfWeek();
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isClosed() {
        return type == CLOSED;
    }

    @Override
    public String toString() {
        return "BusinessInterval{" +
                "type=" + type +
                " hourStart=" + TimeUnit.MILLISECONDS.toHours(startMillis) +
                ", hourEnd=" + TimeUnit.MILLISECONDS.toHours(endMillis) +
                ", dayOfWeek=" + dayOfWeek +
                (isClosed() ? "" : ", mainPrice=" + mainPrice) +
                (isClosed() ? "" : ", hourlyPrice=" + hourlyPrice) +
                '}';
    }

    /**
     * Builder class
     */
    public static class Builder {
        private long startMillis;
        private long endMillis;
        private int type;
        private int dayOfWeek;
        private float mainPrice;
        private float hourlyPrice;

        public Builder(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            this.type = FREE;
            this.startMillis = Const.UNKNOWN_VALUE;
            this.endMillis = Const.UNKNOWN_VALUE;
            this.mainPrice = 0f;
            this.hourlyPrice = 0;
        }

        public Builder period(LotAgendaPeriod period) {
            // TODO complete builder
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder startMillis(long millis) {
            this.startMillis = millis;
            return this;
        }

        public Builder endMillis(long millis) {
            this.endMillis = millis;
            return this;
        }

        public Builder startHour(float hour) {
            this.startMillis = (long) (hour * DateUtils.HOUR_IN_MILLIS);
            return this;
        }

        public Builder endHour(float hour) {
            this.endMillis = (long) (hour * DateUtils.HOUR_IN_MILLIS);
            return this;
        }

        public Builder interval(Interval interval) {
            this.startMillis = interval.getStartMillis();
            this.endMillis = interval.getEndMillis();
            return this;
        }

        public Builder allDay() {
            this.startMillis = 0;
            this.endMillis = DateUtils.DAY_IN_MILLIS;
            return this;
        }

        public BusinessInterval build() {
            return new BusinessInterval(this);
        }
    }
}
