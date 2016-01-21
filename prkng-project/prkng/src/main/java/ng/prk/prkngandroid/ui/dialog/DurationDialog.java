package ng.prk.prkngandroid.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class DurationDialog extends DialogFragment {
    private static final String TAG = "DurationDialog ";

    private OnDurationChangedListener listener;

    public static DurationDialog newInstance(float duration) {
        final DurationDialog dialog = new DurationDialog();

        final Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleKeys.CURRENT_INDEX, (int) Math.floor(duration));
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnDurationChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDurationChangedListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.dialog_duration_discreet_seekbar, null);
        final TextView vTitle = (TextView) view.findViewById(R.id.dialog_title);
        final TextView vLegend = (TextView) view.findViewById(R.id.dialog_legend);
        final DiscreteSeekBar seekbar = (DiscreteSeekBar) view.findViewById(R.id.seekbar_duration);

        int currentDuration = getArguments().getInt(Const.BundleKeys.CURRENT_INDEX, 0);

        seekbar.setProgress(currentDuration);
        vTitle.setText(getDurationTitle(currentDuration));
        vLegend.setText(getDurationLegend(currentDuration));

        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                vTitle.setText(getDurationTitle(value));
                vLegend.setText(getDurationLegend(value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        return new android.app.AlertDialog.Builder(getActivity(), R.style.PrkngDialogStyle)
                .setView(view)
                .setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                listener.onDurationChanged(getTimeFilter(seekbar));
                            }
                        }
                )
                .setNegativeButton(R.string.btn_cancel, null)
                .create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    private String getDurationTitle(int progress) {
        if (progress == 0) {
            return getResources().getString(R.string.quantity_half);
        } else {
            return String.valueOf(progress);
        }
    }

    private String getDurationLegend(int progress) {
        return getResources().getQuantityString(
                R.plurals.duration_legend,
                progress == 0 ? 1 : progress);
    }

    private float getTimeFilter(DiscreteSeekBar seekBar) {
        final int progress = seekBar.getProgress();
        return progress == 0 ? 0.5f : progress;
    }

    public interface OnDurationChangedListener {
        void onDurationChanged(float duration);
    }
}