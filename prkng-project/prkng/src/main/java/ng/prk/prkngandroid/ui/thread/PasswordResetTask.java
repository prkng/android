package ng.prk.prkngandroid.ui.thread;

import android.os.AsyncTask;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;

public class PasswordResetTask extends AsyncTask<String, Void, Void> {

    private PrkngApiError error = null;

    @Override
    protected Void doInBackground(String... params) {
        try {
            final String email = params[0];
            if (email != null) {
                ApiClient.resetUserPassword(
                        ApiClient.getServiceLog(),
                        email);
            }
        } catch (PrkngApiError e) {
            error = e;
        }

        return null;
    }
}
