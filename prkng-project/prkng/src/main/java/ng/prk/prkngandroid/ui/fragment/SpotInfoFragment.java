package ng.prk.prkngandroid.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.RestrInterval;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.model.ui.HumanDuration;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.ui.activity.OnMarkerInfoClickListener;
import ng.prk.prkngandroid.ui.adapter.SpotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.AnalyticsUtils;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class SpotInfoFragment extends Fragment implements
        View.OnClickListener,
        MarkerInfoUpdateListener, Toolbar.OnMenuItemClickListener {
    private static final String TAG = "SpotInfoFragment";

    private OnMarkerInfoClickListener listener;
    private TextView vTitle;
    private View vPrice;
    private TextView vRemainingTime;
    private TextView vRemainingTimePrefix;
    private Button vCheckinBtn;
    private View vProgressBar;
    private RecyclerView vRecyclerView;
    private String mId;
    private String mTitle;
    private long mRemainingTime;
    private int mParkingRestrType;
    private RestrIntervalsList mDataset;
    private boolean isExpanded;

    public static SpotInfoFragment newInstance(String id, String title) {
        final SpotInfoFragment fragment = new SpotInfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_ID, id);
        bundle.putString(Const.BundleKeys.MARKER_TITLE, title);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static SpotInfoFragment clone(SpotInfoFragment fragment) {
        final SpotInfoFragment clone = new SpotInfoFragment();

        clone.mRemainingTime = fragment.mRemainingTime;
        clone.mParkingRestrType = fragment.mParkingRestrType;
        clone.mDataset = fragment.mDataset;

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
        isExpanded = args.getBoolean(Const.BundleKeys.IS_EXPANDED, false);

        final View view = inflater.inflate(
                isExpanded ? R.layout.fragment_spot_details : R.layout.fragment_spot_info,
                container,
                false);

        vTitle = (TextView) view.findViewById(R.id.title);
        vPrice = view.findViewById(R.id.price);
        vRemainingTime = (TextView) view.findViewById(R.id.remaining_time);
        vRemainingTimePrefix = (TextView) view.findViewById(R.id.remaining_time_prefix);
        vCheckinBtn = (Button) view.findViewById(R.id.btn_checkin);
        vProgressBar = view.findViewById(R.id.progress);
        vRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        setupLayout(view);

        downloadData(getActivity(), mId);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isExpanded) {
            AnalyticsUtils.sendFragmentView(this, mId);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (R.id.btn_checkin == id) {
            doCheckin();
        } else if (R.id.root_view == id) {
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
        mRemainingTime = time;

        if (!isExpanded) {
            final HumanDuration duration = new HumanDuration.Builder(vRemainingTime.getContext())
                    .millis(time)
                    .spot()
                    .status(mParkingRestrType)
                    .build();
            vRemainingTime.setText(duration.getExpiry());

            final String prefix = duration.getPrefix();
            if (prefix == null || prefix.isEmpty()) {
                vRemainingTimePrefix.setVisibility(View.GONE);
            } else {
                vRemainingTimePrefix.setText(prefix);
            }
        }
    }

    /**
     * Implements MarkerInfoUpdateListener
     *
     * @param status
     * @param capacity
     */
    @Override
    public void setCurrentStatus(LotCurrentStatus status, int capacity) {

    }

    @Override
    public void setDataset(ArrayList list) {
        mDataset = (RestrIntervalsList) list;

        if (isExpanded) {
            final SpotAgendaListAdapter adapter = new SpotAgendaListAdapter(getContext(), R.layout.list_item_spot_agenda);
            vRecyclerView.setAdapter(adapter);

            adapter.swapDataset((RestrIntervalsList) list);
        } else {
            setHourlyRate();
        }

        hideProgressBar();

    }

    private void hideProgressBar() {
        if (!isExpanded && getView() != null) {
            ObjectAnimator.ofFloat(getView().findViewById(R.id.price), View.ALPHA, 0, 1).start();
            ObjectAnimator.ofFloat(getView().findViewById(R.id.subtitle), View.ALPHA, 0, 1).start();
            ObjectAnimator.ofFloat(getView().findViewById(R.id.ic_agenda), View.ALPHA, 0, 1).start();
        }

        vProgressBar.setVisibility(View.GONE);
    }

    private void setHourlyRate() {
        if (!isExpanded) {
            final int index = mDataset.findContainingIntervalToday(
                    CalendarUtils.todayMillis(),
                    CalendarUtils.getIsoDayOfWeek());
            if (index != Const.UNKNOWN_VALUE) {
                final RestrInterval interval = mDataset.get(index);
                if (interval != null) {
                    mParkingRestrType = interval.getType();
                    if (interval.hasHourlyRate()) {
                        vPrice.setVisibility(View.VISIBLE);
                        ((TextView) getView().findViewById(R.id.main_price))
                                .setText(String.format(
                                        getResources().getString(R.string.currency_decimals),
                                        interval.getHourlyRate()));
                    }
                }
            }
        }
    }

    @Override
    public void setAttributes(LotAttrs attrs) {

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
        if (isExpanded) {
            final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
                TypefaceHelper.setTitle(getActivity(), toolbar, mTitle);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                toolbar.inflateMenu(R.menu.menu_spot_info);
                toolbar.setOnMenuItemClickListener(this);
            }

            // Setup the recycler view
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            vRecyclerView.setLayoutManager(layoutManager);
        } else {
            view.setOnClickListener(this);

            vTitle.setText(mTitle);

            vCheckinBtn.setOnClickListener(this);
        }
    }

    private void downloadData(Context context, String id) {
        if (mDataset == null) {
            (new SpotInfoDownloadTask(
                    context,
                    this)
            ).execute(id);
        } else {
            setDataset(mDataset);
            setRemainingTime(mRemainingTime);
        }

    }

    private void showDetails() {
        listener.expandMarkerInfo(this);
    }

    private void doCheckin() {
        getActivity().startActivity(
                CheckinActivity.newIntent(getActivity(),
                        mId,
                        mTitle,
                        mRemainingTime)
        );
        listener.hideMarkerInfo(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_checkin) {
            doCheckin();
        }
        return false;
    }
}
