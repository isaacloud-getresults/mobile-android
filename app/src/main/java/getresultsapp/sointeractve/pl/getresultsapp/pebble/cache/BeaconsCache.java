package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.config.IsaaCloudSettings;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker.BeaconsInfoChangeChecker;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.RoomIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers.RoomsProvider;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.BeaconResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class BeaconsCache {
    public static final BeaconsCache INSTANCE = new BeaconsCache();

    private Collection<ResponseItem> beaconsResponse = new LinkedList<ResponseItem>();

    private BeaconsCache() {
        // Exists only to defeat instantiation.
    }

    public Collection<ResponseItem> getData() {
        if (beaconsResponse.isEmpty()) {
            reload();
        }
        return beaconsResponse;
    }

    public void reload() {
        final Collection<ResponseItem> oldBeaconsResponse = beaconsResponse;
        final Collection<RoomIC> rooms = RoomsProvider.INSTANCE.getData();
        loadNewResponses(rooms);

        BeaconsInfoChangeChecker.check(oldBeaconsResponse, beaconsResponse);
    }

    private void loadNewResponses(final Iterable<RoomIC> rooms) {
        beaconsResponse = new LinkedList<ResponseItem>();
        for (final RoomIC room : rooms) {
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

        return IsaaCloudSettings.ROOM_NOT_FOUND_NAME;
    }

    public void clear() {
        RoomsProvider.INSTANCE.clear();
        beaconsResponse.clear();
    }
}
