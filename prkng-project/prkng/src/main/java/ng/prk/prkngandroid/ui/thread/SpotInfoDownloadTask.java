package ng.prk.prkngandroid.ui.thread;

import android.os.AsyncTask;
import android.util.Log;

import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.LinesGeoJSONFeature;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.model.SpotRules;

public class SpotInfoDownloadTask extends AsyncTask<String, Void, SpotRules> {
    private final static String TAG = "SpotInfo";

    private String mApiKey;

    @Override
    protected SpotRules doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

        final String spotId = params[0];
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
        Log.v(TAG, "rules = " + spotRules.toString());


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
