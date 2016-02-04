package ng.prk.prkngandroid.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.geocoder.service.models.GeocoderResponse;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Iterator;
import java.util.List;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.model.foursquare.FoursquareResults;
import ng.prk.prkngandroid.model.foursquare.MiniVenue;
import ng.prk.prkngandroid.util.CityBoundsHelper;

public class GeocoderAdapter extends BaseAdapter implements
        Filterable {
    private final static String TAG = "GeocoderAdapter";

    private final Context context;
    private final String mapboxToken;
    private final String foursquareClientId;
    private final String foursquareClientSecret;
    private final String foursquareVersion;

    private GeocoderFilter geocoderFilter;

    private List<GeocoderFeature> features;

    public GeocoderAdapter(Context context) {
        this.context = context;
        this.mapboxToken = context.getString(R.string.mapbox_access_token);
        this.foursquareClientId = context.getString(R.string.foursquare_client_id);
        this.foursquareClientSecret = context.getString(R.string.foursquare_client_secret);
        this.foursquareVersion = context.getString(R.string.foursquare_version);
    }

    /*
     * Required by BaseAdapter
     */

    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public GeocoderFeature getItem(int position) {
        return features.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * Get a View that displays the data at the specified position in the data set.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        } else {
            view = convertView;
        }

        // It always is a textview
        TextView text = (TextView) view;

        // Set the place name
        GeocoderFeature feature = getItem(position);
        text.setText(feature.getPlaceName());

        return view;
    }

    /*
     * Required by Filterable
     */

    @Override
    public Filter getFilter() {
        if (geocoderFilter == null) {
            geocoderFilter = new GeocoderFilter(
                    CityBoundsHelper.getCityByName(context, "montreal")
            );
        }

        return geocoderFilter;
    }

    private class GeocoderFilter extends Filter {
        private final static String TAG = "GeocoderFilter";

        private PrkngService service;
        private City city;

        public GeocoderFilter(City city) {
            this.service = ApiClient.getServiceLog();
            this.city = city;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.v(TAG, "performFiltering");
            FilterResults results = new FilterResults();

            // No constraint
            if (TextUtils.isEmpty(constraint)) {
                return results;
            }

            GeocoderResponse response = ApiClient.searchMapbox(service,
                    mapboxToken,
                    constraint.toString(),
                    city);

            if (response == null) {
                return null;
            }

            final List<GeocoderFeature> features = response.getFeatures();

            Iterator<GeocoderFeature> iterator = features.iterator();
            while (iterator.hasNext()) {
                GeocoderFeature f = iterator.next();
                if (!city.containsInRadius(new LatLng(f.getLatitude(), f.getLongitude()))) {
                    iterator.remove();
                }
            }

            FoursquareResults foursquareResponse = ApiClient.searchFoursquare(service,
                    foursquareClientId,
                    foursquareClientSecret,
                    foursquareVersion,
                    constraint.toString(),
                    city
            );

            if (foursquareResponse != null) {
                List<MiniVenue> venues = foursquareResponse.getVenues();
                for (MiniVenue venue : venues) {
                    Log.v(TAG, venue.toString());
                }
            }



            results.values = features;
            results.count = features.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.v(TAG, "publishResults");

            if (results != null && results.count > 0) {
                features = (List<GeocoderFeature>) results.values;
                Log.v(TAG, "notifyDataSet Changed");

                notifyDataSetChanged();
            } else {
                Log.v(TAG, "notifyDataSet Invalidated");

                notifyDataSetInvalidated();
            }
        }
    }
}
