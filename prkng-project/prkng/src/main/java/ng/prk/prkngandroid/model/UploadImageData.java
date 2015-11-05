package ng.prk.prkngandroid.model;

import android.support.annotation.NonNull;

public class UploadImageData {
    @NonNull
    private String image_type;
    @NonNull
    private String file_name;

    public UploadImageData(String image_type, String file_name) {
        this.image_type = image_type;
        this.file_name = file_name;
    }
}
