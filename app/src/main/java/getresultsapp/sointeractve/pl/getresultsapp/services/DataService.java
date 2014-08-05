package getresultsapp.sointeractve.pl.getresultsapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;

/**
 * Created by mac on 31.07.2014.
 */
public class DataService extends Service{

    private static Timer timer = new Timer();
    private Context context;
    private static final String TAG = "DataService";

    public DataService() {}

    public void onCreate()
    {
        super.onCreate();
        context = this;
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "startService");
        timer.scheduleAtFixedRate(new dataUpdate(), 0, Settings.dataDownloadInterval );
        return Service.START_NOT_STICKY;
    }

    private class dataUpdate extends TimerTask
    {
        @Override
        public void run() {
            Log.d(TAG, "postEventUpdateData");
            App.getEventManager().postEventUpdateData();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}