package com.sointeractive.getresults.app.pebble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.PebbleConnector;

public class PebbleConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PebbleConnectedReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, "Event: Pebble is now connected");
        final PebbleConnector pebbleConnector = App.getPebbleConnector();
        pebbleConnector.closePebbleApp();
        pebbleConnector.isPebbleConnected();
    }
}
