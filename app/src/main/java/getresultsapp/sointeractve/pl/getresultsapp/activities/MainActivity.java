package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.ActionBar;

import android.app.Activity;

import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.LocationsFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.ProfileFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.StatusFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.TabListener;
import getresultsapp.sointeractve.pl.getresultsapp.services.TrackService;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class MainActivity extends Activity{

    private static final String TAG = "MainActivity";
    ActionBar.Tab tab1, tab2, tab3;
    Fragment fragmentTab1 = new StatusFragment();
    Fragment fragmentTab2 = new LocationsFragment();
    Fragment fragmentTab3 = new ProfileFragment();
    boolean success = false;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_test);

        // disable custom actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        tab1 = actionBar.newTab().setText("Status");
        tab2 = actionBar.newTab().setText("Locations");
        tab3 = actionBar.newTab().setText("Profile");

        tab1.setTabListener(new TabListener(fragmentTab1));
        tab2.setTabListener(new TabListener(fragmentTab2));
        tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);

        // Post login event
        App.getEventManager().postEventLogin();

        // create new data manager which downloads locations and people
        App.createDataManager();
        
        Intent i = new Intent(getApplicationContext(), TrackService.class);
        i.putExtra("KEY1", "Value to be used by the service");
        getApplicationContext().startService(i);
        Log.d(TAG, "Service started 1");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // beaconManager.disconnect();
    }
}
