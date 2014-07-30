package getresultsapp.sointeractve.pl.getresultsapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import getresultsapp.sointeractve.pl.getresultsapp.fragments.LocationsFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.ProfileFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.StatusFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {

            case 0: return StatusFragment.newInstance();
            case 1: return LocationsFragment.newInstance();
            case 2: return ProfileFragment.newInstance();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}