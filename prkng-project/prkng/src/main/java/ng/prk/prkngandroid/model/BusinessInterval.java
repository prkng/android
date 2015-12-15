package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;
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
     * Check if the first interval ends at the same midnight where the second interval starts.
     * This is regardless of type
     *
     * @param firstInterval  The interval to examine
     * @param secondInterval The interval to examine
     * @return true if the both intervals abut at midnight, following specific order
     */
    private static boolean abutsOvernight(BusinessInterval firstInterval, BusinessInterval secondInterval) {
        return (Float.compare(firstInterval.getEndMillis(), DateUtils.DAY_IN_MILLIS) == 0)
                && (Float.compare(secondInterval.getStartMillis(), 0) == 0);
    }

    /**
     * Join with another interval.
     * The result interval keeps the type/pricing of the current one.
     * Future UI changes may need a more strict join policy. The current UI version of the agenda
     * displays business hours only.
     *
     * @param another Interval to join with, must abut or overlap
     */
    @Deprecated
    public void join(BusinessInterval another) {
        // TODO Define price/type join policy
        super.join(another);
    }

    public void joinOvernight(BusinessInterval another) {
        if (abutsOvernight(this, another)) {
            this.endMillis = another.getEndMillis() + DateUtils.DAY_IN_MILLIS;
        } else if (abutsOvernight(another, this)) {
            this.dayOfWeek = another.dayOfWeek;
            this.startMillis = another.getStartMillis();
            this.endMillis += DateUtils.DAY_IN_MILLIS;
            this.mainPrice = another.mainPrice;
            this.hourlyPrice = another.hourlyPrice;
        }
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
        if (Float.valueOf(mainPrice).compareTo((float) Const.UNKNOWN_VALUE) != 0) {
            return mainPrice;
        } else if ((Float.valueOf(hourlyPrice).compareTo(0f) > 0)) {
            Log.i("BusinessInterval", "main price based on hourly");
            final double remainingHours = Math.ceil(
                    (endMillis - CalendarUtils.todayMillis()) / DateUtils.HOUR_IN_MILLIS);

            return (float) (hourlyPrice * remainingHours);
        } else {
            return 0;
        }
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

    public boolean isMergeableType(BusinessInterval another) {
        return isSameType(another) || (!isClosed() && !another.isClosed());
    }

    public boolean isSameDay(BusinessInterval another) {
        return this.dayOfWeek == another.getDayOfWeek();
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Check if the interval abuts the current at midnight. Also handles week-loop.
     * This is regardless of type or order.
     *
     * @param another The interval to examine
     * @return true if the other interval abuts at previous/following midnight
     */
    public boolean abutsOvernight(@NonNull BusinessInterval another) {

        if (CalendarUtils.areConsecutiveDaysOfWeekLooped(this.dayOfWeek, another.getDayOfWeek())) {
            return abutsOvernight(this, another);
        } else if (CalendarUtils.areConsecutiveDaysOfWeekLooped(another.getDayOfWeek(), this.dayOfWeek)) {
            return abutsOvernight(another, this);
        }

        return false;
    }

    public boolean isClosed() {
        return type == CLOSED;
    }

    public boolean isFree() {
        return type == FREE;
    }

    public boolean isNaturalMerge(BusinessInterval another) {
        if (another == null || another.isAllDay()) {
            return false;
        }

        return isSameDay(another)
                && isMergeableType(another)
                && abuts(another);
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
