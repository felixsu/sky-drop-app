package felix.com.skydrop.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

import felix.com.skydrop.R;
import felix.com.skydrop.adapter.SectionsPagerAdapter;
import felix.com.skydrop.constant.ApplicationDataConstant;
import felix.com.skydrop.constant.WeatherConstant;
import felix.com.skydrop.constant.SettingConstant;
import felix.com.skydrop.factory.ApplicationDataFactory;
import felix.com.skydrop.factory.WeatherFactory;
import felix.com.skydrop.fragment.InfoDialogFragment;
import felix.com.skydrop.model.ApplicationData;
import felix.com.skydrop.model.WeatherData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_REQUEST_LOCATION_STATE = "locationRequestState";

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private Location mLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocation = false;

    //view
    private DrawerLayout mDrawer;
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //data
    private WeatherData mWeatherData;
    private ApplicationData mApplicationData;

    public WeatherData getWeatherData() {
        return mWeatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        mWeatherData = weatherData;
    }

    public ApplicationData getApplicationData() {
        return mApplicationData;
    }

    public void setApplicationData(ApplicationData applicationData) {
        mApplicationData = applicationData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initField();
        updateState(savedInstanceState);
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
        mSectionsPagerAdapter = new SectionsPagerAdapter(mFragmentManager);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    private void initField(){
        SharedPreferences applicationData = getSharedPreferences(ApplicationDataConstant.KEY, Context.MODE_PRIVATE);
        SharedPreferences settingData = getSharedPreferences(SettingConstant.KEY, Context.MODE_PRIVATE);
        SharedPreferences weatherData = getSharedPreferences(WeatherConstant.KEY, Context.MODE_PRIVATE);

        try {
            mApplicationData = ApplicationDataFactory.getInstance(applicationData);
            if (mApplicationData.isInitialized()) {
                mWeatherData = WeatherFactory.getInstance(weatherData);
            } else {
                mWeatherData = WeatherFactory.getInstance();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parsing error");
        }
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

        long time = System.currentTimeMillis();
        String currentHourString = new SimpleDateFormat("hh").format(new Date(time));
        int currentHour = Integer.parseInt(currentHourString);
        LinearLayout navBarHeaderContainer = (LinearLayout) mDrawer.findViewById(R.id.navBarHeaderContainer);
        if (navBarHeaderContainer != null) {
            Log.i(TAG, "header not null");
            if (currentHour > 0 && currentHour < 6) {
                navBarHeaderContainer.setBackgroundResource(R.drawable.head_paper_dawn);
            } else if (currentHour >= 6 && currentHour < 18) {
                navBarHeaderContainer.setBackgroundResource(R.drawable.head_paper_sunny);
            } else {
                navBarHeaderContainer.setBackgroundResource(R.drawable.head_paper_dawn);
            }
        }else{
            Log.i(TAG, "header null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor applicationDataEditor = getSharedPreferences(ApplicationDataConstant.KEY, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor settingDataEditor = getSharedPreferences(SettingConstant.KEY, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor weatherDataEditor = getSharedPreferences(WeatherConstant.KEY, Context.MODE_PRIVATE).edit();

        applicationDataEditor.putBoolean(ApplicationDataConstant.INIT, getApplicationData().isInitialized()).apply();
        weatherDataEditor.putString(WeatherConstant.KEY_CURRENT_WEATHER, getWeatherData().toJson()).apply();
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
            Toast.makeText(this, "Setting Pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_refresh){
            Toast.makeText(this, "Refresh Pressed", Toast.LENGTH_SHORT).show();
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
            showInfo();
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

    private void showInfo() {
        InfoDialogFragment dialogFragment = new InfoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "info_dialog");
    }

    //gms
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
