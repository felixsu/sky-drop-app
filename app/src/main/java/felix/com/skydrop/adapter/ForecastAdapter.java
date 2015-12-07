package felix.com.skydrop.adapter;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import felix.com.skydrop.R;
import felix.com.skydrop.constant.Color;
import felix.com.skydrop.constant.GlobalConstant;
import felix.com.skydrop.formatter.IntegerValueFormatter;
import felix.com.skydrop.formatter.LevelValueFormatter;
import felix.com.skydrop.listener.MyOnItemClickListener;
import felix.com.skydrop.model.HourlyForecast;
import felix.com.skydrop.model.WeatherData;
import felix.com.skydrop.util.ForecastConverter;
import felix.com.skydrop.viewholder.MyViewHolder;

/**
 * Created by fsoewito on 12/4/2015.
 *
 */
public class ForecastAdapter extends RecyclerView.Adapter<MyViewHolder<WeatherData>> {

    Context mContext;
    WeatherData mWeatherData;
    MyOnItemClickListener mOnItemClickListener;
    int mChartState = GlobalConstant.CHART_TEMP_MODE;

    public ForecastAdapter(Context context, WeatherData weatherData) {
        mWeatherData = weatherData;
        mContext = context;
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? 0 : 1);
    }

    @Override
    public MyViewHolder<WeatherData> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View itemView = inflater.inflate(R.layout.item_forecast_layout_header, parent, false);
            return new ChartViewHolder(itemView, mOnItemClickListener);
        } else {
            View itemView = inflater.inflate(R.layout.item_forecast_layout_member, parent, false);
            return new ForecastViewHolder(itemView, mOnItemClickListener, mWeatherData.getTimezone());

        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder<WeatherData> holder, int position) {
        holder.bindViewHolder(mWeatherData, position);
    }

    @Override
    public int getItemCount() {
        return (mWeatherData.getHourlyForecasts().length) + 1;
    }

    public class ForecastViewHolder extends MyViewHolder<WeatherData> implements View.OnClickListener {
        MyOnItemClickListener mListener;
        String mTimezone;

        TextView mTimeLabel;
        ImageView mWeatherIcon;
        TextView mSummaryLabel;
        TextView mPrecipLabel;
        TextView mTemperatureLabel;
        TextView mWindLabel;

        public ForecastViewHolder(View itemView, MyOnItemClickListener listener, String timezone) {
            super(itemView);
            mListener = listener;
            mTimezone = timezone;

            mTimeLabel = (TextView) itemView.findViewById(R.id.item_label_time);
            mWeatherIcon = (ImageView) itemView.findViewById(R.id.item_icon_forecast);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.item_label_summary);
            mPrecipLabel = (TextView) itemView.findViewById(R.id.item_label_precip);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.item_label_temperature);
            mWindLabel = (TextView) itemView.findViewById(R.id.item_label_wind);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(v, getLayoutPosition());
            }
        }

        @Override
        public void bindViewHolder(WeatherData data, int position) {
            HourlyForecast forecast = data.getHourlyForecasts()[position - 1];
            String timeValue = ForecastConverter.getString(forecast.getTime(), mTimezone, ForecastConverter.SHORT_MODE);
            String type = (forecast.getPrecipType() != null) ? forecast.getPrecipType() : "precipitation chance";
            String precipValue = String.format("%s %%, %s",
                    ForecastConverter.getString(forecast.getPrecipProbability(), true, true), type);
            String temperatureValue = String.format("%s°",
                    ForecastConverter.getString(forecast.getTemperature(), true, false));
            String windValue = String.format("%s mps %s",
                    ForecastConverter.getString(forecast.getWindSpeed(), false, false),
                    ForecastConverter.getDirection(forecast.getWindDirection()));

            ColorFilter filter = new LightingColorFilter(Color.BLACK, 0xFF3F51B5);
            Drawable drawable = mContext.getResources().getDrawable(ForecastConverter.getIcon(forecast.getIcon()));
            if (drawable != null) {
                drawable.setColorFilter(filter);
            }

            mTimeLabel.setText(timeValue);
            mWeatherIcon.setImageDrawable(drawable);
            mSummaryLabel.setText(forecast.getSummary());
            mPrecipLabel.setText(precipValue);
            mTemperatureLabel.setText(temperatureValue);
            mWindLabel.setText(windValue);

            mTimeLabel.setTextColor(ForecastConverter.getTextColor(forecast.getIcon()));
            ((GradientDrawable) mTimeLabel.getBackground()).setColor(ForecastConverter.getColor(forecast.getIcon()));
        }

    }

    public class ChartViewHolder extends MyViewHolder<WeatherData> implements View.OnClickListener {
        TextView mTitleLabel;
        TextView mSummaryLabel;
        LineChart mLineChart;
        MyOnItemClickListener mListener;

        public ChartViewHolder(View itemView, MyOnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            mTitleLabel = (TextView) itemView.findViewById(R.id.item_label_header_title);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.item_label_hourly_summary);
            mLineChart = (LineChart) itemView.findViewById(R.id.item_chart_forecast);
            if (mLineChart != null) {
                initChart();
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(v, getLayoutPosition());
            }
            toggleChartState();
        }

        @Override
        public void bindViewHolder(WeatherData weatherData, int position) {
            mTitleLabel.setText("Next 8 hours");
            mSummaryLabel.setText(weatherData.getHourSummary());
            drawChart(weatherData, GlobalConstant.CHART_TEMP_MODE);
        }

        public void drawChart(WeatherData weatherData, int state) {
            HourlyForecast[] datas = weatherData.getHourlyForecasts();
            //init label
            List<String> xVal = new ArrayList<>();
            for (int i = 0; i < WeatherData.N_FORECAST; i++) {
                xVal.add(ForecastConverter.getString(
                        datas[i].getTime(), weatherData.getTimezone(),
                        ForecastConverter.SHORT_MODE));
            }

            //init Line
            List<Entry> actualTempList = new ArrayList<>();
            List<Entry> apparentTempList = new ArrayList<>();
            List<Entry> precipProbList = new ArrayList<>();
            List<Entry> precipIntensityList = new ArrayList<>();

            double minYAxisVal = 200;
            double maxYAxisVal = -100;
            for (int i = 0; i < WeatherData.N_FORECAST; i++) {
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
            if (state == GlobalConstant.CHART_TEMP_MODE) {
                for (int i = 0; i < WeatherData.N_FORECAST; i++) {
                    double val = weatherData.getHourlyForecasts()[i].getTemperature();
                    double val2 = weatherData.getHourlyForecasts()[i].getApparentTemperature();
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

            if (state == GlobalConstant.CHART_TEMP_MODE) {
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
            if (state == GlobalConstant.CHART_TEMP_MODE) {
                axisLeft.setAxisMinValue((float) (minYAxisVal - 1d));
                axisLeft.setAxisMaxValue((float) (maxYAxisVal + 1d));

                axisLeft.setEnabled(true);
                axisLeft.setAxisLineWidth(1.4f);
                axisLeft.setDrawAxisLine(true);
                axisLeft.setDrawGridLines(true);
            } else {
                axisLeft.setAxisMinValue(0f);
                axisLeft.setAxisMaxValue(100f);

                axisLeft.setEnabled(true);
                axisLeft.setAxisLineWidth(1.4f);
                axisLeft.setDrawAxisLine(true);
                axisLeft.setDrawGridLines(false);
            }
            axisLeft.setLabelCount(6, false);
            axisLeft.setStartAtZero(false);

            //title
            if (state == GlobalConstant.CHART_TEMP_MODE) {
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
            lineDataSet.setDrawValues(false);
            if (drawFill) {
                lineDataSet.setDrawCircles(false);
            }
        }

        public void initChart() {
            mLineChart.setOnClickListener(this);

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
        }

        public void toggleChartState() {
            if (mChartState == GlobalConstant.CHART_TEMP_MODE) {
                mChartState = GlobalConstant.CHART_PRECIP_MODE;
            } else {
                mChartState = GlobalConstant.CHART_TEMP_MODE;
            }
            drawChart(mWeatherData, mChartState);
            //notifyItemChanged(getLayoutPosition());
        }

    }

}
