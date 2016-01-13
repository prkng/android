package ng.prk.prkngandroid.io;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import ng.prk.prkngandroid.ui.activity.LoginActivity;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallback<T> implements Callback<T> {
    private final static String TAG = "ApiCallback";

    private Context context;
    private View view;

    public ApiCallback() {
        this(null, null);
    }

    public ApiCallback(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void onResponse(Response<T> response) {

    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();

        if (t instanceof PrkngApiError) {
            final PrkngApiError error = (PrkngApiError) t;
            Log.v(TAG, "PrkngApiError");
            if (error.isUnauthorized() && context != null) {
                context.startActivity(LoginActivity.newIntent(context));

                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            } else {
                if (view != null) {
                    error.showSnackbar(view);
                }
            }
        }

    }
}
