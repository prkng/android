package ng.prk.prkngandroid.model;

import android.text.format.DateUtils;

import org.joda.time.Interval;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.CalendarUtils;

public class RestrInterval implements Comparable<RestrInterval> {
    private final static int LESS_THAN = -1;
    private final static int GREATER_THAN = 1;
    private final static int EQUALS = 0;

    private int dayOfWeek;
    private Interval interval;
    private int minuteOfDayStart;
    private int minuteOfDayEnd;
    private int type;
    private boolean isAllDay;

    public RestrInterval(int dayOfWeek, float hourStart, float hourEnd, int type) {
        this.dayOfWeek = dayOfWeek;
        this.minuteOfDayStart = (int) ((hourStart - 0) * CalendarUtils.HOUR_IN_MINUTES);
        this.minuteOfDayEnd = (int) ((hourEnd + 0) * CalendarUtils.HOUR_IN_MINUTES);
        this.type = type;
        this.isAllDay = (hourStart == 0) && (hourEnd == 24);
        this.interval = new Interval(minuteOfDayStart * DateUtils.MINUTE_IN_MILLIS, minuteOfDayEnd * DateUtils.MINUTE_IN_MILLIS);
    }

    public RestrInterval(int dayOfWeek, int type) {
        this.dayOfWeek = dayOfWeek;
        this.minuteOfDayStart = Const.UNKNOWN_VALUE;
        this.minuteOfDayEnd = Const.UNKNOWN_VALUE;
        this.type = type;
        this.isAllDay = true;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getMinuteStart() {
        return minuteOfDayStart;
    }

    public int getMinuteEnd() {
        return minuteOfDayEnd;
    }

    public int getType() {
        return type;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public Interval getInterval() {
        return interval;
    }

    public boolean abuts(RestrInterval another) {
//        Log.v("RestrInterval", interval.toString() + " vs " + another.getInterval().toString());
        return interval.abuts(another.getInterval());
    }

    public boolean overlaps(RestrInterval another) {
//        Log.v("RestrInterval", interval.toString() + " vs " + another.getInterval().toString());
        return interval.overlaps(another.getInterval());
    }

    public void join(RestrInterval another) {
        minuteOfDayStart = Math.min(minuteOfDayStart, another.getMinuteStart());
        minuteOfDayEnd = Math.max(minuteOfDayEnd, another.getMinuteEnd());
    }

    @Override
    public String toString() {
        return "RestrInterval{" +
                "type=" + type +
                ", hourStart=" + String.format("%.2f", (float) minuteOfDayStart / CalendarUtils.HOUR_IN_MINUTES) +
                ", hourEnd=" + String.format("%.2f", (float) minuteOfDayEnd / CalendarUtils.HOUR_IN_MINUTES) +
                '}';
    }

    @Override
    public int compareTo(RestrInterval another) {
        return interval.getStart().compareTo(another.getInterval().getStart());
//        if (interval.isBefore(another.getInterval())) {
//            return LESS_THAN;
//        } else if (interval.isEqual(another.getInterval())) {
//            return EQUALS;
//        } else {
//            return GREATER_THAN;
//        }

//
//        if (getMinuteStart() < another.getMinuteStart()) {
//            // Starts before another
//            return LESS_THAN;
//        } else if ((getMinuteStart() == another.getMinuteStart()) && (getMinuteEnd() < another.getMinuteEnd())) {
//            // Both start at the same time, ends before another
//            return LESS_THAN;
//        } else if ((getMinuteStart() == another.getMinuteStart()) && (getMinuteEnd() == another.getMinuteEnd())) {
//            // Same start and end, so we sort by type
//            if (getType() < another.getType()) {
//                // Same start and end, type is less
//                return LESS_THAN;
//            } else if (getType() > another.getType()) {
//                // Same start and end, type is greater
//                return GREATER_THAN;
//            } else {
//                // Same start, end and type
//                return EQUALS;
//            }
//        } else {
//            // Not any of the above, so it's greater (later) than other
//            return GREATER_THAN;
//        }
    }

}
