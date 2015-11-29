package felix.com.skydrop.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
 *
 */
public class CurrentFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ForecastConstant, Color {
    private static final String TAG = CurrentFragment.class.getSimpleName();

    private static final String CHART_MODE_KEY = "chart_mode";
    private static final int CHART_TEMP_MODE = 0;
    private static final int CHART_PRECIP_MODE = 1;

    double mLatitude = -6.215117;
    double mLongitude = 106.824896;

    CurrentWeather mCurrentWeather;
    HashMap<String, Integer> mState;

    //root view
    private Activity mActivity;
    private View mLayout;

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
    LineChart mLineChart;

    @Bind(R.id.labelGraphViewEmpty)
    TextView mLineChartEmptyLabel;


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
            preferencesEditor.putBoolean(KEY_IS_INIT, true);
            preferencesEditor.putString(KEY_CURRENT_WEATHER, mCurrentWeather.toJson());
            preferencesEditor.apply();
        }
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "on refresh called");
        getForecast(mLatitude, mLongitude);
    }

    protected void initData() {
        mCurrentWeather = CurrentWeatherFactory.getInstance();
        mActivity = getActivity();
        mState = new HashMap<>();
        mState.put(CHART_MODE_KEY, CHART_TEMP_MODE);
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

        mLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleState();
                drawChart();
            }
        });
    }

    private void drawChart() {
        HourlyForecast[] datas = mCurrentWeather.getHourlyForecasts();
        //init label
        List<String> xVal = new ArrayList<>();
        for (int i = 0; i < CurrentWeather.FORECAST_DISPLAYED; i++) {
            xVal.add(ForecastConverter.getString(
                    datas[i].getTime(), mCurrentWeather.getTimezone(),
                    ForecastConverter.SHORT_MODE));
        }

        //init Line
        List<Entry> actualTempList = new ArrayList<>();
        List<Entry> apparentTempList = new ArrayList<>();
        List<Entry> precipProbList = new ArrayList<>();

        double minYAxisVal = 200;
        double maxYAxisVal = -100;
        for (int i = 0; i < CurrentWeather.FORECAST_DISPLAYED; i++) {
            actualTempList.add(new Entry((float) Math.round(datas[i].getTemperature()), i));
            apparentTempList.add(new Entry((float) Math.round(datas[i].getApparentTemperature()), i));
            precipProbList.add(new Entry((float) (datas[i].getPrecipProbability() * 100), i));
        }

        LineDataSet actualTempDataSet = new LineDataSet(actualTempList, "actual temp");
        setUpDataSet(actualTempDataSet, PRIMARY_COLOR);
        LineDataSet apparentTempDataSet = new LineDataSet(apparentTempList, "apparent temp");
        setUpDataSet(apparentTempDataSet, ACCENT_COLOR);
        LineDataSet precipProbDataSet = new LineDataSet(precipProbList, "rain chance");
        setUpDataSet(precipProbDataSet, PRIMARY_COLOR);

        List<LineDataSet> dataSets = new ArrayList<>();
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            for (int i = 0; i < CurrentWeather.FORECAST_DISPLAYED; i++) {
                double val = mCurrentWeather.getHourlyForecasts()[i].getTemperature();
                double val2 = mCurrentWeather.getHourlyForecasts()[i].getApparentTemperature();
                if (minYAxisVal > val) {
                    minYAxisVal = val;
                }
                if (minYAxisVal > val2) {
                    minYAxisVal = val2;
                }
                if (maxYAxisVal < val) {
                    maxYAxisVal = val;
                }
                if (maxYAxisVal < val2) {
                    maxYAxisVal = val2;
                }
            }
            dataSets.add(actualTempDataSet);
            dataSets.add(apparentTempDataSet);
        } else {
            minYAxisVal = 1;
            maxYAxisVal = 99;
            precipProbDataSet.setDrawValues(false);
            dataSets.add(precipProbDataSet);
        }
        //init char body
        LineData data = new LineData(xVal, dataSets);
        mLineChart.setData(data);
        YAxis axisRight = mLineChart.getAxisRight();
        YAxis axisLeft = mLineChart.getAxisLeft();
        axisRight.setEnabled(false);

        axisLeft.setAxisMinValue((float) (minYAxisVal - 1d));
        axisLeft.setAxisMaxValue((float) (maxYAxisVal + 1d));
        axisLeft.setStartAtZero(false);
        axisLeft.setShowOnlyMinMax(true);
        mLineChart.setBorderColor(GREY_DARK);
        mLineChart.setBackgroundColor(WHITE);
        mLineChart.setGridBackgroundColor(WHITE);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setScaleEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setDoubleTapToZoomEnabled(false);

        //refresh chart
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    protected void setUpDataSet(LineDataSet lineDataSet, int color) {
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format("%d", (int) value);
            }
        };

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleSize(4f);
        lineDataSet.setCircleColor(color);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setFillColor(color);
        lineDataSet.setColor(color);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setValueFormatter(formatter);
        lineDataSet.setValueTextColor(color);
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

            String time = ForecastConverter.getString(currentWeather.getTime(),
                    currentWeather.getTimezone(), ForecastConverter.LONG_MODE);
            mTimeLabel.setText(time.substring(0, time.length() - 2));
            mTimeLabelProperties.setText(time.substring(time.length() - 2, time.length()));

            mSummaryLabel.setText(currentWeather.getSummary());
            mRealFeelLabel.setText(String.format("Real Feel : %s",
                    ForecastConverter.getString(currentWeather.getApparentTemperature(), true, false)));
            mLabelTodaySummary.setText(
                    currentWeather.getTodaySummary());
            //todo get uv index or remove it :(
            mUvIndexLabel.setText("NA");
            mHumidityLabel.setText(String.format
                    ("%s %%", ForecastConverter.getString(currentWeather.getHumidity(), true, true)));
            mPrecipitationLabel.setText(String.format
                    ("%s %%", ForecastConverter.getString(currentWeather.getPrecipProbability(), true, true)));
            mWindLabel.setText(String.format
                    ("%s mps", ForecastConverter.getString(currentWeather.getWindSpeed(), false, false)));
            mWindDirectionLabel.setText(ForecastConverter.getDirection(currentWeather.getWindDirection()));

            drawChart();
            mLineChart.setVisibility(View.VISIBLE);
            mLineChartEmptyLabel.setVisibility(View.GONE);
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

            mLineChart.setVisibility(View.GONE);
            mLineChartEmptyLabel.setVisibility(View.VISIBLE);
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
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            Request request = new Request.Builder().
                    url(String.format("%s/%s/%04f,%04f?units=si", url, apiKey, latitude, longitude)).
                    build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
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
                            //todo optimize
                            alertUserAboutError();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay(mCurrentWeather);
                                    mRefreshLayout.setRefreshing(false);
                                }
                            });
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

    private void toggleState() {
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            mState.put(CHART_MODE_KEY, CHART_PRECIP_MODE);
        } else {
            mState.put(CHART_MODE_KEY, CHART_TEMP_MODE);
        }
    }
}
