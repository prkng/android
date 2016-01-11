package ng.prk.prkngandroid.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.PrkngApp;
import ng.prk.prkngandroid.R;

public class DurationDialog extends DialogFragment {

    private static final String TAG = "DurationDialog ";

    public static DurationDialog newInstance(float duration) {
        final DurationDialog dialog = new DurationDialog();

        final Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleKeys.CURRENT_INDEX, getDurationIndex(duration));
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PrkngDialogStyle);
        builder.setTitle(R.string.dialog_durations_title)
                .setIcon(R.drawable.ic_action_timer)
                .setSingleChoiceItems(R.array.durations_filter, getSelectedItem(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "onClick "
                                + String.format("which = %s", which));
                        Log.v(TAG, "duration = " + Const.UiConfig.DURATIONS[which]);
                        PrkngApp.getInstance(getContext())
                                .setMapDurationFilter(Const.UiConfig.DURATIONS[which]);

                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setSelection(3);
        return alertDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    private int getSelectedItem() {
        return getArguments().getInt(Const.BundleKeys.CURRENT_INDEX, 0);
    }

    private static int getDurationIndex(float duration) {
        final int nbDurations = Const.UiConfig.DURATIONS.length;
        for (int i = 0; i < nbDurations; i++) {
            if (Float.valueOf(duration).equals(Const.UiConfig.DURATIONS[i])) {
                return i;
            }
        }

        return Const.UiConfig.DEFAULT_DURATION_INDEX;
    }
}
