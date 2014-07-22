package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.util.SparseArray;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import java.util.Date;

import java.util.List;

import java.io.IOException;
import java.util.Map;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class MainActivity extends Activity {
    // test comment
    SparseArray<Group> groups = new SparseArray<Group>();
    private static final String TAG = "UserActivity";
    String[] locations;
    boolean success = false;

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private BeaconManager beaconManager = new BeaconManager(this);
    private Context context;
    private static int counter = 0;
    static SparseArray<ArrayList<Double>> beaconDistances;
    static double dist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AlertDialog.Builder(this)
                .setTitle("Hello " + App.loadUserData().getName())
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                }).show();

        new LoginTask().execute();

        while(!success) {}
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    DateFormat dateFormat = new SimpleDateFormat(("yyyy/MM/dd HH:mm:ss"));
                    public void run() {

                        Beacon foundBeacon;
                        Date date = new Date();
                        for(Beacon tempBeacon : beacons) {
                            foundBeacon = tempBeacon;
                            calculateDistance(counter, foundBeacon);
                            Log.d(TAG, "Found beacon: " + foundBeacon + " distance: " + Utils.computeAccuracy(foundBeacon) + " when: " + dateFormat.format(date));
                        }
                        Log.d(TAG, "Ranged beacons: " + beacons);
                        counter++;
                    }
                });
            }
        });

    }

    public void calculateDistance(int count, Beacon beacon) {
        if(count == 5) {
            double d = 0;
            ArrayList<Double> distances = beaconDistances.get(beacon.getMajor());
            for(Double tempDouble: distances) {
                d =+ tempDouble.doubleValue();
                d = d / 5;
            }
            beaconDistances.delete(beacon.getMajor());
            dist = d;
            Log.d(TAG, "Average distance to a beacon " + beacon.getMacAddress() + ": " + dist);
            counter = -1;
        }

        else {
            ArrayList help = new ArrayList();
            Log.d(TAG, "Ranged beacons: " + beacon);
            help.add(new Double(Utils.computeAccuracy(beacon)));
            beaconDistances.put(beacon.getMajor(), help);

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
/*
    @Override
    protected void onStop() {
        super.onStop();
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }

    private class LoginTask extends AsyncTask<Object, Object, Object> {
        protected Object doInBackground(Object... params) {

            Log.d(TAG, "ATTENTION");
            try {
                HttpResponse response = App.getConnector().path("/cache/users/groups").get();
                Log.d(TAG, response.toString());
                JSONArray array = response.getJSONArray();
                locations = new String[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    locations[i] = json.getString("label");
                    Log.d(TAG, locations[i].toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            createData();
            return null;
        }

        protected void createData() {
            for (int j = 0; j < locations.length; j++) {
                Group group = new Group(locations[j]);
                int rand = (int) (Math.random() * 10);
                for (int i = 0; i < rand; i++) {
                    group.children.add("Janusz Tester");
                }
                groups.append(j, group);
                success = true;
            }
        }
    }
}
