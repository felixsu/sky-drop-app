package felix.com.skydrop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import felix.com.skydrop.R;
import felix.com.skydrop.adapter.SettingsAdapter;
import felix.com.skydrop.constant.SettingConstant;
import felix.com.skydrop.decorator.DividerItemDecoration;
import felix.com.skydrop.model.SettingData;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Bind(R.id.recycler_setting)
    RecyclerView mRecyclerView;

    SettingData mSettingData;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "on back pressed");
        Intent intent = new Intent();
        intent.putExtra(SettingConstant.KEY, mSettingData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    void initData() {
        Log.d(TAG, "begin initdata");
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "getting intent");
            mSettingData = (SettingData) intent.getSerializableExtra(SettingConstant.KEY);
            Log.d(TAG, mSettingData.toString());
        }
    }

    void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SettingsAdapter adapter = new SettingsAdapter(this, mSettingData);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

}
