package ng.prk.prkngandroid.ui.thread;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.model.SpotRules;
import ng.prk.prkngandroid.ui.adapter.AgendaListAdapter;
import ng.prk.prkngandroid.util.CalendarUtils;

public class SpotInfoDownloadTask extends AsyncTask<String, Void, SpotRules> {
    private final static String TAG = "SpotInfo";

    private String mApiKey;

    private AgendaListAdapter mAdapter;
    private TextView vIntervalEnd;

    public SpotInfoDownloadTask(AgendaListAdapter adapter, TextView intervalEnd) {
        this.mAdapter = adapter;
        this.vIntervalEnd = intervalEnd;
    }

    @Override
    protected SpotRules doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

//        String spotId = "416753"; // arret + Interdiction
//        String spotId = "458653"; // arret + Interdiction
//        String spotId = "416663"; //  tues + friday
//        String spotId = "462798"; //  paid + interdiction
//        String spotId = "448955"; // arret + paid (ConcurrentModificationException)
//        String spotId = "417832"; // arret mardi 9:30-10:30, timeRemaining = 7 days
//        String spotId = "442896"; // 48h+ parking allowed
//        String spotId = "417002"; // 6day+ parking allowed
//        String spotId = "447204"; // mercredi NoParking 9-10. looped week
//        String spotId = "429308"; // arret x2 + paid x2
//        String spotId = "429400"; // Allowed all week
        String spotId = "419907"; // TimeMax (60) 9-16

//        final String spotId = params[0];
        Log.v(TAG, "spotId = " + spotId);

        final PrkngService service = ApiClient.getServiceLog();
        try {
            final String apiKey = getApiKey(service);

            LinesGeoJSONFeature spotFeatures = ApiClient.getParkingSpotInfo(service, apiKey, spotId);
            return spotFeatures.getProperties().getRules();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(SpotRules spotRules) {
        Log.v(TAG, "onPostExecute");
        if (spotRules != null) {
            Log.v(TAG, "rules = " + spotRules.toString());
            final RestrIntervalsList parkingAgenda = spotRules.getParkingAgenda();
            mAdapter.swapDataset(parkingAgenda);

            final long remainingTime = spotRules.getRemainingTime(parkingAgenda,
                    CalendarUtils.todayMillis());

            vIntervalEnd.setText(CalendarUtils.getDurationFromMillis(
                    vIntervalEnd.getContext(),
                    remainingTime));
        }

//        SpotRuleAgenda agenda = spotRules.get(0).getAgenda();
//        Log.v(TAG, "timeMax = " + spotRules.get(0).getTimeMaxParking());
//        Log.v(TAG, "monday = " + agenda.getMonday().get(0));
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
