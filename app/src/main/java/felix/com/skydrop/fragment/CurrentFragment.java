package felix.com.skydrop.fragment;


import android.app.Activity;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;

/**
 * Created by fsoewito on 11/24/2015.
 *
 */
public class CurrentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = CurrentFragment.class.getSimpleName();
    private static final String TIME_FORMAT = "HH:mm";
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
        mActivity = getActivity();
        initData();
        return mLayout;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "on refresh called");
        mRefreshLayout.setRefreshing(false);
    }

    protected void initData(){
        mTemperatureLabel.setText("--");
        mIconWeather.setImageResource(R.drawable.ic_sunny);
        mTimeLabel.setText(getTime());
        mSummaryLabel.setText("-");
        mRealFeelLabel.setText("--");

        mLabelTodaySummary.setText("getting information..");

        mUvIndexLabel.setText(String.valueOf(8));
        mHumidityLabel.setText(String.valueOf(0.7));
        mPrecipitationLabel.setText(String.format("%d %%", 70));
        mWindLabel.setText(String.format("%d - mph", 4));
        mWindDirectionLabel.setText("SSE");

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN);

    }

    private final String getTime(){
        return new SimpleDateFormat(TIME_FORMAT).format(new Date());
    }
}
