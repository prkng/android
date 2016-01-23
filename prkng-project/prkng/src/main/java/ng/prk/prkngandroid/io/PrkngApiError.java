package ng.prk.prkngandroid.io;

import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.view.RedSnackbar;

public class PrkngApiError extends IOException {
    private final static String TAG = "PrkngApiError";
    private final static int HOST_NOT_FOUND = 1404;

    private int code;
    private String message;

    public static PrkngApiError getInstance(IOException e) {
        if (e instanceof UnknownHostException) {
            return new PrkngApiError(HOST_NOT_FOUND, null);
        } else if (e instanceof PrkngApiError) {
            return (PrkngApiError) e;
        } else {
            return new PrkngApiError(Const.UNKNOWN_VALUE, e.getMessage());
        }
    }

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
                if (isHostNotFound()) {
                    msg = String.format(res.getString(R.string.snackbar_host_unknown_error_message),
                            code);
                } else {
                    msg = String.format(res.getString(R.string.snackbar_api_error_code),
                            code);
                }
            }
            RedSnackbar.make(v,
                    msg,
                    Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUnauthorized() {
        return this.code == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

    public boolean isNotFound() {
        return this.code == HttpURLConnection.HTTP_NOT_FOUND;
    }

    public boolean isConflict() {
        return this.code == HttpURLConnection.HTTP_CONFLICT;
    }

    public boolean isHostNotFound() {
        return this.code == HOST_NOT_FOUND;
    }
}
