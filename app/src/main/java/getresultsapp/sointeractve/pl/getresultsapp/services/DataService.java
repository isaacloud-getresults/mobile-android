package getresultsapp.sointeractve.pl.getresultsapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 31.07.2014.
 */
public class DataService extends Service{
    private static Timer timer = new Timer();
    private Context context;

    public void onCreate()
    {
        super.onCreate();
        context = this;
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {

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