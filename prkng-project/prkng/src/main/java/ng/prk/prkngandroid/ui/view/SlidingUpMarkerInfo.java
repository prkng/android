package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.ui.adapter.SpotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.LotInfoDownloadTask;
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;

public class SlidingUpMarkerInfo extends SlidingUpPanelLayout {

    private Context context;
    private String mMarkerId;
    private int mMarkerType;
    private RecyclerView vRecyclerView;
    private TextView vIntervalEnd;
    private ViewGroup vLotHeader;

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

        setDragView(R.id.drag_handle);

        vRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        vIntervalEnd = (TextView) findViewById(R.id.interval_end);
        vLotHeader = (ViewGroup) findViewById(R.id.lot_header);

        // Setup the recycler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vRecyclerView.setLayoutManager(layoutManager);
    }

    public void setMarkerInfo(String id, int type) {
        this.mMarkerId = id;
        this.mMarkerType = type;

        if (mMarkerType == Const.MapSections.ON_STREET) {
            vIntervalEnd.setVisibility(VISIBLE);
            vLotHeader.setVisibility(GONE);

            vRecyclerView.setAdapter(new SpotAgendaListAdapter(getContext(), R.layout.list_item_spot_agenda));
            (new SpotInfoDownloadTask(
                    context,
                    (SpotAgendaListAdapter) vRecyclerView.getAdapter(),
                    vIntervalEnd)
            ).execute(mMarkerId);
        } else if (mMarkerType == Const.MapSections.OFF_STREET) {
            vLotHeader.setVisibility(VISIBLE);
            vIntervalEnd.setVisibility(GONE);

            vRecyclerView.setAdapter(new LotAgendaListAdapter(getContext(), R.layout.list_item_lot_agenda));
            (new LotInfoDownloadTask(
                    context,
                    (LotAgendaListAdapter) vRecyclerView.getAdapter(),
                    vLotHeader)
            ).execute(mMarkerId);
        }
    }

    @Override
    public void setPanelState(PanelState state) {
        super.setPanelState(state);
    }
}
