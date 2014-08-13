package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker;

import android.util.Log;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.communication.Responder;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.PersonInResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class NewPeopleChecker {
    private static final String TAG = NewPeopleChecker.class.getSimpleName();

    public static void check(final Collection<ResponseItem> oldPeople, final Collection<ResponseItem> newPeople) {
        final Set<ResponseItem> oldPeopleSet = new HashSet<ResponseItem>(oldPeople);
        final Set<ResponseItem> newPeopleSet = new HashSet<ResponseItem>(newPeople);
        final Set<ResponseItem> peopleIn = Sets.difference(newPeopleSet, oldPeopleSet).immutableCopy();
        final Set<ResponseItem> peopleOut = Sets.difference(oldPeopleSet, newPeopleSet).immutableCopy();

        notifyPeopleIn(peopleIn);
        notifyPeopleOut(peopleOut);
    }

    private static void notifyPeopleIn(final Collection<ResponseItem> people) {
        if (!people.isEmpty()) {
            Log.i(TAG, "Checker: New people entered observed room");
            Responder.sendResponseItemsToPebble(people);
        }
    }

    private static void notifyPeopleOut(final Collection<ResponseItem> people) {
        if (!people.isEmpty()) {
            Log.i(TAG, "Check: New people exited observed room");
            final Collection<ResponseItem> response = getPeopleOutResponse(people);
            Responder.sendResponseItemsToPebble(response);
        }
    }

    private static Collection<ResponseItem> getPeopleOutResponse(final Iterable<ResponseItem> people) {
        final Collection<ResponseItem> peopleOut = new LinkedList<ResponseItem>();
        for (final ResponseItem responseItem : people) {
            final PersonInResponse personInResponse = (PersonInResponse) responseItem;
            peopleOut.add(personInResponse.toPersonOutResponse());
        }
        return peopleOut;
    }
}
