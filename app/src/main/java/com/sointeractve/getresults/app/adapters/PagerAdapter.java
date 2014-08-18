package com.sointeractve.getresults.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sointeractve.getresults.app.fragments.AchievementsFragment;
import com.sointeractve.getresults.app.fragments.LocationsFragment;
import com.sointeractve.getresults.app.fragments.ProfileFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {

            case 0:
                return ProfileFragment.newInstance();
            case 1:
                return LocationsFragment.newInstance();
            case 2:
                return AchievementsFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}