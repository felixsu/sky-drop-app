package felix.com.skydrop.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.adapter.SettingsAdapter;
import felix.com.skydrop.factory.SettingDataFactory;
import felix.com.skydrop.model.SettingData;

public class SettingsActivity extends AppCompatActivity {
    @Bind(R.id.recycler_setting)
    RecyclerView mRecyclerView;

    SettingData mSettingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    void initData() {
        mSettingData = SettingDataFactory.getInstance();
    }

    void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SettingsAdapter adapter = new SettingsAdapter(mSettingData);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }

}
