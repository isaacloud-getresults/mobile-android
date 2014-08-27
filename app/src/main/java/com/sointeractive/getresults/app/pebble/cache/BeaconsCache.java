package com.sointeractive.getresults.app.pebble.cache;

import android.util.Log;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.pebble.checker.BeaconsInfoChangeChecker;
import com.sointeractive.getresults.app.pebble.responses.BeaconResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BeaconsCache {
    public static final BeaconsCache INSTANCE = new BeaconsCache();

    private static final String TAG = BeaconsCache.class.getSimpleName();

    private Collection<ResponseItem> beaconsResponse = new LinkedList<ResponseItem>();
    private List<List<ResponseItem>> beaconPages = new LinkedList<List<ResponseItem>>();

    private int observedPage = -1;

    private BeaconsCache() {
        // Exists only to defeat instantiation.
    }

    public Collection<ResponseItem> getData() {
        return beaconsResponse;
    }

    public void reload() {
        final List<List<ResponseItem>> oldBeaconPages = beaconPages;
        final Collection<Location> rooms = App.getDataManager().getLocations();
        loadNewResponses(rooms);
        paginateBeacons();

        findChanges(oldBeaconPages);
    }

    private void loadNewResponses(final Iterable<Location> rooms) {
        beaconsResponse = new LinkedList<ResponseItem>();
        for (final Location room : rooms) {
            final int peopleNumber = PeopleCache.INSTANCE.getSize(room.getId());
            final int peoplePages = PeopleCache.INSTANCE.getPeoplePagesNumber(room.getId());
            beaconsResponse.add(room.toBeaconResponse(peopleNumber, peoplePages));
        }
    }

    private void paginateBeacons() {
        beaconPages = new LinkedList<List<ResponseItem>>();
        beaconPages.add(new LinkedList<ResponseItem>());
        int pageNumber = 0;
        int items = 0;
        for (final ResponseItem generalResponse : beaconsResponse) {
            final BeaconResponse response = (BeaconResponse) generalResponse;
            response.setIsMore();
            if (items >= Settings.MAX_BEACONS_PER_PAGE) {
                items = 0;
                pageNumber += 1;
                beaconPages.add(new LinkedList<ResponseItem>());
            }
            items += 1;
            response.setPageNumber(pageNumber);
            beaconPages.get(pageNumber).add(response);
        }
    }

    private void findChanges(final List<List<ResponseItem>> oldBeaconPages) {
        try {
            BeaconsInfoChangeChecker.check(oldBeaconPages.get(observedPage), beaconPages.get(observedPage));
        } catch (final IndexOutOfBoundsException e) {
            if (observedPage == -1) {
                Log.d(TAG, "No beacons page is observed");
            } else {
                Log.e(TAG, "Cannot check beacons on observed page: " + observedPage);
            }
        }
    }

    public List<ResponseItem> getBeaconsPage(final int pageNumber) {
        try {
            final List<ResponseItem> beaconsPage = beaconPages.get(pageNumber);
            final ResponseItem lastResponse = beaconsPage.get(beaconsPage.size() - 1);
            final BeaconResponse lastBeaconResponse = (BeaconResponse) lastResponse;
            lastBeaconResponse.setLast();
            return beaconsPage;
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: Cannot get page " + pageNumber);
            return new LinkedList<ResponseItem>();
        }
    }

    public int getBeaconPagesNumber() {
        return beaconPages.size();
    }

    public String getRoomName(final int roomId) {
        for (final ResponseItem responseItem : beaconsResponse) {
            final BeaconResponse beacon = (BeaconResponse) responseItem;
            if (beacon.getId() == roomId) {
                return beacon.getName();
            }
        }

        return "";
    }

    public void clear() {
        beaconsResponse.clear();
        paginateBeacons();
        clearObservedPage();
    }

    public void setObservedPage(final int observedPage) {
        Log.i(TAG, "Action: Set observed page to: " + observedPage);
        this.observedPage = observedPage;
    }

    public void clearObservedPage() {
        observedPage = -1;
    }
}
