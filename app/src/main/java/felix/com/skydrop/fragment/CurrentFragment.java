package felix.com.skydrop.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.constant.ForecastConstant;
import felix.com.skydrop.factory.CurrentWeatherFactory;
import felix.com.skydrop.model.CurrentWeather;
import felix.com.skydrop.util.ForecastConverter;

/**
 * Created by fsoewito on 11/24/2015.
 */
public class CurrentFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ForecastConstant {
    private static final String TAG = CurrentFragment.class.getSimpleName();
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private static final String TIME_FORMAT = "HH:mm";

    double mLatitude = -6.215117;
    double mLongitude = 106.824896;

    CurrentWeather mCurrentWeather;

    //root view
    Activity mActivity;
    View mLayout;

    @Bind(R.id.layout_current_weather)
    SwipeRefreshLayout mRefreshLayout;

    //component
    @Bind(R.id.labelTemperature)
    TextView mTemperatureLabel;

    @Bind(R.id.labelTime)
    TextView mTimeLabel;

    @Bind(R.id.imageViewWeather)
    ImageView mIconWeather;

    @Bind(R.id.labelSummary)
    TextView mSummaryLabel;

    @Bind(R.id.labelRealFeel)
    TextView mRealFeelLabel;

    @Bind(R.id.labelTodaySummary)
    TextView mLabelTodaySummary;

    @Bind(R.id.labelHumidity)
    TextView mHumidityLabel;

    @Bind(R.id.labelPrecipitation)
    TextView mPrecipitationLabel;

    @Bind(R.id.labelUvIndex)
    TextView mUvIndexLabel;

    @Bind(R.id.labelWindSpeed)
    TextView mWindLabel;

    @Bind(R.id.labelWindDirection)
    TextView mWindDirectionLabel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.layout_current_weather, container, false);
        ButterKnife.bind(this, mLayout);
        initView();
        initData();
        return mLayout;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "on refresh called");
        getForecast(mLatitude, mLongitude);
    }

    protected void initData() {
        mCurrentWeather = CurrentWeatherFactory.getInstance();
        mActivity = getActivity();
    }

    protected void initView() {
        mTemperatureLabel.setText("--");
        mIconWeather.setImageResource(R.drawable.ic_weather_sunny);
        mTimeLabel.setText(new SimpleDateFormat(TIME_FORMAT).format(new Date()));
        mSummaryLabel.setText("-");
        mRealFeelLabel.setText("--");

        mLabelTodaySummary.setText("getting information..");

        mUvIndexLabel.setText(String.valueOf(8));
        mHumidityLabel.setText(String.valueOf(0.7));
        mPrecipitationLabel.setText(String.format("%d %%", 70));
        mWindLabel.setText(String.format("%d mph", 4));
        mWindDirectionLabel.setText("SSE");

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN);
    }

    protected void updateDisplay(CurrentWeather currentWeather){
        mTemperatureLabel.setText(ForecastConverter.getString(currentWeather.getTemperature(), true));
        mIconWeather.setImageResource(ForecastConverter.getIcon(currentWeather.getIcon()));
        mTimeLabel.setText(ForecastConverter.getString(currentWeather.getTime(), currentWeather.getTimezone()));
        mSummaryLabel.setText(currentWeather.getSummary());
        mRealFeelLabel.setText(ForecastConverter.getString(currentWeather.getApparentTemperature(), true));
        mLabelTodaySummary.setText(currentWeather.getTodaySummary());
        //todo get uv index or remove it :(
        mHumidityLabel.setText(ForecastConverter.getString(currentWeather.getHumidity(), true));
        mPrecipitationLabel.setText(ForecastConverter.getString(currentWeather.getPrecipProbability(), true));
        mWindLabel.setText(ForecastConverter.getString(currentWeather.getWindSpeed(), false));
        mWindDirectionLabel.setText(ForecastConverter.getString(currentWeather.getWindDirection(), true));
    }

    //etc
    private void alertUserAboutError() {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(mActivity.getFragmentManager(), "error_dialog");
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
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url(String.format("%s/%s/%04f,%04f?units=si", url, apiKey, latitude, longitude)).
                    build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, response.body().string());
                        if (response.isSuccessful()) {
                            mCurrentWeather.getFromJson(jsonData);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay(mCurrentWeather);
                                    mRefreshLayout.setRefreshing(false);
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON exception caught", e);
                    }
                }
            });
        } else {
            Toast.makeText(mActivity, "Network not available", Toast.LENGTH_LONG).show();
        }
    }
}
