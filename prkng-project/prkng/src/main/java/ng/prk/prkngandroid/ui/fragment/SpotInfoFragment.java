package ng.prk.prkngandroid.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.ui.activity.OnMarkerInfoClickListener;
import ng.prk.prkngandroid.ui.adapter.SpotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;
import ng.prk.prkngandroid.util.TypefaceHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotInfoFragment extends Fragment implements
        View.OnClickListener,
        MarkerInfoUpdateListener, Toolbar.OnMenuItemClickListener {
    private static final String TAG = "SpotInfoFragment";

    private OnMarkerInfoClickListener listener;
    private TextView vTitle;
    private TextView vSubtitle;
    private Button vCheckinBtn;
    private View vProgressBar;
    private RecyclerView vRecyclerView;
    private String mId;
    private String mTitle;
    private long mRemainingTime;
    private RestrIntervalsList mDataset;

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
        clone.mDataset = fragment.mDataset;

        final Bundle bundle = fragment.getArguments();
        bundle.putBoolean(Const.BundleKeys.IS_EXPANDED, true);
        clone.setArguments(bundle);

        return clone;
    }

    private final Callback checkinCallback = new Callback<CheckinData>() {
        @Override
        public void onResponse(Call<CheckinData> call, Response<CheckinData> response) {
            final CheckinData checkin = response.body();
            if (checkin != null) {
                checkin.fixTimezones();
            }

            final Context context = getContext();
            CheckinHelper.checkin(context,
                    checkin,
                    mTitle,
                    mRemainingTime);
            context.startActivity(CheckinActivity.newIntent(context));
        }

        @Override
        public void onFailure(Call<CheckinData> call, Throwable t) {
            Log.v(TAG, "onFailure");

        }
    };

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
                isExpanded ? R.layout.fragment_spot_details : R.layout.fragment_spot_info,
                container,
                false);

        vTitle = (TextView) view.findViewById(R.id.title);
        vSubtitle = (TextView) view.findViewById(R.id.subtitle);
        vCheckinBtn = (Button) view.findViewById(R.id.btn_checkin);
        vProgressBar = view.findViewById(R.id.progress);
        vRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        setupLayout(view);

        downloadData(getActivity(), mId);

        return view;
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

        if (vSubtitle != null) {
            vSubtitle.setText(CalendarUtils.getDurationFromMillis(
                    vSubtitle.getContext(),
                    time));
            ObjectAnimator.ofFloat(vSubtitle, View.ALPHA, 0, 1).start();
        }
        vProgressBar.setVisibility(View.GONE);
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
        Log.v(TAG, "setDataset "
                + String.format("list = %s", list));

        mDataset = (RestrIntervalsList) list;
        if (vRecyclerView != null) {
            final SpotAgendaListAdapter adapter = new SpotAgendaListAdapter(getContext(), R.layout.list_item_spot_agenda);
            vRecyclerView.setAdapter(adapter);

            adapter.swapDataset((RestrIntervalsList) list);
            Log.v(TAG, "setDataset: OK. size: " + mDataset.size());
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
        view.setOnClickListener(this);

        vProgressBar.setVisibility(View.VISIBLE);

        if (vSubtitle != null) {
            vSubtitle.setAlpha(0f);
        }

        if (mTitle != null) {
            if (vTitle != null) {
                vTitle.setText(mTitle);
            }
            final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            if (toolbar != null) {
//                toolbar.setTitle(mTitle);
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
        }

        if (vCheckinBtn != null) {
            vCheckinBtn.setTag(R.id.spot_id_tag, mId);
            vCheckinBtn.setOnClickListener(this);
        }

        if (vRecyclerView != null) {
            // Setup the recycler view
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            vRecyclerView.setLayoutManager(layoutManager);
        }
    }

    private void downloadData(Context context, String id) {
        if (mDataset == null) {
            Log.v(TAG, "downloadData");
            (new SpotInfoDownloadTask(
                    context,
                    this)
            ).execute(id);
        } else {
            setRemainingTime(mRemainingTime);
            setDataset(mDataset);
        }

    }

    private void showDetails() {
        Log.v(TAG, "showDetails");
//        listener.onClick(mId, mTitle);
        listener.onClick(this);
    }

    private void doCheckin() {
        final String apiKey = PrkngPrefs.getInstance(getActivity()).getApiKey();

        ApiClient.checkin(
                ApiClient.getServiceLog(),
                apiKey,
                mId,
                checkinCallback);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_checkin) {
            doCheckin();
        }
        return false;
    }
}
