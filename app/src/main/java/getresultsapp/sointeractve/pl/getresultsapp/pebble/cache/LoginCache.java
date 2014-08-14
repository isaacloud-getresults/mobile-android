package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker.UserChangeChecker;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.UserData;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.EmptyResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class LoginCache {
    public static final LoginCache INSTANCE = new LoginCache();

    private ResponseItem loginResponse = EmptyResponse.INSTANCE;
    private boolean stopSending = true;

    private LoginCache() {
        // Exists only to defeat instantiation.
    }

    public Collection<ResponseItem> getData() {
        if (loginResponse instanceof EmptyResponse) {
            reload();
        }
        return getCollection();
    }

    private Collection<ResponseItem> getCollection() {
        final Collection<ResponseItem> responseList = new LinkedList<ResponseItem>();
        responseList.add(loginResponse);
        return responseList;
    }

    public void reload() {
        final UserData newUserData = App.loadUserData();
        if (newUserData == null || stopSending) {
            return;
        }

        final ResponseItem oldLoginResponse = loginResponse;
        loginResponse = getLoginResponse(newUserData);

        UserChangeChecker.check(oldLoginResponse, loginResponse);
    }

    private ResponseItem getLoginResponse(final UserData userData) {
        final String roomName = BeaconsCache.INSTANCE.getRoomName(userData.getUserLocationId());
        final int roomsNumber = BeaconsCache.INSTANCE.getSize();
        final int achievementsNumber = AchievementsCache.INSTANCE.getSize();
        return userData.toLoginResponse(roomName, roomsNumber, achievementsNumber);
    }

    public void clear() {
        loginResponse = EmptyResponse.INSTANCE;
    }

    public void logIn() {
        stopSending = false;
    }
}
