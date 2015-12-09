package felix.com.skydrop.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import felix.com.skydrop.fragment.CurrentFragment;
import felix.com.skydrop.fragment.ForecastFragment;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = SectionsPagerAdapter.class.getSimpleName();

    private Map<Integer, Integer> mIdMapper;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        mIdMapper = new HashMap<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mIdMapper.put(position, fragment.getId());
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new CurrentFragment();
        } else {
            return new ForecastFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Currently";
        } else {
            return "Forecast";
        }
    }

    public int getId(int position) {
        if (mIdMapper != null) {
            return mIdMapper.get(position);
        } else {
            return 0;
        }
    }
}
