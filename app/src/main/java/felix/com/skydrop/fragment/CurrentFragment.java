package felix.com.skydrop.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
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
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.constant.Color;
import felix.com.skydrop.constant.ForecastConstant;
import felix.com.skydrop.factory.CurrentWeatherFactory;
import felix.com.skydrop.model.CurrentWeather;
import felix.com.skydrop.model.HourlyForecast;
import felix.com.skydrop.util.ForecastConverter;

/**
 * Created by fsoewito on 11/24/2015.
 */
public class CurrentFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ForecastConstant, Color {
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

    @Bind(R.id.labelGraphViewEmpty)
    TextView mGraphViewEmptyLabel;


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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurrentWeather.isInitialized()) {
            SharedPreferences preferences = mActivity.getSharedPreferences(FORECAST_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            storePref(preferencesEditor);
        }
    }

    private void storePref(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_IS_INIT, true);
        preferencesEditor.putString(KEY_CURRENT_WEATHER, mCurrentWeather.toJson());
        preferencesEditor.commit();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "on refresh called");
        getForecast(mLatitude, mLongitude);
    }

    protected void initData() {
        mCurrentWeather = CurrentWeatherFactory.getInstance();
        mActivity = getActivity();
        SharedPreferences preferences = mActivity.getSharedPreferences(FORECAST_PREFERENCES, Context.MODE_PRIVATE);
        if (preferences.getBoolean(KEY_IS_INIT, false)) {
            try {
                mCurrentWeather.getFromJson(preferences.getString(KEY_CURRENT_WEATHER, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void initView() {
        updateDisplay(mCurrentWeather);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(RED, BLUE, YELLOW, GREEN);
    }

    private void initGraph() {
        mGraphView.removeAllSeries();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));

        mHourlyApparentTempSeries.setColor(0xFF3F51B5);
        mHourlyApparentTempSeries.setThickness(12);
        mHourlyApparentTempSeries.setDrawDataPoints(true);
        mHourlyApparentTempSeries.setDataPointsRadius(12f);
        mHourlyApparentTempSeries.setCustomPaint(paint);
        mHourlyApparentTempSeries.setTitle("Real Feel");

        mHourlyTempSeries.setColor(0xFFFF4081);
        mHourlyTempSeries.setThickness(12);
        mHourlyTempSeries.setDrawDataPoints(true);
        mHourlyTempSeries.setDataPointsRadius(12f);
        mHourlyTempSeries.setTitle("Actual");

        GridLabelRenderer gridRenderer = mGraphView.getGridLabelRenderer();
        gridRenderer.setGridStyle(GridStyle.HORIZONTAL);
        gridRenderer.setGridColor(R.color.greyWhiteMedium);
        gridRenderer.setNumHorizontalLabels(6);
        gridRenderer.setNumVerticalLabels(4);
        gridRenderer.setHighlightZeroLines(true);

        Viewport viewport = mGraphView.getViewport();
        viewport.setMaxY(35);
        viewport.setMinY(20);
        viewport.setYAxisBoundsManual(true);

        mGraphView.addSeries(mHourlyTempSeries);
        mGraphView.addSeries(mHourlyApparentTempSeries);
        mGraphView.setScaleX(1);
        mGraphView.setScaleY(1);
        mGraphView.setTitle("Temperature");
        mGraphView.getLegendRenderer().setVisible(true);
        mGraphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        mGraphView.getLegendRenderer().setBackgroundColor(android.R.color.transparent);
        mGraphView.getLegendRenderer().setTextColor(R.color.greyWhiteDark);
    }

    protected void updateDisplay(CurrentWeather currentWeather) {
        ColorFilter filter = new LightingColorFilter(BLACK, 0xFF3F51B5);
        Drawable drawable = mActivity.getDrawable(ForecastConverter.getIcon(currentWeather.getIcon()));
        if (drawable != null) {
            drawable.setColorFilter(filter);
        }

        if (currentWeather.isInitialized()) {
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

            HourlyForecast[] hourlyForecasts = mCurrentWeather.getHourlyForecasts();
            DataPoint[] hourlyTemp = new DataPoint[CurrentWeather.FORECAST_DISPLAYED];
            DataPoint[] hourlyApparentTemp = new DataPoint[CurrentWeather.FORECAST_DISPLAYED];
            for (int i = 0; i < CurrentWeather.FORECAST_DISPLAYED; i++) {
                hourlyTemp[i] = new DataPoint(i, Math.round(hourlyForecasts[i].getTemperature()));
                hourlyApparentTemp[i] = new DataPoint(i, Math.round(hourlyForecasts[i].getApparentTemperature()));
                Log.d(TAG, String.valueOf(Math.round(hourlyForecasts[i].getTemperature())));
            }
            mHourlyTempSeries = new LineGraphSeries<>(hourlyTemp);
            mHourlyApparentTempSeries = new LineGraphSeries<>(hourlyApparentTemp);
            initGraph();
            mGraphView.setVisibility(View.VISIBLE);
            mGraphViewEmptyLabel.setVisibility(View.GONE);
        } else {
            mTemperatureLabel.setText(R.string.not_available);
            mIconWeather.setImageResource(R.drawable.ic_weather_sunny);
            mTimeLabel.setText("00:00");
            mTimeLabelProperties.setText("AM");
            mSummaryLabel.setText(R.string.not_available);
            mRealFeelLabel.setText(R.string.not_available);

            mLabelTodaySummary.setText("please refresh.....");

            mUvIndexLabel.setText(R.string.not_available);
            mHumidityLabel.setText(R.string.not_available);
            mPrecipitationLabel.setText(R.string.not_available);
            mWindLabel.setText(R.string.not_available);
            mWindDirectionLabel.setText(R.string.not_available);

            mGraphView.setVisibility(View.GONE);
            mGraphViewEmptyLabel.setVisibility(View.VISIBLE);
        }
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
