package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

@Deprecated
public class AnalyticsQuery {
    @NonNull
    private String query;

    public AnalyticsQuery(String query) {
        this.query = query;
    }
}
