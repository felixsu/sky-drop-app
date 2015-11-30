package felix.com.skydrop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import felix.com.skydrop.R;
import felix.com.skydrop.Receiver.AddressResultReceiver;
import felix.com.skydrop.constant.ForecastConstant;
import felix.com.skydrop.constant.GeocoderConstant;
import felix.com.skydrop.factory.CurrentWeatherFactory;
import felix.com.skydrop.fragment.AlertDialogFragment;
import felix.com.skydrop.fragment.CurrentFragment;
import felix.com.skydrop.model.CurrentWeather;
import felix.com.skydrop.service.FetchAddressIntentService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ForecastConstant,
        LocationListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_REQUEST_LOCATION_STATE = "locationRequestState";

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private Location mLocation;
    private String mAddressOutput;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocation = false;

    DrawerLayout mDrawer;
    FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        updateState(savedInstanceState);
        initField();
        if (checkGooglePlayServices()) {
            Log.i(TAG, "gms available");
            buildGoogleApiClient();
        }
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        CurrentFragment currentFragment = new CurrentFragment();
        fragmentTransaction.add(R.id.fragmentContainer, currentFragment);
        fragmentTransaction.commit();
    }
    private void initField(){
    }

    private void updateState(Bundle savedState){
        if (savedState != null){
            Log.i(TAG, "state not empty");
            mRequestingLocation = savedState.getBoolean(KEY_REQUEST_LOCATION_STATE, false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_REQUEST_LOCATION_STATE, mRequestingLocation);
        super.onSaveInstanceState(outState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            Toast.makeText(this, "setting pressed", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_info) {
            Toast.makeText(this, "info pressed", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //google play sevice
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            return false;
        }
        return true;
    }

    //etc
    public Location getLocation() {
        return mLocation;
    }

    public String getAddressOutput() {
        return mAddressOutput;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "entering on connected gms");
        LocationAvailability availability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (availability != null) {
            if (availability.isLocationAvailable() && !mRequestingLocation) {
                mRequestingLocation = true;
                Log.i(TAG, "finding location started");
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setMaxWaitTime(2000);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.i(TAG, "availability null");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "entering on Location changed");
        if (location != null) {
            mLocation = location;
        }else{
            Log.i(TAG, "Location not found");
        }
        mRequestingLocation = false;
    }
}
