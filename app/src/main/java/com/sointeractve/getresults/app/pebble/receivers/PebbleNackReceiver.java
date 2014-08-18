package com.sointeractve.getresults.app.pebble.receivers;

import android.content.Context;
import android.util.Log;

import com.sointeractive.android.kit.PebbleKit;
import com.sointeractve.getresults.app.config.Settings;
import com.sointeractve.getresults.app.data.App;
import com.sointeractve.getresults.app.pebble.PebbleConnector;

public class PebbleNackReceiver extends PebbleKit.PebbleNackReceiver {
    private static final String TAG = PebbleNackReceiver.class.getSimpleName();

    public PebbleNackReceiver() {
        super(Settings.PEBBLE_APP_UUID);
    }

    @Override
    public void receiveNack(final Context context, final int transactionId) {
        Log.e(TAG, "Event: Received Nack from Pebble, transactionId=" + transactionId);
        final PebbleConnector pebbleConnector = App.getPebbleConnector();
        pebbleConnector.sendNext();
    }
}
