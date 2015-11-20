package com.lh.leonard.amplifiedscheduler;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private Map<Integer, Fragment> mFragmentTags;
    private FragmentManager mFragmentManager;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        mFragmentManager = fm;
        this.Titles = mTitles;
        mFragmentTags = new HashMap<>();
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        if (position == 0) // if the position is 0 we are returning the First tab
        {
            MyContactsFragment tab1 = new MyContactsFragment();

            mFragmentTags.put(position, tab1);

            return tab1;
        } else if (position == 1)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            PersonRequestsTabs tab2 = new PersonRequestsTabs();

            mFragmentTags.put(position, tab2);

            return tab2;
        } else        // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FindContactsFragment tab3 = new FindContactsFragment();

            mFragmentTags.put(position, tab3);

            return tab3;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment f = (Fragment) object;
            mFragmentTags.put(position, f);


        }
        return object;
    }

    public Fragment getFragment(int key) {
        return mFragmentTags.get(key);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragmentTags.remove(position);
    }
}