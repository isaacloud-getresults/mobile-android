package getresultsapp.sointeractve.pl.getresultsapp.pebble.receivers;

import android.content.Context;
import android.util.Log;

import com.sointeractive.android.kit.PebbleKit;

import getresultsapp.sointeractve.pl.getresultsapp.config.PebbleSettings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.utils.PebbleConnector;

public class PebbleNackReceiver extends PebbleKit.PebbleNackReceiver {
    private static final String TAG = PebbleNackReceiver.class.getSimpleName();

    public PebbleNackReceiver() {
        super(PebbleSettings.PEBBLE_APP_UUID);
    }

    @Override
    public void receiveNack(final Context context, final int transactionId) {
        Log.e(TAG, "Event: Received Nack from Pebble, transactionId=" + transactionId);
        final PebbleConnector pebbleConnector = App.getPebbleConnector();
        pebbleConnector.sendNext();
    }
}
