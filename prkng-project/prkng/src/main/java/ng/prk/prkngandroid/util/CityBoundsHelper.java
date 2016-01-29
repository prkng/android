package ng.prk.prkngandroid.util;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.City;

public class CityBoundsHelper {
    private final static String TAG = "CityBoundsHelper";

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

    public static City getNearestCity(Context context, LatLng latLng) {
        final List<City> cities = getSupportedCities(context, latLng);

        Collections.sort(cities);
        return cities.get(0);
    }

    public static City getCityByName(Context context, @NonNull String city) {
        final List<City> cities = getSupportedCities(context);

        for (City c : cities) {
            if (city.equals(c.getName())) {
                return c;
            }
        }

        return null;
    }

    public static boolean isValidLocation(Context context, Location location) {
        if (location == null) {
            return false;
        }

        final LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());

        return CityBoundsHelper
                .getNearestCity(context, latLng)
                .containsInRadius(latLng);
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
