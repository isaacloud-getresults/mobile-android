package com.sointeractive.getresults.app.pebble.checker;

import android.util.Log;

import com.google.common.collect.Sets;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.responses.PersonInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class NewPeopleChecker {
    private static final String TAG = NewPeopleChecker.class.getSimpleName();

    public static void check(final Collection<ResponseItem> oldPeople, final Collection<ResponseItem> newPeople) {
        final Set<ResponseItem> oldPeopleSet = new HashSet<ResponseItem>(oldPeople);
        final Set<ResponseItem> newPeopleSet = new HashSet<ResponseItem>(newPeople);
        final Set<ResponseItem> peopleIn = Sets.difference(newPeopleSet, oldPeopleSet).immutableCopy();
        final Set<ResponseItem> peopleOut = Sets.difference(oldPeopleSet, newPeopleSet).immutableCopy();

        notifyPeopleOut(peopleOut);
        notifyPeopleIn(peopleIn);
    }

    private static void notifyPeopleIn(final Collection<ResponseItem> people) {
        if (!people.isEmpty()) {
            Log.i(TAG, "Checker: New people entered observed room");
            App.getPebbleConnector().sendDataToPebble(people);
        }
    }

    private static void notifyPeopleOut(final Collection<ResponseItem> people) {
        if (!people.isEmpty()) {
            Log.i(TAG, "Checker: New people exited observed room");
            final Collection<ResponseItem> response = getPeopleOutResponse(people);
            App.getPebbleConnector().sendDataToPebble(response);
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
