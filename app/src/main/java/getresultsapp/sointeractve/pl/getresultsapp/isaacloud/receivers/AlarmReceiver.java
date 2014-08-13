package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import getresultsapp.sointeractve.pl.getresultsapp.utils.CacheManager;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Event: Alarm tick");
        CacheManager.INSTANCE.update();
    }
}
