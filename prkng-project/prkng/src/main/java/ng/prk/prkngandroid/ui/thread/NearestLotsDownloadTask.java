package ng.prk.prkngandroid.ui.thread;

import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.model.ui.MapAssets;

public class NearestLotsDownloadTask extends LotsDownloadTask {
    private final static String TAG = "LotsTask";
    private boolean mForceBoundingBox = false;

    public NearestLotsDownloadTask(MapView mapView, MapAssets mapAssets, MapTaskListener listener) {
        super(mapView, mapAssets, listener);
    }

    @Override
    protected int getNearest() {
        return 1;
    }

    @Override
    protected boolean forceBoundingBox() {
        return true;
    }

}
