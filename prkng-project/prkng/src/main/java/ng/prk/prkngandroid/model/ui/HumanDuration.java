package ng.prk.prkngandroid.model.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.util.CalendarUtils;

public class HumanDuration implements
        Const.MapSections,
        Const.ParkingRestrType,
        Const.BusinnessHourType {
    private final static String TAG = "HumanDuration";

    private Context context;
    private long millis;
    private String onStreetPrefix;
    private String offStreetPrefix;
    private String expiry;
    private int type;
    private int subType; // refers to Const.ParkingRestrType or BusinnessHourType
    private boolean isFullDateTime;

    private HumanDuration(Builder builder) {
        this.context = builder.context;
        this.millis = builder.millis;
        this.type = builder.type;
        this.subType = builder.subType;
        this.isFullDateTime = builder.isFullDateTime;

        initialize();
    }

    private boolean isSpotForbidden() {
        return false;
    }

    private boolean isLotClosed() {
        return false;
    }

    private void initialize() {
        if (type == OFF_STREET) {
            initializeLotDuration();
        } else {
            initializeSpotDuration();
        }
    }

    public HumanDuration() {
        this.offStreetPrefix = "";
        this.onStreetPrefix = "";
        this.expiry = "";
    }

    public HumanDuration(Context context, long millis, int type) {
        this.context = context;
        this.millis = millis;

        if (type == OFF_STREET) {
            initializeLotDuration();
        } else {
            initializeSpotDuration();
        }
    }

    public String getPrefix() {
        return (type == OFF_STREET) ? offStreetPrefix : onStreetPrefix;
    }

    public String getExpiry() {
        return expiry;
    }

    private void computeDuration() {
        final Resources res = context.getResources();

        final Calendar now = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        end.setTimeInMillis(end.getTimeInMillis() + millis);

        final int dayDiff = end.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
        final int minuteDiff = (int) Math.ceil(
                (end.getTimeInMillis() - now.getTimeInMillis()) / DateUtils.MINUTE_IN_MILLIS
        );

        String day;
        String timeOfDay;

        if (!isFullDateTime && dayDiff == 0) {
            day = null;
        } else if (!isFullDateTime && dayDiff == 1) {
            day = res.getString(R.string.expiry_day_tomorrow);
        } else {
            final SimpleDateFormat dayFormat = new SimpleDateFormat(
                    CalendarUtils.DATE_FORMAT_DAY_OF_WEEK, Locale.getDefault());
            day = dayFormat.format(end.getTime());
        }


        if (!isFullDateTime && (dayDiff == 0) && (minuteDiff <= (2 * CalendarUtils.HOUR_IN_MINUTES))) {
            this.offStreetPrefix = context.getString(R.string.duration_prefix_for);
            this.onStreetPrefix = context.getString(isPaidSpot() ?
                    R.string.duration_prefix_paid_for : R.string.duration_prefix_for);
            // Ends today in 2hrs or less
            final int hours = minuteDiff / CalendarUtils.HOUR_IN_MINUTES;
            final int minutes = minuteDiff % CalendarUtils.HOUR_IN_MINUTES;
            if (hours == 0) {
                timeOfDay = String.format(
                        res.getQuantityString(R.plurals.expiry_minutes, minutes),
                        minutes);
            } else if (minutes == 0) {
                timeOfDay = String.format(
                        res.getQuantityString(R.plurals.expiry_hours, hours),
                        hours);
            } else {
                timeOfDay = String.format(
                        res.getString(R.string.expiry_hours_minutes),
                        hours,
                        minutes);
            }
        } else {
            this.offStreetPrefix = context.getString(R.string.duration_prefix_until);
            this.onStreetPrefix = context.getString(isPaidSpot() ?
                    R.string.duration_prefix_paid_until : R.string.duration_prefix_until);
            timeOfDay = DateFormat
                    .getTimeFormat(context)
                    .format(end.getTime());
        }

        if (day == null) {
            this.expiry = timeOfDay;
        } else {
            this.expiry = String.format(
                    res.getString(R.string.expiry_day_time_of_day),
                    day,
                    timeOfDay);
        }
    }

    private void initializeLotDuration() {
        if (Long.valueOf(millis).compareTo(0L) == 0) {
            this.offStreetPrefix = context.getString(R.string.duration_prefix_closed);
            // TODO
            this.expiry = "";
        } else if (CalendarUtils.isWeekLongDuration(millis)) {
            this.offStreetPrefix = context.getString(R.string.duration_prefix_open);
            this.expiry = context.getString(R.string.open_all_week);
        } else {
            computeDuration();
            this.expiry = offStreetPrefix + expiry;
            this.offStreetPrefix = context.getString(R.string.duration_prefix_open);
        }
    }

    private void initializeSpotDuration() {
        if (Long.valueOf(millis).compareTo(0L) == 0) {
            this.onStreetPrefix = "";
            this.expiry = context.getString(R.string.not_allowed);
        } else if (CalendarUtils.isWeekLongDuration(millis)) {
            this.onStreetPrefix = context.getString(subType == PAID
                    ? R.string.duration_prefix_paid : R.string.duration_prefix_available);
            this.expiry = context.getString(R.string.allowed_all_week_prefixed);
        } else {
            computeDuration();
        }
    }

    private boolean isPaidSpot() {
        return (this.subType == PAID) || (subType == TIME_MAX_PAID);
    }

    @Override
    public String toString() {
        return "HumanDuration{" +
                "onStreetPrefix='" + onStreetPrefix + '\'' +
                ", offStreetPrefix='" + offStreetPrefix + '\'' +
                ", expiry='" + expiry + '\'' +
                ", type=" + type +
                '}';
    }

    public static class Builder {
        private Context context;
        private long millis;
        private int type;
        private int subType;
        private boolean isFullDateTime;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder millis(long millis) {
            this.millis = millis;
            return this;
        }

        public Builder spot() {
            this.type = Const.MapSections.ON_STREET;
            return this;
        }

        public Builder lot() {
            this.type = Const.MapSections.OFF_STREET;
            return this;
        }

        /**
         * @param status
         * @return
         * @see ng.prk.prkngandroid.Const.ParkingRestrType
         * @see ng.prk.prkngandroid.Const.BusinnessHourType
         */
        public Builder status(int status) {
            this.subType = status;
            return this;
        }

        public Builder checkin() {
            this.type = Const.MapSections.ON_STREET;
            this.isFullDateTime = true;
            return this;
        }

        public HumanDuration build() {
            return new HumanDuration(this);
        }
    }
}
