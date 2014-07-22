package getresultsapp.sointeractve.pl.getresultsapp.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

import getresultsapp.sointeractve.pl.getresultsapp.R;

public class TabListener implements ActionBar.TabListener {
    Fragment fragment;

    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_container, fragment);
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // nothing done here
    }
}