package felix.com.skydrop.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.activity.MainActivity;
import felix.com.skydrop.adapter.SectionsPagerAdapter;
import felix.com.skydrop.constant.Color;
import felix.com.skydrop.constant.GeocoderConstant;
import felix.com.skydrop.constant.GlobalConstant;
import felix.com.skydrop.constant.WeatherConstant;
import felix.com.skydrop.model.ApplicationData;
import felix.com.skydrop.model.WeatherData;
import felix.com.skydrop.receiver.AddressResultReceiver;
import felix.com.skydrop.service.FetchAddressIntentService;
import felix.com.skydrop.util.ForecastConverter;

/**
 * Created by fsoewito on 11/24/2015.
 */
public class CurrentFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AddressResultReceiver.Receiver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = CurrentFragment.class.getSimpleName();

    private static final String KEY_REQUEST_LOCATION_STATE = "locationRequestState";

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    WeatherData mWeatherData;
    ApplicationData mApplicationData;
    @Bind(R.id.layout_current_weather)
    SwipeRefreshLayout mRefreshLayout;
    //component
    @Bind(R.id.labelLocation)
    TextView mAddressLabel;
    @Bind(R.id.labelTime)
    TextView mTimeLabel;
    @Bind(R.id.labelTimeProperties)
    TextView mTimeLabelProperties;
    @Bind(R.id.labelTemperature)
    TextView mTemperatureLabel;
    @Bind(R.id.imageViewWeather)
    ImageView mIconWeather;
    @Bind(R.id.labelSummary)
    TextView mSummaryLabel;
    @Bind(R.id.labelRealFeel)
    TextView mRealFeelLabel;
    @Bind(R.id.labelHumidity)
    TextView mHumidityLabel;
    @Bind(R.id.labelPrecipitation)
    TextView mPrecipitationLabel;
    @Bind(R.id.labelUvIndex)
    TextView mUvIndexLabel;
    @Bind(R.id.labelPressure)
    TextView mPressureLabel;
    @Bind(R.id.labelWindSpeed)
    TextView mWindLabel;
    @Bind(R.id.labelWindDirection)
    TextView mWindDirectionLabel;

    private Location mLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocation = false;
    private int mNRequest = 0;
    private AddressResultReceiver mResultReceiver;
    //root view
    private MainActivity mActivity;
    private View mLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "entering on create");
        super.onCreate(savedInstanceState);
        initData();
        if (savedInstanceState != null) {
            loadState(savedInstanceState);
        }
        if (checkGooglePlayServices()) {
            Log.i(TAG, "gms available");
            buildGoogleApiClient();
        }
    }

    private void loadState(Bundle savedInstanceState) {
        mRequestingLocation = savedInstanceState.getBoolean(KEY_REQUEST_LOCATION_STATE, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "entering on create view");
        mLayout = inflater.inflate(R.layout.fragment_current_weather, container, false);
        ButterKnife.bind(this, mLayout);
        initView();
        return mLayout;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "entering on start");
        super.onStart();
    }

    @Override
    public void onResume() {
        if (mResultReceiver.getReceiver() == null) {
            mResultReceiver.setReceiver(this);
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_REQUEST_LOCATION_STATE, mRequestingLocation);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "entering on pause");
        if (mResultReceiver.getReceiver() != null) {
            mResultReceiver.setReceiver(null);
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "entering on stop");
        super.onStop();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "entering on refresh");
        if (mLocation != null) {
            getForecast(mLocation.getLatitude(), mLocation.getLongitude());
            Log.i(TAG, "refresh using new data");
        } else {
            getForecast(mWeatherData.getLatitude(), mWeatherData.getLongitude());
            Log.i(TAG, "refresh using old data");
        }
    }

    protected void initData() {
        Log.i(TAG, "entering init Data");
        mActivity = (MainActivity) getActivity();
        mSectionsPagerAdapter = mActivity.getSectionsPagerAdapter();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);

        mWeatherData = mActivity.getWeatherData();
        mApplicationData = mActivity.getApplicationData();
        mNRequest = 0;
    }

    protected void initView() {
        updateDisplay();
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN);
    }

    @SuppressLint("SetTextI18n")
    protected void updateDisplay() {
        Log.i(TAG, "entering update display");
        ForecastFragment forecastFragment = (ForecastFragment) mActivity.getSupportFragmentManager()
                .findFragmentById(mSectionsPagerAdapter.getId(GlobalConstant.FORECAST_FRAGMENT_INDEX));
        if (forecastFragment != null && forecastFragment.isCreated()) {
            Log.d(TAG, "forecastFragment is created and updated");
            forecastFragment.updateDisplay();
        }
        ColorFilter filter = new LightingColorFilter(Color.BLACK, 0xFF3F51B5);
        Drawable drawable = mActivity.getResources().getDrawable(ForecastConverter.getIcon(mWeatherData.getIcon()));
        if (drawable != null) {
            drawable.setColorFilter(filter);
        }

        if (mWeatherData.isInitialized()) {
            mIconWeather.setImageDrawable(drawable);
            mTemperatureLabel.setText(ForecastConverter.getString(mWeatherData.getTemperature(), true, false));
            mAddressLabel.setText(mApplicationData.getAddress());
            String time = ForecastConverter.getString(mWeatherData.getTime(),
                    mWeatherData.getTimezone(), ForecastConverter.LONG_MODE);
            mTimeLabel.setText(time.substring(0, time.length() - 2));
            mTimeLabelProperties.setText(time.substring(time.length() - 2, time.length()));

            mSummaryLabel.setText(mWeatherData.getSummary());
            mRealFeelLabel.setText(String.format("Feels like : %s°",
                    ForecastConverter.getString(mWeatherData.getApparentTemperature(), true, false)));
            //todo get uv index or remove it :(
            mUvIndexLabel.setText("NA");
            mHumidityLabel.setText(String.format
                    ("%s %%", ForecastConverter.getString(mWeatherData.getHumidity(), true, true)));
            mPrecipitationLabel.setText(String.format
                    ("%s %%", ForecastConverter.getString(mWeatherData.getPrecipProbability(), true, true)));
            mPressureLabel.setText(String.format
                    ("%s mbar", ForecastConverter.getString(mWeatherData.getPressure(), false, false)));

            mWindLabel.setText(String.format
                    ("%s mps", ForecastConverter.getString(mWeatherData.getWindSpeed(), false, false)));
            mWindDirectionLabel.setText(ForecastConverter.getDirection(mWeatherData.getWindDirection()));
        } else {
            mAddressLabel.setText("Location NA");
            mTemperatureLabel.setText(R.string.not_available);
            mIconWeather.setImageResource(R.drawable.ic_weather_sunny);
            mTimeLabel.setText("00:00");
            mTimeLabelProperties.setText("AM");
            mSummaryLabel.setText(R.string.not_available);
            mRealFeelLabel.setText(R.string.not_available);

            mUvIndexLabel.setText(R.string.not_available);
            mHumidityLabel.setText(R.string.not_available);
            mPrecipitationLabel.setText(R.string.not_available);
            mPressureLabel.setText(R.string.not_available);
            mWindLabel.setText(R.string.not_available);
            mWindDirectionLabel.setText(R.string.not_available);
        }
        Log.i(TAG, "finish on update display");
    }

    //etc
    private void alertUserAboutError() {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(mActivity.getSupportFragmentManager(), "error_dialog");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void getForecast(double latitude, double longitude) {
        Log.i(TAG, "entering get forecast");
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            Request request = new Request.Builder().
                    url(String.format("%s/%s/%04f,%04f?units=si", WeatherConstant.url, WeatherConstant.apiKey, latitude, longitude)).
                    build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i(TAG, "entering on failure http");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(false);
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) {
                    Log.i(TAG, "entering on response http");
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, response.body().string());
                        if (response.isSuccessful()) {
                            mWeatherData.getFromJson(jsonData);
                            startIntentService();
                        } else {
                            alertUserAboutError();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                    mRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception caught", e);
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            mRefreshLayout.setRefreshing(false);
            Toast.makeText(mActivity, "Network not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.i(TAG, "entering on receive result");
        String address;
        if (resultCode == GeocoderConstant.SUCCESS_RESULT) {
            Log.i(TAG, "geocoder success");
            address = resultData.getString(GeocoderConstant.RESULT_DATA_KEY);
            mApplicationData.setAddress(address);
        } else {
            Log.i(TAG, "geocoder unsuccessful");
        }
        Log.i(TAG, "address set");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDisplay();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    protected void startIntentService() {
        Log.i(TAG, "send intent into geocoder service");
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(GeocoderConstant.RECEIVER, mResultReceiver);
        intent.putExtra(GeocoderConstant.LOCATION_DATA_EXTRA, mLocation);
        mActivity.startService(intent);
    }

    //gms
    //google play sevice
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mActivity);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    mActivity, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            return false;
        }
        return true;
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
                Toast.makeText(mActivity, "Location not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i(TAG, "availability null");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "entering on gms connected failure");
        Log.e(TAG, connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "entering on Location changed");
        mNRequest++;
        if (location != null) {
            mLocation = location;
            Log.i(TAG, "Location found");
        } else {
            Log.i(TAG, "Location not found");
            if (mNRequest > 5) {
                Toast.makeText(mActivity, "location not updated", Toast.LENGTH_SHORT).show();
            }
        }
        if ((mGoogleApiClient != null) && (mNRequest > 5)) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "gms service disconnected");
        }
        mRequestingLocation = false;
    }
}
