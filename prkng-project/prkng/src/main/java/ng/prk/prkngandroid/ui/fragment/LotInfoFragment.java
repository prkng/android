package ng.prk.prkngandroid.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.BusinessIntervalList;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.ui.activity.OnMarkerInfoClickListener;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.LotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotInfoFragment extends Fragment implements
        MarkerInfoUpdateListener,
        View.OnClickListener {
    private static final String TAG = "SpotInfoFragment";

    private OnMarkerInfoClickListener listener;
    private TextView vTitle;
    private TextView vSubtitle;
    private TextView vMainPrice;
    private View vProgressBar;
    private RecyclerView vRecyclerView;
    private LotAgendaListAdapter mAdapter;
    private String mId;
    private String mTitle;
    private long mSubtitle;
    private BusinessIntervalList mDataset;
    private LotCurrentStatus mStatus;
    private int mCapacity;
    private LotAttrs mAttrs;

    public static LotInfoFragment newInstance(String id, String title) {
        final LotInfoFragment fragment = new LotInfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_ID, id);
        bundle.putString(Const.BundleKeys.MARKER_TITLE, title);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static LotInfoFragment clone(LotInfoFragment fragment) {
        LotInfoFragment clone = new LotInfoFragment();

        clone.mSubtitle = fragment.mSubtitle;
        clone.mDataset = fragment.mDataset;
        clone.mStatus = fragment.mStatus;
        clone.mCapacity = fragment.mCapacity;
        clone.mAttrs = fragment.mAttrs;

        final Bundle bundle = fragment.getArguments();
        bundle.putBoolean(Const.BundleKeys.IS_EXPANDED, true);
        clone.setArguments(bundle);

        return clone;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnMarkerInfoClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMarkerInfoClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final Bundle args = getArguments();
        mId = args.getString(Const.BundleKeys.MARKER_ID);
        mTitle = args.getString(Const.BundleKeys.MARKER_TITLE);
        final boolean isExpanded = args.getBoolean(Const.BundleKeys.IS_EXPANDED, false);

        final View view = inflater.inflate(
                isExpanded ? R.layout.fragment_lot_details : R.layout.fragment_lot_info,
                container,
                false);

        vTitle = (TextView) view.findViewById(R.id.title);
        vSubtitle = (TextView) view.findViewById(R.id.subtitle);
        vMainPrice = (TextView) view.findViewById(R.id.main_price);
        vProgressBar = view.findViewById(R.id.progress);
        vRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        setupLayout(view);

        downloadData(getActivity(), mId);

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
     * @param status
     * @param capacity
     */
    @Override
    public void setCurrentStatus(LotCurrentStatus status, int capacity) {
        this.mStatus = status;
        this.mCapacity = capacity;

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
                vMainPrice.setVisibility(View.INVISIBLE);
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


    @Override
    public void setDataset(ArrayList list) {
        mDataset = (BusinessIntervalList) list;
        Log.v(TAG, "setDataset: OK. size: " + mDataset.size());
        if (vRecyclerView != null) {
            mAdapter.swapDataset(mDataset);
            Log.v(TAG, "setDataset: OK. size: " + mDataset.size());
        }
    }


    @Override
    public void setAttributes(LotAttrs attrs) {
        this.mAttrs = attrs;
        if (mAdapter != null) {
            mAdapter.setFooterAttrs(attrs);
        }
    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param e
     */
    @Override
    public void onFailure(PrkngApiError e) {

    }

    private void setupLayout(View view) {
        view.setOnClickListener(this);

        vProgressBar.setVisibility(View.VISIBLE);

        vSubtitle.setAlpha(0f);
        vMainPrice.setAlpha(0f);

        if (mTitle != null) {
            vTitle.setText(mTitle);
        }

        if (vRecyclerView != null) {
            // Setup the recycler view
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            vRecyclerView.setLayoutManager(layoutManager);

            mAdapter = new LotAgendaListAdapter(getContext(), R.layout.list_item_lot_agenda);
            vRecyclerView.setAdapter(mAdapter);
        }
    }

    private void downloadData(Context context, String id) {
        if (mDataset == null) {
            (new LotInfoDownloadTask(
                    context,
                    this)
            ).execute(id);
        } else {
            setDataset(mDataset);
            setCurrentStatus(mStatus, mCapacity);
            setAttributes(mAttrs);
        }
    }

    private void showDetails() {
        Log.v(TAG, "showDetails");
        listener.onClick(this);
    }

}
