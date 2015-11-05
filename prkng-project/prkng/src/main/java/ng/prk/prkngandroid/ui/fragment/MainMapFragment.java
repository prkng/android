package ng.prk.prkngandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.R;

public class MainMapFragment extends Fragment {

    private MapView mapView;

    public static MainMapFragment newInstance() {
        return new MainMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        createMapIfNecessary(view, savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void createMapIfNecessary(View view, Bundle savedInstanceState) {
        if (mapView == null) {
            mapView = (MapView) view.findViewById(R.id.mapview);

            mapView.setCenterCoordinate(new LatLng(45.501689, -73.567256));
            mapView.setZoomLevel(14);
            mapView.onCreate(savedInstanceState);
//            mapView.addOnMapChangedListener(this);
        }
    }
}
