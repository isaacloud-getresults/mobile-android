package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.RoomIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.tasks.GetRoomsTask;

public class RoomsProvider {
    public static final RoomsProvider INSTANCE = new RoomsProvider();

    private Collection<RoomIC> roomsIC = new LinkedList<RoomIC>();

    private RoomsProvider() {
        // Exists only to defeat instantiation.
    }


    public Collection<RoomIC> getData() {
        if (roomsIC.isEmpty()) {
            reload();
        }
        return roomsIC;
    }

    private void reload() {
        final GetRoomsTask getRooms = new GetRoomsTask();
        try {
            final Collection<RoomIC> newRoomsIC = getRooms.execute().get();
            if (newRoomsIC.size() > roomsIC.size()) {
                roomsIC = newRoomsIC;
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        roomsIC.clear();
    }
}
