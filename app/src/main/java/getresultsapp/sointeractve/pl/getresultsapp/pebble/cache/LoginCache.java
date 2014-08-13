package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker.UserChangeChecker;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.UserIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers.UserProvider;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.EmptyResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class LoginCache {
    public static final LoginCache INSTANCE = new LoginCache();

    private ResponseItem loginResponse = EmptyResponse.INSTANCE;

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
        final UserIC newUserIC = UserProvider.INSTANCE.getUpToDateData();
        if (newUserIC == null) {
            return;
        }

        final ResponseItem oldLoginResponse = loginResponse;
        loginResponse = getLoginResponse(newUserIC);

        UserChangeChecker.check(oldLoginResponse, loginResponse);
    }

    private ResponseItem getLoginResponse(final UserIC userIC) {
        final String roomName = BeaconsCache.INSTANCE.getRoomName(userIC.getBeacon());
        final int roomsNumber = BeaconsCache.INSTANCE.getSize();
        final int achievementsNumber = AchievementsCache.INSTANCE.getSize();
        return userIC.toLoginResponse(roomName, roomsNumber, achievementsNumber);
    }

    public void clear() {
        UserProvider.INSTANCE.clear();
        loginResponse = EmptyResponse.INSTANCE;
    }
}
