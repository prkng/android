package ng.prk.prkngandroid.ui.thread;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.PointsGeoJSONFeature;
import ng.prk.prkngandroid.ui.adapter.LotAgendaListAdapter;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LotInfoDownloadTask extends AsyncTask<String, Void, GeoJSONFeatureProperties> {
    private final static String TAG = "LotInfo";

    private String mApiKey;

    private final Context context;
    private LotAgendaListAdapter mAdapter;
    private ViewGroup vHeader;

    public LotInfoDownloadTask(Context context, LotAgendaListAdapter adapter, ViewGroup header) {
        this.context = context;
        this.mAdapter = adapter;
        this.vHeader = header;
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
            final Resources res = vHeader.getContext().getResources();

//            Log.v(TAG, properties.getAgenda().getMonday().get(0).toString());
            Log.v(TAG, properties.getAttrs().toString());
            Log.v(TAG, properties.getStreetView().toString());


            mAdapter.swapDataset(properties.getAgenda().getLotAgenda());
            LotCurrentStatus status = properties.getAgenda().getLotCurrentStatus(CalendarUtils.todayMillis());
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
                    ((TextView) vHeader.findViewById(R.id.lot_daily_price))
                            .setText(String.format(res.getString(R.string.lot_daily_price), sDailyPrice));
                } else {
                    vHeader.findViewById(R.id.lot_daily_price).setVisibility(View.INVISIBLE);
                }
                if (hourlyPrice != Const.UNKNOWN_VALUE) {
                    final String sHourlPrice = String.format(res.getString(R.string.currency_round),
                            hourlyPrice);
                    ((TextView) vHeader.findViewById(R.id.lot_hourly_price))
                            .setText(String.format(res.getString(R.string.lot_hourly_price), sHourlPrice));
                } else {
                    vHeader.findViewById(R.id.lot_hourly_price).setVisibility(View.INVISIBLE);
                }

                ((TextView) vHeader.findViewById(R.id.lot_remaining_time)).setText(CalendarUtils.getDurationFromMillis(
                        vHeader.getContext(),
                        status.getRemainingMillis()
                ));
            }

            final int capacity = properties.getCapacity();
            if (capacity != Const.UNKNOWN_VALUE) {
                ((TextView) vHeader.findViewById(R.id.lot_capacity))
                        .setText(String.format(res.getString(R.string.lot_capactiy), capacity));
            } else {
                vHeader.findViewById(R.id.lot_capacity).setVisibility(View.INVISIBLE);
            }


            mAdapter.setFooterAttrs(properties.getAttrs());
        }
    }
}
