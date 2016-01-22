package ng.prk.prkngandroid.model.ui;

import android.content.Context;

import ng.prk.prkngandroid.Const;

public class HumanDuration implements
        Const.MapSections {
    private final static String TAG = "HumanDuration";

    private Context context;
    private long millis;
    private String prefix;
    private String duration;

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
        return prefix;
    }

    public String getDuration() {
        return duration;
    }

    private void initializeLotDuration() {

    }

    private void initializeSpotDuration() {

    }

}
