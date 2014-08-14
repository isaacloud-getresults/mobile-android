package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.ActionBar;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;

import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.adapters.PagerAdapter;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.services.DataService;
import getresultsapp.sointeractve.pl.getresultsapp.services.TrackService;


public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private static final String TAG = "UserActivity";
    private ViewPager viewPager;
    private PagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey("achPointer")) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.addTab(actionBar.newTab().setIcon(new IconDrawable(this, Iconify.IconValue.fa_user)
                .colorRes(R.color.color_theme_text_dark)
                .actionBarSize()).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(new IconDrawable(this, Iconify.IconValue.fa_bars)
                .colorRes(R.color.color_theme_text_dark)
                .actionBarSize()).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(new IconDrawable(this, Iconify.IconValue.fa_trophy)
                .colorRes(R.color.color_theme_text_dark)
                .actionBarSize()).setTabListener(this));


        mAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        // set page swipe listener
        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                       actionBar.setSelectedNavigationItem(position);
                    }
                });


            // SEND LOGIN EVENT
        App.getEventManager().postEventLogin();

        Intent i = new Intent(getApplicationContext(), TrackService.class);
        getApplicationContext().startService(i);
        Intent j = new Intent(getApplicationContext(), DataService.class);
        getApplicationContext().startService(j);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onNewIntent(getIntent());
    }

    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("achPointer"))
            {
                Toast.makeText(this, "onResume() extras = " + extras.getInt("achPointer"), Toast.LENGTH_LONG).show();
                actionBar.setSelectedNavigationItem(extras.getInt("achPointer"));
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        Intent i = new Intent(getApplicationContext(), DataService.class);
        getApplicationContext().stopService(i);
        Intent j = new Intent(getApplicationContext(), TrackService.class);
        getApplicationContext().stopService(j);
        super.onDestroy();

    }
}
