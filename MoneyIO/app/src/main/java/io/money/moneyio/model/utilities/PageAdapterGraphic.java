package io.money.moneyio.model.utilities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.money.moneyio.fragments.graphicsTabs.FragmentTab_DayGraphic;
import io.money.moneyio.fragments.graphicsTabs.FragmentTab_MonthGraphic;
import io.money.moneyio.fragments.graphicsTabs.FragmentTab_YearGraphic;

public class PageAdapterGraphic extends FragmentPagerAdapter {

    private int numberOfTabs;

    public PageAdapterGraphic(FragmentManager fm, int tabs) {
        super(fm);
        this.numberOfTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentTab_DayGraphic day = new FragmentTab_DayGraphic();
                return day;
            case 1:
                FragmentTab_MonthGraphic month = new FragmentTab_MonthGraphic();
                return month;
            case 2:
                FragmentTab_YearGraphic year = new FragmentTab_YearGraphic();
                return year;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
