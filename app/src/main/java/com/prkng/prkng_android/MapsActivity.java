package com.prkng.prkng_android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.MapFragment;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.annotations.Sprite;
import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.MapView.OnMapChangedListener;

public class MapsActivity extends FragmentActivity {

    private MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setAccessToken("pk.eyJ1IjoiYXJuYXVkc3B1aGxlciIsImEiOiJSaEctSlVnIn0.R8cfngN9KkHYZx54JQdgJA");
        mapView.setStyleUrl("mapbox://styles/arnaudspuhler/cifpv67st000185m0qjvgw3d6");
        mapView.setCenterCoordinate(new LatLng(45.501689, -73.567256));
        mapView.setZoomLevel(14);
        mapView.onCreate(savedInstanceState);

        mapView.addOnMapChangedListener(new MyOnMapChangedListener());
        //add a test marker
        Drawable markerDrawable = getResources().getDrawable(R.drawable.ic_button_line_inactive3x);
        Sprite sprite = new SpriteFactory(mapView).fromDrawable(markerDrawable);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(45.501689, -73.567256));
        markerOptions.icon(sprite);
        mapView.addMarker(markerOptions);

        //add a test polyline
        int lineBlue = Color.parseColor("#5B717F");
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(lineBlue);
        polylineOptions.add(new LatLng(45.5015, -73.5671));
        polylineOptions.add(new LatLng(45.5017, -73.5674));
        mapView.addPolyline(polylineOptions);
    }

    private class MyOnMapChangedListener implements MapView.OnMapChangedListener {

        @Override
        public void onMapChanged(int change) {
            if (change < 5) {
                System.out.println(change);
            }

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void zoomOutBtnClicked(View v) {
        try {
            double zoomLevel = mapView.getZoomLevel();
            mapView.setZoomLevel(zoomLevel - 1, true);
        } catch (Exception e) {

        }
    }

    public void zoomInBtnClicked(View v) {
        try {
            double zoomLevel = mapView.getZoomLevel();
            mapView.setZoomLevel(zoomLevel+1, true);
        } catch (Exception e) {

        }
    }

    double radius() {
        //get a top corner of the map and calculate the meters from the center
        LatLng center =  mapView.getCenterCoordinate();
        LatLng topLeft = mapView.fromScreenLocation(new PointF(0, 0));
        double meters = center.distanceTo(topLeft);
        return meters;
    }

}
