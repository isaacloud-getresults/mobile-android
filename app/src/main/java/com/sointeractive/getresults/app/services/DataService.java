package com.sointeractive.getresults.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;

import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service {

    private static final String TAG = DataService.class.getSimpleName();
    private static Timer timer;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Event: onCreate");
    }

    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.i(TAG, "Event: StartService");
        timer = new Timer();
        timer.scheduleAtFixedRate(new dataUpdate(), 0, Settings.DATA_DOWNLOAD_INTERVAL);
//        SocketIONotifier.INSTANCE.connect(userId);
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Toast.makeText(this, "DataService stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    ////////////////////////////////
    /// WEB SOCKET CLIENT
    ////////////////////////////////

    private class dataUpdate extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "Action: postEventUpdateData");
            App.getEventManager().postEventUpdateData();
        }
    }


}

