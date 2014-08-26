package com.sointeractive.getresults.app.pebble.cache;

import android.util.Log;
import android.util.SparseArray;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.data.isaacloud.Person;
import com.sointeractive.getresults.app.pebble.checker.NewPeopleChecker;
import com.sointeractive.getresults.app.pebble.responses.PersonInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PeopleCache {
    public static final PeopleCache INSTANCE = new PeopleCache();

    private static final String TAG = PeopleCache.class.getSimpleName();

    private int observedRoom = -1;
    private int observedPage = -1;

    private SparseArray<Collection<ResponseItem>> peopleResponses = new SparseArray<Collection<ResponseItem>>();
    private SparseArray<List<List<ResponseItem>>> peopleInRoomPages = new SparseArray<List<List<ResponseItem>>>();

    private PeopleCache() {
        // Exists only to defeat instantiation.
    }

    public void reload() {
        final SparseArray<List<List<ResponseItem>>> oldPages = peopleInRoomPages;

        peopleResponses = new SparseArray<Collection<ResponseItem>>();
        final Collection<Person> people = App.getDataManager().getPeople();
        updatePeopleList(people);
        paginatePeople();

        findChanges(oldPages);
    }

    private void updatePeopleList(final Iterable<Person> people) {
        int room;
        for (final Person person : people) {
            room = person.getLocation();
            addPersonToRoom(room, person);
        }
    }

    private void addPersonToRoom(final int roomId, final Person person) {
        if (peopleResponses.get(roomId) == null) {
            peopleResponses.put(roomId, new ArrayList<ResponseItem>());
        }
        peopleResponses.get(roomId).add(person.toPersonInResponse());
    }

    private void paginatePeople() {
        peopleInRoomPages = new SparseArray<List<List<ResponseItem>>>();
        final List<Location> locations = App.getDataManager().getLocations();
        for (Location location : locations) {
            final int locationId = location.getId();
            peopleInRoomPages.append(locationId, paginatePeopleInRoom(locationId));
        }
    }

    private List<List<ResponseItem>> paginatePeopleInRoom(final int roomId) {
        LinkedList<List<ResponseItem>> pages = new LinkedList<List<ResponseItem>>();
        try {
            final Collection<ResponseItem> peopleInRoom = peopleResponses.get(roomId);
            if (peopleInRoom == null) {
                return pages;
            }
            pages.add(new LinkedList<ResponseItem>());
            int pageNumber = 0;
            int items = 0;
            for (ResponseItem generalResponse : peopleInRoom) {
                PersonInResponse response = (PersonInResponse) generalResponse;
                response.setIsMore();
                if (items >= Settings.MAX_PEOPLE_PER_PAGE) {
                    items = 0;
                    pageNumber += 1;
                    pages.add(new LinkedList<ResponseItem>());
                }
                items += 1;
                response.setPageNumber(pageNumber);
                pages.get(pageNumber).add(response);
            }
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Cannot get room " + roomId + " for pagination");
        }
        return pages;
    }

    private void findChanges(final SparseArray<List<List<ResponseItem>>> oldPeopleInRoomPages) {
        try {
            Log.d(TAG, "Check: Changes in roomId: " + observedRoom + " on page: " + observedPage);
            final List<List<ResponseItem>> oldRoom = oldPeopleInRoomPages.get(observedRoom);
            final List<List<ResponseItem>> newRoom = peopleInRoomPages.get(observedRoom);
            if (oldRoom == null || newRoom == null) {
                throw new IndexOutOfBoundsException();
            }
            final List<ResponseItem> oldPage = oldRoom.get(observedPage);
            final List<ResponseItem> newPage = newRoom.get(observedPage);
            NewPeopleChecker.check(oldPage, newPage);
        } catch (IndexOutOfBoundsException e) {
            if (observedPage == -1) {
                Log.d(TAG, "No people page is observed");
            } else {
                Log.e(TAG, "Cannot check people on observed page: " + observedPage);
            }
            if (observedRoom == -1) {
                Log.d(TAG, "No people room is observed");
            } else {
                Log.e(TAG, "Cannot check people on observed room: " + observedRoom);
            }
        }
    }

    public Collection<ResponseItem> getPeoplePage(final int roomId, final int pageNumber) {
        try {
            final List<List<ResponseItem>> pages = peopleInRoomPages.get(roomId);
            final List<ResponseItem> page = pages.get(pageNumber);
            final ResponseItem lastResponse = page.get(page.size() - 1);
            final PersonInResponse lastPersonResponse = (PersonInResponse) lastResponse;
            lastPersonResponse.setLast();
            return page;
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: Cannot get page " + pageNumber);
            return new LinkedList<ResponseItem>();
        }
    }

    public void clear() {
        peopleResponses.clear();
        clearObservedRoom();
        peopleInRoomPages = new SparseArray<List<List<ResponseItem>>>();
    }

    public int getPeoplePagesNumber(final int roomId) {
        return peopleInRoomPages.get(roomId).size();
    }

    public int getSize(final int room) {
        return getData(room).size();
    }

    public Collection<ResponseItem> getData(final int room) {
        if (peopleResponses.size() == 0) {
            reload();
        }
        return getPeopleRoomResponse(room);
    }

    private Collection<ResponseItem> getPeopleRoomResponse(final int room) {
        Collection<ResponseItem> response = peopleResponses.get(room);
        if (response == null) {
            response = new LinkedList<ResponseItem>();
        }
        return response;
    }

    public void setObservedRoom(final int observedRoom) {
        Log.i(TAG, "Action: Set observed room to: " + observedRoom);
        this.observedRoom = observedRoom;
    }

    public void setObservedPage(final int observedPage) {
        Log.d(TAG, "Action: Set observed page to: " + observedPage);
        this.observedPage = observedPage;
    }

    public void clearObservedRoom() {
        observedRoom = -1;
        observedPage = -1;
    }
}
