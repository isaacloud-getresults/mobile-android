package com.sointeractive.getresults.app.pebble.receivers;

import android.content.Context;
import android.util.Log;

import com.sointeractive.android.kit.PebbleKit;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;

public class PebbleAckReceiver extends PebbleKit.PebbleAckReceiver {
    private static final String TAG = PebbleAckReceiver.class.getSimpleName();

    public PebbleAckReceiver() {
        super(Settings.PEBBLE_APP_UUID);
    }

    @Override
    public void receiveAck(final Context context, final int transactionId) {
        Log.i(TAG, "Event: Received Ack from Pebble, transactionId=" + transactionId);
        App.getPebbleConnector().onAckReceived();
    }
}
