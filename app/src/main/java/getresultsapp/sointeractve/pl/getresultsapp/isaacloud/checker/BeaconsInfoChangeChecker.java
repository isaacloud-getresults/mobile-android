package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker;

import android.util.Log;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.communication.Responder;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class BeaconsInfoChangeChecker {
    private static final String TAG = BeaconsInfoChangeChecker.class.getSimpleName();

    public static void check(final Collection<ResponseItem> oldBeacons, final Collection<ResponseItem> newBeacons) {
        final Set<ResponseItem> changedBeacons = getChangedBeacons(oldBeacons, newBeacons);
        if (!changedBeacons.isEmpty()) {
            Log.i(TAG, "Checker: Beacons info changed");
            Responder.sendResponseItemsToPebble(changedBeacons);
        }
    }

    private static Set<ResponseItem> getChangedBeacons(final Collection<ResponseItem> oldBeacons, final Collection<ResponseItem> newBeacons) {
        final Set<ResponseItem> oldBeaconsSet = new HashSet<ResponseItem>(oldBeacons);
        final Set<ResponseItem> newBeaconsSet = new HashSet<ResponseItem>(newBeacons);
        return Sets.difference(newBeaconsSet, oldBeaconsSet).immutableCopy();
    }
}
