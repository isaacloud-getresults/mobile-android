package getresultsapp.sointeractve.pl.getresultsapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;

public class DataService extends Service {

    private static final String TAG = "DataService";
    private static Timer timer;

    public DataService() {
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "startService");
        timer = new Timer();
        timer.scheduleAtFixedRate(new dataUpdate(), 0, Settings.dataDownloadInterval);
//        SocketIONotifier.INSTANCE.connect(userId);
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Toast.makeText(this, "DataService stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ////////////////////////////////
    /// WEB SOCKET CLIENT
    ////////////////////////////////

    private class dataUpdate extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "postEventUpdateData");
            App.getEventManager().postEventUpdateData();
        }
    }


}

