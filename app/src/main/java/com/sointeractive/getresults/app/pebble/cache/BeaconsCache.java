package com.sointeractive.getresults.app.pebble.cache;

import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.pebble.checker.BeaconsInfoChangeChecker;
import com.sointeractive.getresults.app.pebble.responses.BeaconResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;

public class BeaconsCache {
    public static final BeaconsCache INSTANCE = new BeaconsCache();

    private Collection<ResponseItem> beaconsResponse = new LinkedList<ResponseItem>();

    private BeaconsCache() {
        // Exists only to defeat instantiation.
    }

    public Collection<ResponseItem> getData() {
        return beaconsResponse;
    }

    public void reload() {
        final Collection<ResponseItem> oldBeaconsResponse = beaconsResponse;
        final Collection<Location> rooms = App.getDataManager().getLocations();
        loadNewResponses(rooms);

        BeaconsInfoChangeChecker.check(oldBeaconsResponse, beaconsResponse);
    }

    private void loadNewResponses(final Iterable<Location> rooms) {
        beaconsResponse = new LinkedList<ResponseItem>();
        for (final Location room : rooms) {
            final int peopleNumber = PeopleCache.INSTANCE.getSize(room.getId());
            final int peoplePages = PeopleCache.INSTANCE.getPeoplePages(room.getId());
            beaconsResponse.add(room.toBeaconResponse(peopleNumber, peoplePages));
        }
    }

    public int getSize() {
        return beaconsResponse.size();
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
    }

    public int getMemoryUsage() {
        int size = 0;
        for (ResponseItem responseItem : beaconsResponse) {
            size += responseItem.getSize();
        }
        return size;
    }
}
