package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

@Deprecated
public class CheckInData {
    @NonNull
    private float slot_id;
    private String city;

    public CheckInData(@NonNull float slot_id) {
        this.slot_id = slot_id;
    }

    public CheckInData(@NonNull float slot_id, String city) {
        this.slot_id = slot_id;
        this.city = city;
    }
}
