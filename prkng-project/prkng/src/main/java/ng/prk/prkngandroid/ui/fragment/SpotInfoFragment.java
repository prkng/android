package ng.prk.prkngandroid.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotInfoFragment extends Fragment implements
        View.OnClickListener,
        MarkerInfoUpdateListener {
    private static final String TAG = "SpotInfoFragment";

    private TextView vTitle;
    private TextView vSubtitle;
    private Button vCheckinBtn;
    private View vProgressBar;
    private String mId;
    private String mTitle;
    private long mSubtitle;

    public static SpotInfoFragment newInstance(String id, String title) {
        final SpotInfoFragment fragment = new SpotInfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putString(Const.BundleKeys.MARKER_ID, id);
        bundle.putString(Const.BundleKeys.MARKER_TITLE, title);
        fragment.setArguments(bundle);

        return fragment;
    }

    private final Callback checkinCallback = new Callback<CheckinData>() {

        @Override
        public void onResponse(Response<CheckinData> response) {
            final CheckinData checkin = response.body();
            if (checkin != null) {
                checkin.fixTimezones();
            }

            final Context context = getContext();
            CheckinHelper.checkin(context,
                    checkin,
                    mTitle,
                    mSubtitle);
            context.startActivity(CheckinActivity.newIntent(context));
        }

        @Override
        public void onFailure(Throwable t) {
            Log.v(TAG, "onFailure");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_spot_info, container, false);

        final Bundle args = getArguments();
        if (args != null) {
            mId = args.getString(Const.BundleKeys.MARKER_ID);
            mTitle = args.getString(Const.BundleKeys.MARKER_TITLE);
        }

        downloadData(getActivity(), mId);

        vTitle = (TextView) view.findViewById(R.id.title);
        vSubtitle = (TextView) view.findViewById(R.id.subtitle);
        vCheckinBtn = (Button) view.findViewById(R.id.btn_checkin);
        vProgressBar = view.findViewById(R.id.progress);

        setupLayout(mId, mTitle);

        view.setOnClickListener(this);

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
        mSubtitle = time;

        vSubtitle.setText(CalendarUtils.getDurationFromMillis(
                vSubtitle.getContext(),
                time));
//        AnimatorSet anim = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in);
//        anim.setTarget(vSubtitle);
//        anim.start();
        ObjectAnimator.ofFloat(vSubtitle, View.ALPHA, 0, 1).start();
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
        RestrIntervalsList data = (RestrIntervalsList) list;
        Log.v(TAG, "setDataset: OK. size: " + data.size());
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

    private void setupLayout(String id, String title) {
        vSubtitle.setAlpha(0f);
        vProgressBar.setVisibility(View.VISIBLE);

        if (title != null) {
            vTitle.setText(title);
        }

        vCheckinBtn.setTag(R.id.spot_id_tag, id);
        vCheckinBtn.setOnClickListener(this);
    }

    private void downloadData(Context context, String id) {
        (new SpotInfoDownloadTask(
                context,
                this)
        ).execute(id);
    }

    private void showDetails() {
        Log.v(TAG, "showDetails");
    }

    private void doCheckin() {
        final Object spotIdTag = vCheckinBtn.getTag(R.id.spot_id_tag);
        if (spotIdTag != null && spotIdTag instanceof String) {
            final String apiKey = PrkngPrefs.getInstance(getActivity()).getApiKey();

            ApiClient.checkin(
                    ApiClient.getServiceLog(),
                    apiKey,
                    (String) spotIdTag,
                    checkinCallback);
        }
    }
}
