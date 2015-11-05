package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

public class UserRegisterData extends UserProfileData {
    @NonNull
    protected String name;
    @NonNull
    protected String email;
    @NonNull
    protected String password;

    public UserRegisterData(@NonNull String name, @NonNull String email, @NonNull String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
