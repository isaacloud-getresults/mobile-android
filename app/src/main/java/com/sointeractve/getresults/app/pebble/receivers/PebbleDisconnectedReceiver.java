package com.sointeractve.getresults.app.pebble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sointeractve.getresults.app.data.App;

public class PebbleDisconnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PebbleConnectedReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, "Event: Pebble is now disconnected");
        App.getPebbleConnector().isPebbleConnected();
    }
}
