package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.isaacloud.Location;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.checker.BeaconsInfoChangeChecker;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.BeaconResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

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
            beaconsResponse.add(room.toBeaconResponse(peopleNumber));
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
}
