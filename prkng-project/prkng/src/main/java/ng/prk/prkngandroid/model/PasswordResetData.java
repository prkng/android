package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

@Deprecated
public class PasswordResetData {
    @NonNull
    private String email;

    public PasswordResetData(@NonNull String email) {
        this.email = email;
    }
}
