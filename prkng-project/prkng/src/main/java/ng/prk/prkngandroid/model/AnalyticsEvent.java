package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

public class AnalyticsEvent {
    @NonNull
    private String event;
    private float latitude;
    private float longitude;

    public AnalyticsEvent(@NonNull String event) {
        this.event = event;
    }

    public AnalyticsEvent(@NonNull String event, float latitude, float longitude) {
        this.event = event;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
