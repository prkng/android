package ng.prk.prkngandroid.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.LotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotInfoFragment extends Fragment implements MarkerInfoUpdateListener, View.OnClickListener {
    private static final String TAG = "SpotInfoFragment";

    private TextView vTitle;
    private TextView vSubtitle;
    private TextView vMainPrice;
    private View vProgressBar;
    private String mId;
    private String mTitle;
    private long mSubtitle;

    public static LotInfoFragment newInstance(String id, String title) {
        final LotInfoFragment fragment = new LotInfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_ID, id);
        bundle.putString(Const.BundleKeys.MARKER_TITLE, title);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_lot_info, container, false);

        final Bundle args = getArguments();
        if (args != null) {
            mId = args.getString(Const.BundleKeys.MARKER_ID);
            mTitle = args.getString(Const.BundleKeys.MARKER_TITLE);
        }

        downloadData(getActivity(), mId);

        vTitle = (TextView) view.findViewById(R.id.title);
        vSubtitle = (TextView) view.findViewById(R.id.subtitle);
        vMainPrice = (TextView) view.findViewById(R.id.main_price);
        vProgressBar = view.findViewById(R.id.progress);

        setupLayout(mTitle);

        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (R.id.root_view == id) {
            showDetails();
        }
    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param time
     */
    @Override
    public void setRemainingTime(long time) {

    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param test
     */
    @Override
    public void setCurrentStatus(int test) {

    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param status
     * @param capacity
     */
    @Override
    public void setCurrentStatus(LotCurrentStatus status, int capacity) {
        final Resources res = getResources();

        if (status != null) {
            Log.v(TAG, status.toString());
        }

        if (status != null && !status.isFree()) {
            final int dailyPrice = status.getMainPriceRounded();
            final int hourlyPrice = status.getHourlyPriceRounded();
            Log.v(TAG, "dailyPrice = " + dailyPrice);

            if (dailyPrice != Const.UNKNOWN_VALUE) {
                final String sDailyPrice = String.format(res.getString(R.string.currency_round),
                        dailyPrice);
                vMainPrice
                        .setText(String.format(res.getString(R.string.lot_daily_price), sDailyPrice));
            } else {
                vMainPrice.findViewById(R.id.lot_daily_price).setVisibility(View.INVISIBLE);
            }

            vSubtitle.setText(CalendarUtils.getDurationFromMillis(
                    getContext(),
                    status.getRemainingMillis()
            ));
        }

        ObjectAnimator.ofFloat(vSubtitle, View.ALPHA, 0, 1).start();
        ObjectAnimator.ofFloat(vMainPrice, View.ALPHA, 0, 1).start();
        vProgressBar.setVisibility(View.GONE);
    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param e
     */
    @Override
    public void onFailure(PrkngApiError e) {

    }

    private void setupLayout(String title) {
        vSubtitle.setAlpha(0f);
        vMainPrice.setAlpha(0f);
        vProgressBar.setVisibility(View.VISIBLE);

        if (title != null) {
            vTitle.setText(title);
        }
    }

    private void downloadData(Context context, String id) {
        (new LotInfoDownloadTask(
                context,
                new LotAgendaListAdapter(getContext(), R.layout.list_item_lot_agenda),
                this)
        ).execute(id);
    }

    private void showDetails() {
        Log.v(TAG, "showDetails");
    }

}
