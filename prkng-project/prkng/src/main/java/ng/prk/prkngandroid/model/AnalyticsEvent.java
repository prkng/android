package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

@Deprecated
public class AnalyticsEvent {
    @NonNull
    private String event;
    private double latitude;
    private double longitude;

    public AnalyticsEvent(@NonNull String event) {
        this.event = event;
    }

    public AnalyticsEvent(@NonNull String event, double latitude, double longitude) {
        this.event = event;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
