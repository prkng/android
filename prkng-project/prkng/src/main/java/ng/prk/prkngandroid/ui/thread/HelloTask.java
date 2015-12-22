package ng.prk.prkngandroid.ui.thread;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Locale;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.util.Installation;

public class HelloTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private PrkngApiError error = null;

    public HelloTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            final String apiKey = params[0];
            if (apiKey != null) {
                final String lg = Locale.getDefault().getLanguage();
                final String deviceId = Installation.id(context);

                ApiClient.hello(
                        ApiClient.getService(),
                        apiKey,
                        lg,
                        deviceId);
            }
        } catch (PrkngApiError e) {
            error = e;
        }

        return null;
    }
}
