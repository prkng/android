package ng.prk.prkngandroid.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.Sprite;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.R;

public class MapAssets {
    private int lineColorFree;
    private int lineWidth;
    private int lineColorPaid;
    private Sprite markerIconFree;
    private Sprite markerIconPaid;

    public MapAssets(MapView mapView) {
        final Context context = mapView.getContext();
        markerIconPaid = mapView.getSpriteFactory().fromResource(R.drawable.ic_spot_paid);
        markerIconFree = mapView.getSpriteFactory().fromResource(R.drawable.ic_spot_free);

        lineColorPaid = ContextCompat.getColor(context, R.color.map_line_paid_spot);
        lineColorFree = ContextCompat.getColor(context, R.color.map_line_free_spot);
        lineWidth = context.getResources().getDimensionPixelSize(R.dimen.map_line_width);
    }

    public int getLineColorFree() {
        return lineColorFree;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public int getLineColorPaid() {
        return lineColorPaid;
    }

    public Sprite getMarkerIconFree() {
        return markerIconFree;
    }

    public Sprite getMarkerIconPaid() {
        return markerIconPaid;
    }

}
