package com.sointeractive.getresults.app.pebble.communication;

import android.util.Log;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.PebbleConnector;
import com.sointeractive.getresults.app.pebble.cache.AchievementsCache;
import com.sointeractive.getresults.app.pebble.cache.BeaconsCache;
import com.sointeractive.getresults.app.pebble.cache.LoginCache;
import com.sointeractive.getresults.app.pebble.cache.PeopleCache;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;

public enum Request implements Sendable {
    UNKNOWN(0) {
        @Override
        public Collection<ResponseItem> getSendable(final int mainQuery, final int secondaryQuery) {
            return new LinkedList<ResponseItem>();
        }

        @Override
        void onRequest() {
            Log.d(TAG, "Error: Unknown request");
        }
    },

    LOGIN(1) {
        @Override
        public Collection<ResponseItem> getSendable(final int mainQuery, final int secondaryQuery) {
            return LoginCache.INSTANCE.getData();
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            AchievementsCache.INSTANCE.clearObservedPage();
            App.getPebbleConnector().clearSendingQueue();
        }
    },

    BEACONS(2) {
        @Override
        public Collection<ResponseItem> getSendable(final int pageNumber, final int secondaryQuery) {
            final int pageIndex = pageNumber - 1;
            BeaconsCache.INSTANCE.setObservedPage(pageIndex);
            return BeaconsCache.INSTANCE.getBeaconsPage(pageIndex);
        }

        @Override
        void onRequest() {
            super.onRequest();
            BeaconsCache.INSTANCE.clearObservedPage();
            final PebbleConnector pebbleConnector = App.getPebbleConnector();
            pebbleConnector.deletePeopleResponses();
            pebbleConnector.deleteAchievementResponses();
        }
    },

    PEOPLE_IN_ROOM(3) {
        @Override
        public Collection<ResponseItem> getSendable(final int roomId, final int pageNumber) {
            PeopleCache.INSTANCE.setObservedRoom(roomId);
            final int pageIndex = pageNumber - 1;
            PeopleCache.INSTANCE.setObservedPage(pageIndex);
            return PeopleCache.INSTANCE.getPeoplePage(roomId, pageIndex);
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            AchievementsCache.INSTANCE.clearObservedPage();
            App.getPebbleConnector().deleteAchievementResponses();
        }
    },

    ACHIEVEMENTS(4) {
        @Override
        public Collection<ResponseItem> getSendable(final int pageNumber, final int secondaryQuery) {
            final int pageIndex = pageNumber - 1;
            AchievementsCache.INSTANCE.setObservedPage(pageIndex);
            return AchievementsCache.INSTANCE.getAchievementPage(pageIndex);
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            AchievementsCache.INSTANCE.clearObservedPage();
            App.getPebbleConnector().deletePeopleResponses();
        }
    },

    ACHIEVEMENT_DESCRIPTION(5) {
        @Override
        public Collection<ResponseItem> getSendable(final int mainQuery, final int secondaryQuery) {
            return AchievementsCache.INSTANCE.getDescriptionData(mainQuery);
        }
    };

    public static final int RESPONSE_TYPE = 1;
    public static final int RESPONSE_DATA_INDEX = 2;

    private static final String TAG = Request.class.getSimpleName();

    private static final int REQUEST_TYPE = 1;
    private static final int REQUEST_MAIN_QUERY = 2;
    private static final int REQUEST_SECONDARY_QUERY = 3;

    private final int id;

    private Request(final int id) {
        this.id = id;
    }

    public static Collection<ResponseItem> getResponse(final PebbleDictionary data) {
        final int requestId = getRequestId(data);
        final int mainQuery = getMainQuery(data);
        final int secondaryQuery = getSecondaryQuery(data);
        return getSendable(requestId, mainQuery, secondaryQuery);
    }


    private static int getRequestId(final PebbleDictionary data) {
        final Long requestID = data.getInteger(Request.REQUEST_TYPE);
        return requestID.intValue();
    }

    private static int getMainQuery(final PebbleDictionary data) {
        if (data.contains(REQUEST_MAIN_QUERY)) {
            return data.getInteger(REQUEST_MAIN_QUERY).intValue();
        } else {
            return -1;
        }
    }

    private static int getSecondaryQuery(final PebbleDictionary data) {
        if (data.contains(REQUEST_SECONDARY_QUERY)) {
            return data.getInteger(REQUEST_SECONDARY_QUERY).intValue();
        } else {
            return -1;
        }
    }

    private static Collection<ResponseItem> getSendable(final int requestId, final int mainQuery, final int secondaryQuery) {
        final Request request = getRequest(requestId);
        return request.getSendable(mainQuery, secondaryQuery);
    }

    private static Request getRequest(final int requestId) {
        final Request request = getById(requestId);
        request.onRequest();
        return request;
    }

    private static Request getById(final int id) {
        for (final Request request : Request.values()) {
            if (request.id == id)
                return request;
        }
        return Request.UNKNOWN;
    }

    void onRequest() {
        Log.i(TAG, "Request: " + name());
    }
}