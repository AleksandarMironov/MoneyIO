package io.money.moneyio.model.utilities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Day;
import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Month;
import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Year;

public class PageAdapter extends FragmentPagerAdapter{

    private int numberOfTabs;

    public PageAdapter(FragmentManager childFragmentManager, int tabCount) {
        super(childFragmentManager);
        this.numberOfTabs = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentTab_Day fragmentTabDay = new FragmentTab_Day();
                return fragmentTabDay;
            case 1:
                FragmentTab_Month fragmentTabMonth = new FragmentTab_Month();
                return fragmentTabMonth;
            case 2:
                FragmentTab_Year fragmentTabYear = new FragmentTab_Year();
                return fragmentTabYear;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
