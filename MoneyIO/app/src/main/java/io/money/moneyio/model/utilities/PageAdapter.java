package io.money.moneyio.model.utilities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Day;
import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Month;
import io.money.moneyio.fragments.statisticsTabs.FragmentTab_Year;

//page adapter used for the sliding tabs in the fragment Fragment_DataHistory
public class PageAdapter extends FragmentPagerAdapter{

    private int numberOfTabs;

    public PageAdapter(FragmentManager childFragmentManager, int tabCount) {
        super(childFragmentManager);
        if(tabCount >= 0) {
            this.numberOfTabs = tabCount;
        } else {
            this.numberOfTabs = 0;
        }
    }

    @Override
    public Fragment getItem(int position) {
        //getting the items with switch/case construction
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
    //get the number of the tabs loaded
    public int getCount() {
        return numberOfTabs;
    }
}
