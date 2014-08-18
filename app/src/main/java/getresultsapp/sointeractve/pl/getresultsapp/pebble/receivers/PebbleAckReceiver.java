package getresultsapp.sointeractve.pl.getresultsapp.pebble.receivers;

import android.content.Context;
import android.util.Log;

import com.sointeractive.android.kit.PebbleKit;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.PebbleConnector;

public class PebbleAckReceiver extends PebbleKit.PebbleAckReceiver {
    private static final String TAG = PebbleAckReceiver.class.getSimpleName();

    public PebbleAckReceiver() {
        super(Settings.PEBBLE_APP_UUID);
    }

    @Override
    public void receiveAck(final Context context, final int transactionId) {
        Log.i(TAG, "Event: Received Ack from Pebble, transactionId=" + transactionId);
        final PebbleConnector pebbleConnector = App.getPebbleConnector();
        pebbleConnector.onAckReceived();
        pebbleConnector.sendNext();
    }
}
