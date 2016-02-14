package ng.prk.prkngandroid.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.model.base.SearchItem;
import ng.prk.prkngandroid.model.foursquare.FoursquareResults;
import ng.prk.prkngandroid.model.foursquare.MiniVenue;
import ng.prk.prkngandroid.model.mapbox.Feature;
import ng.prk.prkngandroid.model.mapbox.MapboxResults;
import ng.prk.prkngandroid.util.CityBoundsHelper;

public class GeocoderAdapter extends BaseAdapter implements
        Filterable {
    private final static String TAG = "GeocoderAdapter";

    private final Context context;
    private final GeocoderFilter geocoderFilter;
    private List<SearchItem> features;

    private final String mapboxToken;
    private final String foursquareClientId;
    private final String foursquareClientSecret;

    private final String foursquareVersion;

    public GeocoderAdapter(Context context, LatLng proximity) {
        this.context = context;
        this.features = new ArrayList<>();
        this.geocoderFilter = new GeocoderFilter(proximity,
                CityBoundsHelper.getNearestCity(context, proximity));

        this.mapboxToken = context.getString(R.string.mapbox_access_token);
        this.foursquareClientId = context.getString(R.string.foursquare_client_id);
        this.foursquareClientSecret = context.getString(R.string.foursquare_client_secret);
        this.foursquareVersion = context.getString(R.string.foursquare_version);
    }

    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public SearchItem getItem(int position) {
        if (features != null && position < getCount()) {
            return features.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_search, parent, false);
        } else {
            view = convertView;
        }
        final TextView vTitle = (TextView) view.findViewById(R.id.title);
        final TextView vSubtitle = (TextView) view.findViewById(R.id.subtitle);

        // Set the place name and address
        SearchItem feature = getItem(position);
        vTitle.setText(feature.getName());
        if (!TextUtils.isEmpty(feature.getAddress())) {
            vSubtitle.setText(feature.getAddress());
        }

        return view;
    }

    /**
     * Implements Filterable
     * Returns a filter that can be used to constrain data with a filtering pattern
     */
    @Override
    public Filter getFilter() {
        return geocoderFilter;
    }

    private class GeocoderFilter extends Filter {
        private final static String TAG = "GeocoderFilter";

        private PrkngService service;
        private LatLng proximity;
        private City city;

        public GeocoderFilter(LatLng proximity, City nearestCity) {
            this.service = ApiClient.getService();
            this.proximity = proximity;
            this.city = nearestCity;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            // No constraint
            if (TextUtils.isEmpty(constraint)) {
                return results;
            }
            final String query = constraint.toString();

            List<Feature> mapboxFeatures = getMapboxItems(query);
            List<MiniVenue> foursquareVenues = getFoursquareItems(query);

            List<SearchItem> merged = new ArrayList<>();
            merged.addAll(mapboxFeatures);
            merged.addAll(foursquareVenues);

            merged = sortUniqueResult(merged, 50);

            results.values = merged;
            results.count = merged.size();
            return results;
        }

        /**
         * Search Mapbox API
         * Items are removed if not in city's radius. Distance (from proximity latLng) is computed
         * to allow for later sort-by-distance
         *
         * @param query
         * @return
         */
        private List<Feature> getMapboxItems(String query) {
            final MapboxResults results = ApiClient.searchMapbox(service,
                    mapboxToken,
                    query,
                    proximity,
                    city);

            List<Feature> mapboxFeatures = new ArrayList<>();
            if (results != null) {
                mapboxFeatures = results.getFeatures();

                Iterator<Feature> iterator = mapboxFeatures.iterator();
                while (iterator.hasNext()) {
                    Feature f = iterator.next();
                    if (!city.containsInRadius(f.getLatLng())) {
                        iterator.remove();
                    } else {
                        f.setDistance(city.getLatLng().distanceTo(f.getLatLng()));
                    }
                }
            }

            return mapboxFeatures;
        }

        /**
         * Search Foursquare API
         * Venues without an address are removed. Typically these are non-places (ex: planes!)
         *
         * @param query
         * @return
         */
        private List<MiniVenue> getFoursquareItems(String query) {
            final FoursquareResults results = ApiClient.searchFoursquare(service,
                    foursquareClientId,
                    foursquareClientSecret,
                    foursquareVersion,
                    query,
                    proximity,
                    city
            );

            List<MiniVenue> foursquareVenues = new ArrayList<>();
            if (results != null) {
                foursquareVenues = results.getVenues();

                Iterator<MiniVenue> iterator = foursquareVenues.iterator();
                while (iterator.hasNext()) {
                    MiniVenue f = iterator.next();
                    if (TextUtils.isEmpty(f.getAddress())) {
                        // Remove items without address (wrong category)
                        iterator.remove();
                    }
                }
            }

            return foursquareVenues;
        }

        /**
         * Sort the merged list by distance from proximity latLng.
         * Duplicate items are removed: same if distance <= 50m
         *
         * @param merged
         * @param distance
         * @return
         */
        private List<SearchItem> sortUniqueResult(List<SearchItem> merged, double distance) {
            if (merged.size() >= 2) {
                Collections.sort(merged);

                Iterator<SearchItem> iterator = merged.iterator();
                SearchItem previous = iterator.next();

                while (iterator.hasNext()) {
                    SearchItem current = iterator.next();
                    if (Double.compare(previous.distanceTo(current), distance) < 0) {
                        // Items that are less than 50m (example) away are duplicates, removed
                        iterator.remove();
                    } else {
                        previous = current;
                    }
                }
            }

            return merged;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                features = (List<SearchItem>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
