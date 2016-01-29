package ng.prk.prkngandroid.ui.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class RedSnackbar {
    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        final Snackbar snackbar = Snackbar.make(view, text, duration);

        try {
            final ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_primary_dark));

            snackbar.setActionTextColor(ContextCompat.getColor(view.getContext(), R.color.cream1));

            final TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
            tv.setTypeface(TypefaceHelper.getTypeface(view.getContext(), Const.TypeFaces.BOLD));

            final Button btn = (Button) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_action);
            btn.setTypeface(TypefaceHelper.getTypeface(view.getContext(), Const.TypeFaces.BOLD));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return snackbar;
    }
}
