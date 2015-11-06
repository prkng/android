package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

@Deprecated
public class ErrorReportData {
    @NonNull
    private String city;
    @NonNull
    private String latitude;
    @NonNull
    private String longitude;
    @NonNull
    private String image_url;
    private String notes;

    public ErrorReportData(@NonNull String city, @NonNull String latitude, @NonNull String longitude, @NonNull String image_url, String notes) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image_url = image_url;
        this.notes = notes;
    }
}
