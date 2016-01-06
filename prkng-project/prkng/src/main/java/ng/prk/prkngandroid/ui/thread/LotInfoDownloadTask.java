package ng.prk.prkngandroid.ui.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LotInfoDownloadTask extends AsyncTask<String, Void, GeoJSONFeatureProperties> {
    private final static String TAG = "LotInfo";

    private final Context context;
    private final MarkerInfoUpdateListener listener;
    private LotAgendaListAdapter mAdapter;

    public LotInfoDownloadTask(Context context, LotAgendaListAdapter adapter, MarkerInfoUpdateListener listener) {
        this.context = context;
        this.listener = listener;
        this.mAdapter = adapter;
    }

    @Override
    protected GeoJSONFeatureProperties doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

//        final String lotId = params[0];
        String lotId = params[0];

//        lotId = "20";
//        lotId = "86";
//        lotId = "89";
//        lotId = "95";
//        lotId = "185";
//        lotId = String.valueOf(new Random().nextInt(99) + 1);

        Log.i(TAG, "lotId = " + lotId);

        final PrkngService service = ApiClient.getService();
        try {
            final String apiKey = PrkngPrefs.getInstance(context).getApiKey();

            PointsGeoJSONFeature spotFeatures = ApiClient.getParkingLotInfo(service, apiKey, lotId);
            return spotFeatures.getProperties();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(GeoJSONFeatureProperties properties) {
        Log.v(TAG, "onPostExecute");
        if (properties != null) {
//            Log.v(TAG, properties.getAgenda().getMonday().get(0).toString());
            Log.v(TAG, properties.getAttrs().toString());
            Log.v(TAG, properties.getStreetView().toString());

            mAdapter.swapDataset(properties.getAgenda().getLotAgenda());
            LotCurrentStatus status = properties.getAgenda().getLotCurrentStatus(CalendarUtils.todayMillis());

            final int capacity = properties.getCapacity();
            listener.setCurrentStatus(status, capacity );

            mAdapter.setFooterAttrs(properties.getAttrs());
        }
    }
}
