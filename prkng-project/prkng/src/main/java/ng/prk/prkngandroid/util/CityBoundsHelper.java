package ng.prk.prkngandroid.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.model.MultiPolygonsGeoJSONFeature;
import ng.prk.prkngandroid.model.MultiPolygonsGeoJSONFeatureGeometry;
import ng.prk.prkngandroid.model.MultiPolygonsGeoJson;

public class CityBoundsHelper {
    private final static String TAG = "CityBoundsHelper";

    /**
     * Adds North-West / South-East polygon points. This allows to invert the polygon
     *
     * @param polygonOptions
     * @return
     */
    private static PolygonOptions addBigBox(PolygonOptions polygonOptions) {
        polygonOptions
                .add(new LatLng(-90, -180)) // NW
                .add(new LatLng(-90, 180)) // NE
                .add(new LatLng(90, 180)) // SE
                .add(new LatLng(90, -180)) // SW
                .add(new LatLng(-90, -180)) // NW
        ;

        return polygonOptions;
    }

    private static MultiPolygonsGeoJson getAreas(Context context) {
        final String json = loadJSONFromAsset(context, context.getString(R.string.asset_areas));

        return new Gson().fromJson(json, MultiPolygonsGeoJson.class);
    }

    public static List<PolygonOptions> getAreaPolygonOptions(Context context, LatLng latLng) {
        final City nearestCity = getNearestCity(context, latLng);

        MultiPolygonsGeoJSONFeatureGeometry areaGeometry = CityBoundsHelper.getAreaGeometry(context, nearestCity.getName());

        List<PolygonOptions> polygonOptionsList = new ArrayList<>();

        try {
            final List<List<List<List<Double>>>> MultiPolygons = areaGeometry.getCoordinates();
            for (List<List<List<Double>>> Polygon : MultiPolygons) {
                boolean exteriorRing = true;
                for (List<List<Double>> LinearRings : Polygon) {
                    PolygonOptions polygonOptions = new PolygonOptions()
                            .fillColor(ContextCompat.getColor(context, R.color.cream1))
                            .strokeColor(ContextCompat.getColor(context, R.color.red1))
                            .alpha(0.9f);
                    if (exteriorRing) {
                        polygonOptions = addBigBox(polygonOptions);
                        exteriorRing = false;
                    }
                    for (List<Double> ring : LinearRings) {
                        polygonOptions.add(new LatLng(ring.get(1), ring.get(0)));
                    }
                    polygonOptionsList.add(polygonOptions);
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return polygonOptionsList;
    }

    private static MultiPolygonsGeoJSONFeatureGeometry getAreaGeometry(Context context, String city) {
        final MultiPolygonsGeoJson geoJson = getAreas(context);
        final List<MultiPolygonsGeoJSONFeature> features = geoJson.getFeatures();

        for (MultiPolygonsGeoJSONFeature feature : features) {
            if (city.equals(feature.getProperties().getName())) {
                return feature.getGeometry();
            }
        }
        return null;
    }

    private static String loadJSONFromAsset(Context context, String asset) {
        try {
            InputStream is = context.getAssets().open(asset);

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static City getNearestCity(Context context, LatLng latLng) {
        final List<City> cities = getSupportedCities(context, latLng);

        return cities != null ? cities.get(0) : null;
//
//        City nearestCity = null;
//        double minDistance = Double.MAX_VALUE;
//        for (City city : cities) {
//            double distance = city.getLatLng().distanceTo(latLng);
//            if (Double.compare(distance, minDistance) < 0) {
//                minDistance = distance;
//                nearestCity = city;
//            }
//        }
//
//        return nearestCity;
    }

    public static List<City> getSupportedCities(Context context) {
        return getSupportedCities(context, null);
    }

    public static List<City> getSupportedCities(Context context, LatLng latLng) {
        final String json = loadJSONFromAsset(context, context.getString(R.string.asset_cities));

        final Type listType = new TypeToken<List<City>>() {
        }.getType();
        List<City> cities = new Gson().fromJson(json, listType);

        if (cities != null && latLng != null) {
            for (City city : cities) {
                city.setDistanceTo(city.getLatLng().distanceTo(latLng));
            }
        }


        return cities;
    }
}
