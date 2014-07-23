package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.ActionBar;

import android.app.Activity;

import android.app.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import java.util.Date;
import java.util.ArrayList;
import android.util.SparseArray;

import java.util.List;

import java.io.IOException;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.LocationsFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.ProfileFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.StatusFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.TabListener;
import pl.sointeractive.isaacloud.Isaacloud;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.InvalidConfigException;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class MainActivity extends Activity{

    private static final String TAG = "UserActivity";
    ActionBar.Tab tab1, tab2, tab3;
    Fragment fragmentTab1 = new StatusFragment();
    Fragment fragmentTab2 = new LocationsFragment();
    Fragment fragmentTab3 = new ProfileFragment();
    boolean success = false;

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private BeaconManager beaconManager = new BeaconManager(this);
    private Context context;
    private static int counter = 0;
    static SparseArray<ArrayList<Double>> beaconDistances = new SparseArray<ArrayList<Double>>();
    static ArrayList<Integer> majors= new ArrayList<Integer>();
    static ArrayList<Integer> temp = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_test);

        // disable custom actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        tab1 = actionBar.newTab().setText("Status");
        tab2 = actionBar.newTab().setText("Locations");
        tab3 = actionBar.newTab().setText("Profile");

        tab1.setTabListener(new TabListener(fragmentTab1));
        tab2.setTabListener(new TabListener(fragmentTab2));
        tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    DateFormat dateFormat = new SimpleDateFormat(("yyyy/MM/dd HH:mm:ss"));
                    public void run() {
                        Beacon foundBeacon;
                        trackBeacons(beacons);
                        Date date = new Date();
                        for (Beacon tempBeacon : beacons) {
                            foundBeacon = tempBeacon;
                            if(counter == 5) calculateDistance(foundBeacon);
                            writeDistance(foundBeacon);
                            Log.d(TAG, "Found beacon: " + foundBeacon + " distance: " + Utils.computeAccuracy(foundBeacon) + " when: " + dateFormat.format(date));
                            Log.d(TAG, "Distance: " + beaconDistances.get(foundBeacon.getMajor()));
                        }
                        if(counter == 5) counter = 0;
                        Log.d(TAG, "Ranged beacons: " + beacons);
                        counter++;
                    }
                });
            }
        });
    }

     
    // LOGIN EVENT
    private class PostEventTask extends AsyncTask<Object, Object, Object> {

        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(Object... params) {
            Log.d(TAG, "UWAGA!");
            try {
                JSONObject body = new JSONObject();
                body.put("activity", "login");
                response = App.getConnector().event(userData.getUserId(),
                        "USER", "PRIORITY_HIGH", 1, "NORMAL", body);
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                isError = true;
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            Log.d(TAG, "onPostExecute()");
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }
    }


    public void writeDistance(Beacon beacon) {
        ArrayList<Double> help;

        if((beaconDistances.size() == 0) || (beaconDistances.get(beacon.getMajor()) == null)) {
            help = new ArrayList<Double>();
        }
        else {
            help = beaconDistances.get(beacon.getMajor());

        }
        Log.d(TAG, "Beacon in function: " + beacon.getMacAddress());
        Log.d(TAG, "beaconDistances size: " + beaconDistances.size());

        help.add(new Double(Utils.computeAccuracy(beacon)));
        Log.d(TAG, "Help variable" + help);
        beaconDistances.put(beacon.getMajor(), help);
        Log.d(TAG, "Counter " + counter);
        Log.d(TAG, "beacon " + beacon.getMacAddress() + " ranges - " + beaconDistances.get(beacon.getMajor()));


    }

    public void calculateDistance(Beacon beacon) {
        double d = 0;
        ArrayList<Double> distances = beaconDistances.get(beacon.getMajor());
        for(Double tempDouble : distances) {
            Log.d(TAG, "distance to a beacon " + beacon.getMacAddress() + ": " + tempDouble);
            d += tempDouble.doubleValue();

        }
        d = d / 5;
        beaconDistances.delete(beacon.getMajor());
        Log.d(TAG, "Average distance to a beacon " + beacon.getMacAddress() + ": " + d);

    }

    public void trackBeacons(List<Beacon> beacons) {
        ArrayList<Integer> helper = new ArrayList<Integer>();
        for(Beacon b : beacons) {
            helper.add(new Integer(b.getMajor()));
            if(!(majors.contains(new Integer(b.getMajor())))) {
                majors.add(new Integer(b.getMajor()));
                Log.d(TAG, "Major " + b.getMajor() + " appeared");
            }
        }
        temp = majors;
        for(Integer i : temp) {
            Log.d(TAG, "All majors: " + majors);
            Log.d(TAG, "helper " + helper);
            Log.d(TAG, "i = " + i);
            if(!(helper.contains(i))) {
                majors.remove(i);
                Log.d(TAG, "Major " + i + " disappeared");
                Log.d(TAG, "All majors: " + majors);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    Log.d(TAG, "Start ranging");
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }
}