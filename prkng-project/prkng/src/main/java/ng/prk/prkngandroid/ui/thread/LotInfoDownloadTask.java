package ng.prk.prkngandroid.ui.thread;

import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotInfoDownloadTask extends AsyncTask<String, Void, GeoJSONFeatureProperties> {
    private final static String TAG = "LotInfo";

    private String mApiKey;

    private LotAgendaListAdapter mAdapter;
    private ViewGroup vParent;

    public LotInfoDownloadTask(LotAgendaListAdapter adapter, ViewGroup parent) {
        this.mAdapter = adapter;
        this.vParent = parent;
    }

    @Override
    protected GeoJSONFeatureProperties doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

//        String lotId = "20";

        final String lotId = params[0];
        Log.v(TAG, "lotId = " + lotId);

        final PrkngService service = ApiClient.getServiceLog();
        try {
            final String apiKey = getApiKey(service);

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

            LotCurrentStatus status = properties.getAgenda().getLotCurrentStatus(CalendarUtils.todayMillis());
            Log.v(TAG, status.toString());
            mAdapter.swapDataset(properties.getAgenda().getParkingSpotAgenda());
            mAdapter.setFooterAttrs(properties.getAttrs());
        }
    }

    @Deprecated
    public String getApiKey(PrkngService service) {
        if (mApiKey == null || mApiKey.isEmpty()) {
            LoginObject loginObject = ApiClient
                    .loginEmail(
                            service,
                            "mudar@prk.ng",
                            "mudar123");
            Log.v(TAG, "name = " + loginObject.getName() + " email = " + loginObject.getEmail());
            Log.v(TAG, "mApiKey = " + loginObject.getApikey());
            mApiKey = loginObject.getApikey();
        }

        return mApiKey;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
    }
}
