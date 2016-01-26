package ng.prk.prkngandroid.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Collections;
import java.util.List;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.City;
import ng.prk.prkngandroid.util.CityBoundsHelper;

public class CitiesDialog extends DialogFragment {
    private static final String TAG = "AvailableCitiesDialog ";

    private List<City> mCities;

    public static CitiesDialog newInstance(LatLng latLng) {
        final CitiesDialog dialog = new CitiesDialog();
        dialog.setCancelable(false);

        final Bundle bundle = new Bundle();
        bundle.putDouble(Const.BundleKeys.LATITUDE, latLng.getLatitude());
        bundle.putDouble(Const.BundleKeys.LONGITUDE, latLng.getLongitude());
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCities = CityBoundsHelper.getSupportedCities(getContext(),
                getLatLng(getArguments()));
        Collections.sort(mCities);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PrkngDialogStyle);
        builder.setTitle(R.string.dialog_cities_title)
                .setIcon(R.drawable.ic_city)
                .setItems(getCityNames(mCities), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final City city = mCities.get(which);
                        Log.v(TAG, "selected city: " + city);
                        if (getTargetFragment() != null) {
                            final Intent intent = new Intent();

                            final Bundle bundle = new Bundle();
                            bundle.putDouble(Const.BundleKeys.LATITUDE, city.getLatLng().getLatitude());
                            bundle.putDouble(Const.BundleKeys.LONGITUDE, city.getLatLng().getLongitude());
                            intent.putExtras(bundle);

                            getTargetFragment().onActivityResult(
                                    Const.RequestCodes.CITY_SELECTOR,
                                    Activity.RESULT_OK,
                                    intent);
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();

        return alertDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    private static String[] getCityNames(List<City> cities) {
        if (cities == null || cities.size() == 0) {
            return null;
        }

        final int nbCities = cities.size();
        String[] names = new String[nbCities];
        for (int i = 0; i < nbCities; i++) {
            names[i] = cities.get(i).getAreaName();
        }

        return names;
    }

    private static LatLng getLatLng(Bundle bundle) {
        return new LatLng(
                bundle.getDouble(Const.BundleKeys.LATITUDE),
                bundle.getDouble(Const.BundleKeys.LONGITUDE)
        );
    }


}
