package felix.com.skydrop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import felix.com.skydrop.R;
import felix.com.skydrop.listener.MyOnItemClickListener;
import felix.com.skydrop.model.HourlyForecast;
import felix.com.skydrop.util.ForecastConverter;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    HourlyForecast[] mForecasts;
    String mTimezone;
    MyOnItemClickListener mOnItemClickListener;

    public ForecastAdapter(HourlyForecast[] forecasts, String timezone) {
        mForecasts = forecasts;
        mTimezone = timezone;
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_layout, parent, false);
        return new ForecastViewHolder(itemView, mOnItemClickListener, mTimezone);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        holder.bindViewHolder(mForecasts[position]);
    }

    @Override
    public int getItemCount() {
        return mForecasts.length;
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public void bindViewHolder(HourlyForecast forecast) {
            String timeValue = ForecastConverter.getString(forecast.getTime(), mTimezone, ForecastConverter.SHORT_MODE);
            String type = (forecast.getPrecipType() != null) ? forecast.getPrecipType() : "precipitation chance";
            String precipValue = String.format("%s %%, %s",
                    ForecastConverter.getString(forecast.getPrecipProbability(), true, true), type);
            String temperatureValue = String.format("%s°",
                    ForecastConverter.getString(forecast.getTemperature(), true, false));
            String windValue = String.format("%s mps %s",
                    ForecastConverter.getString(forecast.getWindSpeed(), false, false),
                    ForecastConverter.getDirection(forecast.getWindDirection()));

            mTimeLabel.setText(timeValue);
            mWeatherIcon.setImageResource(ForecastConverter.getIcon(forecast.getIcon()));
            mSummaryLabel.setText(forecast.getSummary());
            mPrecipLabel.setText(precipValue);
            mTemperatureLabel.setText(temperatureValue);
            mWindLabel.setText(windValue);

            mTimeLabel.setBackgroundColor(ForecastConverter.getColor(forecast.getIcon()));
        }
    }
}
