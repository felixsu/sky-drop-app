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

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.activity.MainActivity;
import felix.com.skydrop.adapter.ForecastAdapter;
import felix.com.skydrop.listener.MyOnItemClickListener;
import felix.com.skydrop.model.ApplicationData;
import felix.com.skydrop.model.SettingData;
import felix.com.skydrop.model.WeatherData;

/**
 * Created by fsoewito on 12/4/2015.
 * fragment that responsible for forecast data (houly)
 */
public class ForecastFragment extends Fragment implements MyOnItemClickListener {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    @Bind(R.id.recycler_forecast)
    RecyclerView mRecyclerView;

    @Bind(R.id.label_empty_recycler)
    TextView mEmptyRecyclerLabel;

    View mView;
    ForecastAdapter mForecastAdapter;
    MainActivity mActivity;
    ApplicationData mApplicationData;
    SettingData mSettingData;
    WeatherData mWeatherData;
    boolean created;

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
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
        mSettingData = mActivity.getSettingData();
    }

    public void initView() {
        mForecastAdapter = new ForecastAdapter(mActivity, mWeatherData, mSettingData);
        mForecastAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        updateDisplay();
    }

    public void updateDisplay() {
        if (mWeatherData.isInitialized()) {
            mEmptyRecyclerLabel.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.getAdapter().notifyDataSetChanged();
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
        Log.d(TAG, "on click " + position);
    }
}
