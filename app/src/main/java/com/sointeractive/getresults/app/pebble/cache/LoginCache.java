package com.sointeractive.getresults.app.pebble.cache;

import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.UserData;
import com.sointeractive.getresults.app.pebble.checker.UserChangeChecker;
import com.sointeractive.getresults.app.pebble.responses.EmptyResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;

public class LoginCache {
    public static final LoginCache INSTANCE = new LoginCache();

    private ResponseItem loginResponse = EmptyResponse.INSTANCE;
    private boolean stopSending = true;

    private LoginCache() {
        // Exists only to defeat instantiation.
    }

    public Collection<ResponseItem> getData() {
        if (loginResponse instanceof EmptyResponse) {
            reload(false);
        }
        return getCollection();
    }

    private Collection<ResponseItem> getCollection() {
        final Collection<ResponseItem> responseList = new LinkedList<ResponseItem>();
        responseList.add(loginResponse);
        return responseList;
    }

    public void reload(boolean findChanges) {
        final UserData newUserData = App.loadUserData();
        if (newUserData == null || stopSending) {
            return;
        }

        final ResponseItem oldLoginResponse = loginResponse;
        loginResponse = getLoginResponse(newUserData);

        if (findChanges) {
            UserChangeChecker.check(oldLoginResponse, loginResponse);
        }
    }

    private ResponseItem getLoginResponse(final UserData userData) {
        final String roomName = BeaconsCache.INSTANCE.getRoomName(userData.getUserLocationId());
        final int roomsNumber = BeaconsCache.INSTANCE.getSize();
        final int achievementsNumber = AchievementsCache.INSTANCE.getSize();
        return userData.toLoginResponse(roomName, roomsNumber, achievementsNumber);
    }

    public void clear() {
        loginResponse = EmptyResponse.INSTANCE;
        stopSending = true;
    }

    public void logIn() {
        stopSending = false;
    }
}
