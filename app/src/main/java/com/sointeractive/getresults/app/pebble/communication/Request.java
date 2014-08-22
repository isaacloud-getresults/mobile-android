package com.sointeractive.getresults.app.pebble.communication;

import android.util.Log;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.cache.PeopleCache;
import com.sointeractive.getresults.app.pebble.responses.AchievementDescriptionResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementResponse;
import com.sointeractive.getresults.app.pebble.responses.BeaconResponse;
import com.sointeractive.getresults.app.pebble.responses.LoginResponse;
import com.sointeractive.getresults.app.pebble.responses.PersonInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public enum Request implements Sendable {
    UNKNOWN(0) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            return new LinkedList<ResponseItem>();
        }

        @Override
        void onRequest() {
            Log.d(TAG, "Error: Unknown request");
        }
    },

    LOGIN(1) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            RESPONSES_NUMBER += 1;
            final Collection<ResponseItem> responseList = new LinkedList<ResponseItem>();
            final LoginResponse loginResponse = new LoginResponse("Tester Test " + RESPONSES_NUMBER, 128, 8, "Test room", RESPONSES_NUMBER, RESPONSES_NUMBER, 1);
            responseList.add(loginResponse);
            return responseList;
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            App.getPebbleConnector().clearSendingQueue();
            App.getPebbleConnector().resetMemory();
        }
    },

    BEACONS(2) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            Collection<ResponseItem> beaconsResponse = new LinkedList<ResponseItem>();
            for (int i = 0; i < RESPONSES_NUMBER; i++) {
                beaconsResponse.add(new BeaconResponse(i, "Test room " + i, i));
            }
            return beaconsResponse;
        }

        @Override
        void onRequest() {
            super.onRequest();
            App.getPebbleConnector().deleteAchievementResponses();
        }
    },

    PEOPLE_IN_ROOM(3) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            PeopleCache.INSTANCE.setObservedRoom(query);
            Collection<ResponseItem> peopleResponse = new LinkedList<ResponseItem>();
            for (int i = 0; i < query; i++) {
                peopleResponse.add(new PersonInResponse(i, "Tester Test " + i, query));
            }
            return peopleResponse;
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            App.getPebbleConnector().clearPeopleAchievementResponses();
        }
    },

    ACHIEVEMENTS(4) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            final Collection<ResponseItem> achievementResponses = new LinkedList<ResponseItem>();
            for (int i = 0; i < RESPONSES_NUMBER; i++) {
                achievementResponses.add(new AchievementResponse(i, "Test achievement " + i, "Description " + i));
            }
            Log.d(TAG, "Sending achievements from page " + query);
            return paginateAchievements(achievementResponses).get(query - 1);
        }

        private List<Collection<ResponseItem>> paginateAchievements(final Collection<ResponseItem> allResponses) {
            List<Collection<ResponseItem>> pages = new LinkedList<Collection<ResponseItem>>();
            int totalMemory = App.getPebbleConnector().getMemory();
            int currentMemory = 0;
            int pageNumber = -1;
            AchievementResponse lastResponse = new AchievementResponse(-1, "", "");
            for (ResponseItem generalResponse : allResponses) {
                AchievementResponse response = (AchievementResponse) generalResponse;
                final int responseSize = response.getSize();
                if (responseSize > totalMemory) {
                    continue;
                }
                response.setIsMore();
                if (responseSize > currentMemory) {
                    lastResponse.setLast();
                    pageNumber += 1;
                    pages.add(new LinkedList<ResponseItem>());
                    currentMemory = totalMemory;
                }
                currentMemory -= responseSize;
                pages.get(pageNumber).add(response);
                lastResponse = response;
            }
            return pages;
        }

        @Override
        void onRequest() {
            super.onRequest();
            PeopleCache.INSTANCE.clearObservedRoom();
            App.getPebbleConnector().clearPeopleAchievementResponses();
        }
    },

    ACHIEVEMENT_DESCRIPTION(5) {
        @Override
        public Collection<ResponseItem> getSendable(final int query) {
            return AchievementDescriptionResponse.getResponse(query, "Description " + query);
        }
    };

    public static final int RESPONSE_TYPE = 1;
    public static final int RESPONSE_DATA_INDEX = 2;

    private static final String TAG = Request.class.getSimpleName();

    private static final int REQUEST_TYPE = 1;
    private static final int REQUEST_QUERY = 2;

    private static int RESPONSES_NUMBER = 20;

    private final int id;

    private Request(final int id) {
        this.id = id;
    }

    public static Collection<ResponseItem> getResponse(final PebbleDictionary data) {
        final int requestId = getRequestId(data);
        final int query = getQuery(data);
        return getSendable(requestId, query);
    }

    private static int getRequestId(final PebbleDictionary data) {
        final Long requestID = data.getInteger(Request.REQUEST_TYPE);
        return requestID.intValue();
    }

    private static int getQuery(final PebbleDictionary data) {
        if (data.contains(REQUEST_QUERY)) {
            return data.getInteger(REQUEST_QUERY).intValue();
        } else {
            return -1;
        }
    }

    private static Collection<ResponseItem> getSendable(final int requestId, final int query) {
        final Request request = getRequest(requestId);
        return request.getSendable(query);
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