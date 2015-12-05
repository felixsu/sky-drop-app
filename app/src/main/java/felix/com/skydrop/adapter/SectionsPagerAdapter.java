package felix.com.skydrop.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import felix.com.skydrop.fragment.CurrentFragment;
import felix.com.skydrop.fragment.ForecastFragment;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
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
}
