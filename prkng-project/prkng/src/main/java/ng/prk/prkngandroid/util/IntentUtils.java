package ng.prk.prkngandroid.util;

import android.content.Intent;
import android.net.Uri;

import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;

public class IntentUtils implements Const.AppsIntents {
    private static final String TAG = "IntentUtils";

    public static Intent getDirectionsIntent(LatLng origin, LatLng destination) {
        if (destination == null) {
            return null;
        }

        final String sDestination = destination.getLatitude() + "," + destination.getLongitude();
        final String sOrigin = (origin != null ?
                origin.getLatitude() + "," + origin.getLongitude() : "");

        final Uri uri = Uri.parse(String.format(GOOGLE_MAPS,
                sOrigin,
                sDestination));
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent getNavigationIntent(LatLng destination) {
        if (destination == null) {
            return null;
        }

        final Uri uri = Uri.parse(String.format(GOOGLE_NAVIGATION,
                destination.getLatitude(), destination.getLongitude()));
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
