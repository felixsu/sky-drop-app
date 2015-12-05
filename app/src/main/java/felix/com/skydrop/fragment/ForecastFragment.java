package felix.com.skydrop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.activity.MainActivity;
import felix.com.skydrop.adapter.ForecastAdapter;
import felix.com.skydrop.constant.Color;
import felix.com.skydrop.formatter.IntegerValueFormatter;
import felix.com.skydrop.formatter.LevelValueFormatter;
import felix.com.skydrop.listener.MyOnItemClickListener;
import felix.com.skydrop.model.ApplicationData;
import felix.com.skydrop.model.HourlyForecast;
import felix.com.skydrop.model.WeatherData;
import felix.com.skydrop.util.ForecastConverter;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class ForecastFragment extends Fragment implements MyOnItemClickListener {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    private static final String CHART_MODE_KEY = "chart_mode";
    private static final int CHART_TEMP_MODE = 0;
    private static final int CHART_PRECIP_MODE = 1;

    @Bind(R.id.chart_forecast)
    LineChart mLineChart;

    @Bind(R.id.recycler_forecast)
    RecyclerView mRecyclerView;

    @Bind(R.id.label_summary)
    TextView mSummaryLabel;

    @Bind(R.id.label_empty_recycler)
    TextView mEmptyRecyclerLabel;

    View mView;
    ForecastAdapter mForecastAdapter;


    MainActivity mActivity;
    ApplicationData mApplicationData;
    WeatherData mWeatherData;
    HashMap<String, Integer> mState;
    boolean created;

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isCreated() {
        return created;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "entering on create view");
        mView = inflater.inflate(R.layout.fragment_forecast, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "entering on view created");
        initData();
        initView();
        setCreated(true);
    }

    public void initData() {
        mActivity = (MainActivity) getActivity();
        mWeatherData = mActivity.getWeatherData();
        mApplicationData = mActivity.getApplicationData();

        mState = new HashMap<>();
        mState.put(CHART_MODE_KEY, CHART_TEMP_MODE);
    }

    public void initView() {
        mForecastAdapter = new ForecastAdapter(mWeatherData.getHourlyForecasts(), mWeatherData.getTimezone());
        mForecastAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeatherData.isInitialized()) {
                    toggleState();
                    drawChart();
                }
            }
        });
        updateDisplay();
    }

    public void updateDisplay() {
        mSummaryLabel.setText(mWeatherData.getHourSummary());
        if (mWeatherData.isInitialized()) {
            mEmptyRecyclerLabel.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            drawChart();
        } else {
            mEmptyRecyclerLabel.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyRecyclerLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, "please refresh", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void toggleState() {
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            mState.put(CHART_MODE_KEY, CHART_PRECIP_MODE);
        } else {
            mState.put(CHART_MODE_KEY, CHART_TEMP_MODE);
        }
    }

    private void drawChart() {
        HourlyForecast[] datas = mWeatherData.getHourlyForecasts();
        //init label
        List<String> xVal = new ArrayList<>();
        for (int i = 0; i < WeatherData.FORECAST_DISPLAYED; i++) {
            xVal.add(ForecastConverter.getString(
                    datas[i].getTime(), mWeatherData.getTimezone(),
                    ForecastConverter.SHORT_MODE));
        }

        //init Line
        List<Entry> actualTempList = new ArrayList<>();
        List<Entry> apparentTempList = new ArrayList<>();
        List<Entry> precipProbList = new ArrayList<>();
        List<Entry> precipIntensityList = new ArrayList<>();

        double minYAxisVal = 200;
        double maxYAxisVal = -100;
        for (int i = 0; i < WeatherData.FORECAST_DISPLAYED; i++) {
            actualTempList.add(new Entry((float) Math.round(datas[i].getTemperature()), i));
            apparentTempList.add(new Entry((float) Math.round(datas[i].getApparentTemperature()), i));
            precipProbList.add(new Entry((float) (datas[i].getPrecipProbability() * 100), i));
            precipIntensityList.add(new Entry((float) (datas[i].getPrecipIntensity()) * 100, i));
        }

        LineDataSet actualTempDataSet = new LineDataSet(actualTempList, "actual temp");
        setUpDataSet(actualTempDataSet, Color.PRIMARY_COLOR, false, YAxis.AxisDependency.LEFT);
        LineDataSet apparentTempDataSet = new LineDataSet(apparentTempList, "apparent temp");
        setUpDataSet(apparentTempDataSet, Color.ACCENT_COLOR, false, YAxis.AxisDependency.LEFT);

        LineDataSet precipProbDataSet = new LineDataSet(precipProbList, "rain chance");
        setUpDataSet(precipProbDataSet, Color.PRIMARY_COLOR, false, YAxis.AxisDependency.LEFT);
        LineDataSet precipIntensityDataSet = new LineDataSet(precipIntensityList, "intensity");
        setUpDataSet(precipIntensityDataSet, Color.ACCENT_COLOR, true, YAxis.AxisDependency.RIGHT);


        List<LineDataSet> dataSets = new ArrayList<>();
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            for (int i = 0; i < WeatherData.FORECAST_DISPLAYED; i++) {
                double val = mWeatherData.getHourlyForecasts()[i].getTemperature();
                double val2 = mWeatherData.getHourlyForecasts()[i].getApparentTemperature();
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
            dataSets.add(precipIntensityDataSet);
        }
        //init char body
        LineData data = new LineData(xVal, dataSets);
        mLineChart.setData(data);

        YAxis axisRight = mLineChart.getAxisRight();

        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            axisRight.setEnabled(false);
        } else {
            axisRight.setEnabled(true);
            axisRight.setValueFormatter(new LevelValueFormatter());
            axisRight.setLabelCount(3, true);
            axisRight.setAxisMaxValue(1500);
            axisRight.setAxisMinValue(0);
        }

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(2f);

        YAxis axisLeft = mLineChart.getAxisLeft();
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            axisLeft.setAxisMinValue((float) (minYAxisVal - 1d));
            axisLeft.setAxisMaxValue((float) (maxYAxisVal + 1d));
            axisLeft.setEnabled(true);
        } else {
            axisLeft.setEnabled(true);
            axisLeft.setAxisMinValue(0f);
            axisLeft.setAxisMaxValue(100f);
            axisLeft.setDrawAxisLine(true);
            axisLeft.setAxisLineWidth(1f);
        }

        axisLeft.setLabelCount(6, false);
        axisLeft.setStartAtZero(false);
        axisLeft.setDrawGridLines(false);

        mLineChart.setBorderColor(Color.GREY_DARK);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setGridBackgroundColor(Color.WHITE);
        mLineChart.setDrawGridBackground(false);
        //interaction setting
        mLineChart.setScaleEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setHighlightPerDragEnabled(false);
        mLineChart.setHighlightPerTapEnabled(false);

        //title
        if (mState.get(CHART_MODE_KEY) == CHART_TEMP_MODE) {
            mLineChart.setDescription("Temperature Forecast");
        } else {
            mLineChart.setDescription("Rain Chance Forecast");
        }

        //refresh chart
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    protected void setUpDataSet(LineDataSet lineDataSet, int color, boolean drawFill, YAxis.AxisDependency dependency) {
        ValueFormatter formatter;
        if (dependency == YAxis.AxisDependency.LEFT) {
            formatter = new IntegerValueFormatter();
        } else {
            formatter = new LevelValueFormatter();
        }
        lineDataSet.setAxisDependency(dependency);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleSize(4f);
        lineDataSet.setCircleColor(color);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setFillColor(color);
        lineDataSet.setDrawFilled(drawFill);
        lineDataSet.setColor(color);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setValueFormatter(formatter);
        lineDataSet.setValueTextColor(color);

        if (drawFill) {
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "entering on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "entering on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "entering on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "entering on stop");
    }

    @Override
    public void onClick(View view, int position) {
        Log.i(TAG, "on click " + position);
    }
}
