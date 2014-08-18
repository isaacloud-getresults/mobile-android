package getresultsapp.sointeractve.pl.getresultsapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import android.os.Handler;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;

public class TrackService extends Service {
    public TrackService() {
    }

    private static final String TAG = "TrackService";


    // TODO: UUID loaded from QR reader, not hardcoded
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    //
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, 6000, null);
    private BeaconManager beaconManager = new BeaconManager(this);
    static SparseArray<ArrayList<Double>> beaconDistances = new SparseArray<ArrayList<Double>>();
    static ArrayList<String> majors= new ArrayList<String>();
    static ArrayList<String> temp;
    static HashMap<String, String> x = new HashMap<String, String>();
    Handler handler = new Handler();
    static HashMap<String, Beacon> beaconMap = new HashMap<String, Beacon>();
    static HashMap<String, Integer> counterMap = new HashMap<String, Integer>();
    Beacon lastBeacon;
    Context context = App.getInstance().getApplicationContext();
    static boolean internetConnection;
    static boolean previousFlag = false;
    Thread thread;
    static Context serviceContext;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();
        thread = new Thread(new InternetRunnable());
        thread.start();
        serviceContext = this;
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        temp = majors;
                        trackBeacons(beacons);
                        for (Beacon beacon : beacons) {
                            writeDistance(beacon);
                            Log.d(TAG, "Found beacon: " + beacon + " distance: " + Utils.computeAccuracy(beacon));
                        }
                        Log.d(TAG, "Ranged beacons: " + beacons);
                    }
                });
            }
        });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
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

        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
        serviceContext = null;
        majors = new ArrayList<String>();
        Toast.makeText(this, "TrackService stopped", Toast.LENGTH_LONG).show();
        if(lastBeacon != null && internetConnection) App.getEventManager().postEventLeftBeacon(Integer.toString(lastBeacon.getMajor()) , Integer.toString(lastBeacon.getMinor()));
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void writeDistance(Beacon beacon) {
        ArrayList<Double> help;

        if((beaconDistances.size() == 0) || (beaconDistances.get(beacon.getMajor()) == null)) {
            help = new ArrayList<Double>();
        }
        else {
            help = beaconDistances.get(beacon.getMajor());

        }
        help.add(new Double(Utils.computeAccuracy(beacon)));
        beaconDistances.put(beacon.getMajor(), help);


    }
    /*
    public void calculateDistance(Beacon beacon) {
        double d = 0;
        ArrayList<Double> distances = beaconDistances.get(beacon.getMajor());
        for(Double tempDouble : distances) {
            Log.d(TAG, "distance to a beacon " + beacon.getMacAddress() + ": " + tempDouble);
            d += tempDouble.doubleValue();

        }
        d = d / distances.size();
        beaconDistances.delete(beacon.getMajor());
        Log.d(TAG, "Average distance to a beacon " + beacon.getMacAddress() + ": " + d);
        setAndroidNotification("You entered a new beacon range!", beacon.getMacAddress(), d);
    }
    */

    public void trackBeacons(List<Beacon> beacons) {

        ArrayList<String> helper = new ArrayList<String>();
        Vibrator v = (Vibrator)context.getSystemService((Context.VIBRATOR_SERVICE));
        for(Beacon b : beacons) {
            helper.add(new String(b.getMacAddress()));
            beaconMap.put(b.getMacAddress(), b);
            if(!(majors.contains(new String(b.getMacAddress())))) {
                if(readyToSend(b, true)) {
                    majors.add(new String(b.getMacAddress()));
                    Toast.makeText(getApplicationContext(), "Entered " + b.getMinor() + " range!", Toast.LENGTH_SHORT).show();
                    if (internetConnection)
                        App.getEventManager().postEventNewBeacon(Integer.toString(b.getMajor()), Integer.toString(b.getMinor()));
                    lastBeacon = b;
//                    v.vibrate(0);
                }
            } else previousFlag = false;
            if(internetConnection == false) Toast.makeText(this, "NO INTERNET!", Toast.LENGTH_SHORT).show();
        }

        temp = new ArrayList<String>(majors);
        for(String i : temp) {
            Beacon tempBeacon = beaconMap.get(i);
            if(!(helper.contains(i))) {
                if(readyToSend(tempBeacon, true)) {
                    majors.remove(i);
                    Toast.makeText(getApplicationContext(), "Left " + tempBeacon.getMinor() + " range!", Toast.LENGTH_SHORT).show();
                    if (internetConnection)
                        App.getEventManager().postEventLeftBeacon(Integer.toString(tempBeacon.getMajor()), Integer.toString(tempBeacon.getMinor()));
//                    v.vibrate(0);
                }
            } else previousFlag = false;
        }
    }

    private boolean readyToSend(Beacon b, boolean flag) {
        String mac = b.getMacAddress();
        Integer i;

        if(!counterMap.containsKey(mac) || flag != previousFlag) {
            counterMap.put(mac, new Integer(0));
        } else {
            i = counterMap.get(mac);
            i++;
            counterMap.put(mac, i);
//
            Toast.makeText(getApplicationContext(), "i = " + i, Toast.LENGTH_SHORT).show();
        }
        previousFlag = flag;
        if(counterMap.get(mac) == 5) {
            counterMap.remove(mac);
            return true;
        }
        return false;
    }

    public class InternetRunnable implements Runnable {
        public void run() {
            while(serviceContext != null) {
                internetConnection = isNetworkAvailable();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                Log.d(TAG, "Connected: " + internetConnection);
            }
        }
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {

            }
        } else {

        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
