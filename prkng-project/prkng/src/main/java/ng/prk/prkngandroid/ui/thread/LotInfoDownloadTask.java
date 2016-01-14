package ng.prk.prkngandroid.ui.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.ui.thread.base.MarkerInfoUpdateListener;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LotInfoDownloadTask extends AsyncTask<String, Void, GeoJSONFeatureProperties> {
    private final static String TAG = "LotInfo";

    private final Context context;
    private final MarkerInfoUpdateListener listener;
    private PrkngApiError error;

    public LotInfoDownloadTask(Context context, MarkerInfoUpdateListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected GeoJSONFeatureProperties doInBackground(String... params) {
        final String lotId = params[0];
//        lotId = "20";
//        lotId = "86";
//        lotId = "89";
//        lotId = "95";
//        lotId = "185";
//        lotId = String.valueOf(new Random().nextInt(99) + 1);

//        Log.i(TAG, "lotId = " + lotId);

        final PrkngService service = ApiClient.getService();
        try {
            final String apiKey = PrkngPrefs.getInstance(context).getApiKey();

            PointsGeoJSONFeature spotFeatures = ApiClient.getParkingLotInfo(service, apiKey, lotId);
            return spotFeatures.getProperties();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (PrkngApiError e) {
            error = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(GeoJSONFeatureProperties properties) {
        try {
            if (error != null) {
                listener.onFailure(error);
            }

            if (properties != null) {
                Log.v(TAG, properties.getAttrs().toString());
                Log.v(TAG, properties.getStreetView().toString());

                listener.setDataset(properties.getAgenda().getLotAgenda());
                LotCurrentStatus status = properties.getAgenda().getLotCurrentStatus(CalendarUtils.todayMillis());

                final int capacity = properties.getCapacity();
                listener.setCurrentStatus(status, capacity);

                listener.setAttributes(properties.getAttrs());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
