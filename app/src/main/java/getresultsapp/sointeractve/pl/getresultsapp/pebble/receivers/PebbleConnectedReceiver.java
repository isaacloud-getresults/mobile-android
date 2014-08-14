package getresultsapp.sointeractve.pl.getresultsapp.pebble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;

public class PebbleConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PebbleConnectedReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, "Event: Pebble is now connected");
        App.getPebbleConnector().isPebbleConnected();
    }
}