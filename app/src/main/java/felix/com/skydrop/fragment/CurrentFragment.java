package felix.com.skydrop.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

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

    double mLatitude = -6.215117;
    double mLongitude = 106.824896;

    CurrentWeather mCurrentWeather;

    //root view
    private Activity mActivity;
    private View mLayout;
    private LineGraphSeries<DataPoint> mHourlyTempSeries;
    private LineGraphSeries<DataPoint> mHourlyApparentTempSeries;

    @Bind(R.id.layout_current_weather)
    SwipeRefreshLayout mRefreshLayout;

    //component
    @Bind(R.id.labelTemperature)
    TextView mTemperatureLabel;

    @Bind(R.id.labelTime)
    TextView mTimeLabel;

    @Bind(R.id.labelTimeProperties)
    TextView mTimeLabelProperties;

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

    @Bind(R.id.graphHourly)
    GraphView mGraphView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.layout_current_weather, container, false);
        ButterKnife.bind(this, mLayout);
        initData();
        initView();
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

        DataPoint[] hourlyTemp = new DataPoint[6];
        DataPoint[] hourlyApparentTemp = new DataPoint[6];
        for(int i = 0; i< hourlyTemp.length; i++){
            if (i%2 == 0) {
                hourlyTemp[i] = new DataPoint(i, 29);
                hourlyApparentTemp[i] = new DataPoint(i, 33);
            }else{
                hourlyTemp[i] = new DataPoint(i, 31);
                hourlyApparentTemp[i] = new DataPoint(i, 35);
            }
        }
        mHourlyTempSeries = new LineGraphSeries<>(hourlyTemp);
        mHourlyApparentTempSeries = new LineGraphSeries<>(hourlyApparentTemp);
    }

    protected void initView() {
        mTemperatureLabel.setText("--");
        mIconWeather.setImageResource(R.drawable.ic_weather_sunny);
        mTimeLabel.setText("--");
        mTimeLabelProperties.setText("AM");
        mSummaryLabel.setText("-");
        mRealFeelLabel.setText("--");

        mLabelTodaySummary.setText("please refresh.....");

        mUvIndexLabel.setText(String.valueOf(0));
        mHumidityLabel.setText(String.valueOf(0));
        mPrecipitationLabel.setText(String.format("%d %%", 0));
        mWindLabel.setText(String.format("%d mps", 0));
        mWindDirectionLabel.setText("-");

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN);
        initGraph();
    }

    private void initGraph(){
        mHourlyApparentTempSeries.setColor(0xFF303F9F);
        mHourlyApparentTempSeries.setThickness(12);
        mHourlyApparentTempSeries.setDrawDataPoints(true);
        mHourlyApparentTempSeries.setDataPointsRadius(15f);

        mHourlyTempSeries.setColor(0xFF3F51B5);
        mHourlyTempSeries.setThickness(15);
        mHourlyTempSeries.setDrawDataPoints(true);
        mHourlyTempSeries.setDataPointsRadius(20f);

        GridLabelRenderer gridRenderer = mGraphView.getGridLabelRenderer();
        mGraphView.addSeries(mHourlyTempSeries);
        mGraphView.addSeries(mHourlyApparentTempSeries);
        mGraphView.setScaleX(1);
        mGraphView.setScaleY(1);
    }

    protected void updateDisplay(CurrentWeather currentWeather) {
        ColorFilter filter = new LightingColorFilter(Color.BLACK, 0xFF3F51B5);
        Drawable drawable = mActivity.getDrawable(ForecastConverter.getIcon(currentWeather.getIcon()));
        if (drawable != null) {
            drawable.setColorFilter(filter);
        }
        mIconWeather.setImageDrawable(drawable);
        mTemperatureLabel.setText(ForecastConverter.getString(currentWeather.getTemperature(), true, false));

        String time = ForecastConverter.getString(currentWeather.getTime(), currentWeather.getTimezone());
        mTimeLabel.setText(time.substring(0, time.length() - 2));
        mTimeLabelProperties.setText(time.substring(time.length() - 2, time.length()));

        mSummaryLabel.setText(currentWeather.getSummary());
        mRealFeelLabel.setText(String.format("Real Feel : %s",
                ForecastConverter.getString(currentWeather.getApparentTemperature(), true, false)));
        mLabelTodaySummary.setText(
                currentWeather.getTodaySummary());
        //todo get uv index or remove it :(
        mHumidityLabel.setText(String.format
                ("%s %%", ForecastConverter.getString(currentWeather.getHumidity(), true, true)));
        mPrecipitationLabel.setText(String.format
                ("%s %%", ForecastConverter.getString(currentWeather.getPrecipProbability(), true, true)));
        mWindLabel.setText(String.format
                ("%s mps", ForecastConverter.getString(currentWeather.getWindSpeed(), false, false)));
        mWindDirectionLabel.setText(ForecastConverter.getDirection(currentWeather.getWindDirection()));
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
