package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker.NewPeopleChecker;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Person;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class PeopleCache {
    public static final PeopleCache INSTANCE = new PeopleCache();

    private static final String TAG = PeopleCache.class.getSimpleName();

    private int observedRoom = -1;

    private SparseArray<Collection<ResponseItem>> peopleResponses = new SparseArray<Collection<ResponseItem>>();

    private PeopleCache() {
        // Exists only to defeat instantiation.
    }

    public void setObservedRoom(final int observedRoom) {
        Log.i(TAG, "Action: Set observed room to: " + observedRoom);
        this.observedRoom = observedRoom;
    }

    public void clearObservedRoom() {
        observedRoom = -1;
    }

    public Collection<ResponseItem> getData(final int room) {
        if (peopleResponses.size() == 0) {
            reload();
        }
        return getPeopleRoomResponse(room);
    }

    public void reload() {
        final SparseArray<Collection<ResponseItem>> oldResponses = peopleResponses;

        peopleResponses = new SparseArray<Collection<ResponseItem>>();
        final Collection<Person> people = App.getDataManager().getPeople();
        updatePeopleList(people);

        findChanges(oldResponses);
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

    private void findChanges(final SparseArray<Collection<ResponseItem>> oldResponses) {
        Log.d(TAG, "Check: Changes in roomId: " + observedRoom);
        final Collection<ResponseItem> oldResponsesRoom = oldResponses.get(observedRoom, new LinkedList<ResponseItem>());
        final Collection<ResponseItem> newResponsesRoom = peopleResponses.get(observedRoom, new LinkedList<ResponseItem>());
        NewPeopleChecker.check(oldResponsesRoom, newResponsesRoom);
    }

    private Collection<ResponseItem> getPeopleRoomResponse(final int room) {
        Collection<ResponseItem> response = peopleResponses.get(room);
        if (response == null) {
            response = new LinkedList<ResponseItem>();
        }
        return response;
    }

    public int getSize(final int room) {
        return getData(room).size();
    }

    public void clear() {
        peopleResponses.clear();
        clearObservedRoom();
    }
}
