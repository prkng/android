package ng.prk.prkngandroid.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.mapbox.mapboxsdk.views.MapView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.PrkngService;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.ui.fragment.MainMapFragment;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MapView.OnMapChangedListener {
    private final static String TAG = "Main";

//    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                new ApiConnectionTask().execute();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(Const.FragmentTags.MAP);
            if (fragment == null) {
                fragment = MainMapFragment.newInstance();
                fm.beginTransaction()
                        .replace(R.id.content_frame, fragment, Const.FragmentTags.MAP)
                        .commit();
            }
        }

//        createMapIfNecessary(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //    private void createMapIfNecessary(Bundle savedInstanceState) {
//        if (mapView == null) {
//            mapView = (MapView) findViewById(R.id.mapview);
//            mapView.setCenterCoordinate(new LatLng(45.501689, -73.567256));
//            mapView.setZoomLevel(14);
//            mapView.onCreate(savedInstanceState);
//
////            mapView.addOnMapChangedListener(this);
//        }
//    }
//
    @Override
    public void onMapChanged(int change) {
//        Log.v(TAG, "onMapChanged @ " + change);
//        if (change == MapView.DID_FINISH_RENDERING_FRAME_FULLY_RENDERED) {
//            Snackbar.make(mapView, "Map region DID change", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
//        }
    }

    private class ApiConnectionTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Log.v(TAG, "doInBackground");

            OkHttpClient client = new OkHttpClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Const.ApiPaths.BASE_URL)
                    .client(client)
//                   .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.v(TAG, "AAA");

            PrkngService service = retrofit.create(PrkngService.class);


            Log.v(TAG, "BBB");
            try {
                Response<LoginObject> login = service.loginTest("mudar@prk.ng", "mudar123").execute();
                Log.v(TAG, "CCC");

                Log.v(TAG, "email = " + login.body().getEmail());

                Response<Object> spots = service.getParkingSpots(login.body().getApikey(),
                        45.501689f,
                        -73.567256f,
                        100,
                        4,
                        "2015-11-04T08:47",
                        false,
                        false).execute();

                Log.v(TAG, "DDD");
                Log.v(TAG, "toString = " + spots.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

        }
    }
}
