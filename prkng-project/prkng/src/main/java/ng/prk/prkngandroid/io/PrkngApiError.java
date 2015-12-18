package ng.prk.prkngandroid.io;

import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.View;

import ng.prk.prkngandroid.R;

public class PrkngApiError extends RuntimeException {
    private final static String TAG = "PrkngApiError";

    private int code;
    private String message;

    public PrkngApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void showSnackbar(View v) {
        try {
            final Resources res = v.getResources();
            String msg;
            if (message != null && !message.isEmpty()) {
                msg = String.format(res.getString(R.string.snackbar_api_error_message),
                        message.toLowerCase(),
                        code);
            } else {
                msg = String.format(res.getString(R.string.snackbar_api_error_code),
                        code);
            }
            Snackbar.make(v,
                    msg,
                    Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
