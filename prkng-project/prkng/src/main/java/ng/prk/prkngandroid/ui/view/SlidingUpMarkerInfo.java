package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.ui.adapter.SpotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.LotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.CheckinHelper;
import ng.prk.prkngandroid.util.PrkngPrefs;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SlidingUpMarkerInfo extends SlidingUpPanelLayout implements
        View.OnClickListener,
        MarkerInfoUpdateListener {
    private static final String TAG = "SlidingUpMarkerInfo";

    private Context context;
    private String mMarkerId;
    private int mMarkerType;
    private RecyclerView vRecyclerView;
    private TextView vIntervalEnd;
    private TextView vDragHandle;
    private long mRemainingTime;
    private String mAddress;
    private ViewGroup vLotHeader;
    private FloatingActionButton vFAB;

    public SlidingUpMarkerInfo(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlidingUpMarkerInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpMarkerInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        vDragHandle = (TextView) findViewById(R.id.drag_handle);
        vRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        vIntervalEnd = (TextView) findViewById(R.id.interval_end);
        vLotHeader = (ViewGroup) findViewById(R.id.lot_header);
        vFAB = (FloatingActionButton) findViewById(R.id.fab);

        vFAB.setOnClickListener(this);

        setDragView(vDragHandle);

        // Setup the recycler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vRecyclerView.setLayoutManager(layoutManager);
    }

    public void setMarkerInfo(String id, String address, int type) {
        this.mMarkerId = id;
        this.mMarkerType = type;
        this.mAddress = address;

        vDragHandle.setText(address
//                            + Const.LINE_SEPARATOR + marker.getSnippet()
        );


        if (mMarkerType == Const.MapSections.ON_STREET) {
            vIntervalEnd.setVisibility(VISIBLE);
            vLotHeader.setVisibility(GONE);

            vRecyclerView.setAdapter(new SpotAgendaListAdapter(getContext(), R.layout.list_item_spot_agenda));
            (new SpotInfoDownloadTask(
                    context,
                    (SpotAgendaListAdapter) vRecyclerView.getAdapter(),
                    this)
            ).execute(mMarkerId);

            vFAB.setTag(R.id.spot_id_tag, id);
//            vFAB.setTag(R.id.lot_id_tag, null);
        } else if (mMarkerType == Const.MapSections.OFF_STREET) {
            vLotHeader.setVisibility(VISIBLE);
            vIntervalEnd.setVisibility(GONE);

            vRecyclerView.setAdapter(new LotAgendaListAdapter(getContext(), R.layout.list_item_lot_agenda));
            (new LotInfoDownloadTask(
                    context,
                    (LotAgendaListAdapter) vRecyclerView.getAdapter(),
                    this)
            ).execute(mMarkerId);

//            vFAB.setTag(R.id.lot_id_tag, id);
//            vFAB.setTag(R.id.spot_id_tag, null);
        }
    }

    @Override
    public void setPanelState(PanelState state) {
        super.setPanelState(state);
    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClick");
        if (v == vFAB) {
            final Object spotIdTag = vFAB.getTag(R.id.spot_id_tag);
            if (spotIdTag != null && spotIdTag instanceof String) {
                final String apiKey = PrkngPrefs.getInstance(context).getApiKey();

                ApiClient.checkin(
                        ApiClient.getServiceLog(),
                        apiKey,
                        (String) spotIdTag,
                        checkinCallback);
            }
        }
    }

    private final Callback checkinCallback = new Callback<CheckinData>() {

        @Override
        public void onResponse(Response response, Retrofit retrofit) {
            Log.v(TAG, "onResponse");

            CheckinHelper.checkin(getContext(),
                    (CheckinData) response.body(),
                    mAddress,
                    mRemainingTime);
        }

        @Override
        public void onFailure(Throwable t) {
            Log.v(TAG, "onFailure");

        }
    };

    @Override
    public void setRemainingTime(long time) {
        mRemainingTime = time;

        vIntervalEnd.setText(CalendarUtils.getDurationFromMillis(
                vIntervalEnd.getContext(),
                time));
    }

    @Override
    public void setCurrentStatus(int test) {

    }

    @Override
    public void setCurrentStatus(LotCurrentStatus status, int capacity) {
        final Resources res = context.getResources();

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
                ((TextView) vLotHeader.findViewById(R.id.lot_daily_price))
                        .setText(String.format(res.getString(R.string.lot_daily_price), sDailyPrice));
            } else {
                vLotHeader.findViewById(R.id.lot_daily_price).setVisibility(View.INVISIBLE);
            }
            if (hourlyPrice != Const.UNKNOWN_VALUE) {
                final String sHourlPrice = String.format(res.getString(R.string.currency_round),
                        hourlyPrice);
                ((TextView) vLotHeader.findViewById(R.id.lot_hourly_price))
                        .setText(String.format(res.getString(R.string.lot_hourly_price), sHourlPrice));
            } else {
                vLotHeader.findViewById(R.id.lot_hourly_price).setVisibility(View.INVISIBLE);
            }

            ((TextView) vLotHeader.findViewById(R.id.lot_remaining_time)).setText(CalendarUtils.getDurationFromMillis(
                    vLotHeader.getContext(),
                    status.getRemainingMillis()
            ));
        }

        // Set capacity
        if (capacity != Const.UNKNOWN_VALUE) {
            ((TextView) vLotHeader.findViewById(R.id.lot_capacity))
                    .setText(String.format(res.getString(R.string.lot_capactiy), capacity));
        } else {
            vLotHeader.findViewById(R.id.lot_capacity).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFailure(PrkngApiError e) {
        e.showSnackbar(this);
    }
}
