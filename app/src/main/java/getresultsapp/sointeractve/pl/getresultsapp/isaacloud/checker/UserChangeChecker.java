package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker;

import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.communication.Responder;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class UserChangeChecker {
    private static final String TAG = UserChangeChecker.class.getSimpleName();

    public static void check(final ResponseItem oldUser, final ResponseItem newUser) {
        if (!newUser.equals(oldUser)) {
            Log.i(TAG, "Checker: User data changed");
            final Collection<ResponseItem> loginResponse = new LinkedList<ResponseItem>();
            loginResponse.add(newUser);
            Responder.sendResponseItemsToPebble(loginResponse);
        }
    }
}
