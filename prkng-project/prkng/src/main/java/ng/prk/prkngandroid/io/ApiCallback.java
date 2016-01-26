package ng.prk.prkngandroid.io;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import retrofit2.Call;
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
    public void onResponse(Call<T> call, Response<T> response) {

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();

        if (t instanceof PrkngApiError) {
            final PrkngApiError error = (PrkngApiError) t;
            Log.v(TAG, "PrkngApiError");
            if (error.isUnauthorized() && context != null) {
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(
                            LoginActivity.newIntent(context), Const.RequestCodes.AUTH_LOGIN);
                } else {
                    context.startActivity(LoginActivity.newIntent(context));
                }
            } else {
                if (view != null) {
                    error.showSnackbar(view);
                }
            }
        }

    }
}
