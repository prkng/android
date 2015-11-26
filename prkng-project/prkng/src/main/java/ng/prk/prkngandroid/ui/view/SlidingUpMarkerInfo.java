package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.thread.SpotInfoDownloadTask;

public class SlidingUpMarkerInfo extends SlidingUpPanelLayout {

    private String mMarkerId;
    private int mMarkerType;

    public SlidingUpMarkerInfo(Context context) {
        super(context);
    }

    public SlidingUpMarkerInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingUpMarkerInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMarkerInfo(String id, int type) {
        this.mMarkerId = id;
        this.mMarkerType = type;

        if (mMarkerType == Const.MapSections.ON_STREET) {
            (new SpotInfoDownloadTask()).execute(mMarkerId);
        }
    }

    @Override
    public void setPanelState(PanelState state) {
        super.setPanelState(state);
    }
}
