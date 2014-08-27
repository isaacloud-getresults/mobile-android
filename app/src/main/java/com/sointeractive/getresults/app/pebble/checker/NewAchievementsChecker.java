package com.sointeractive.getresults.app.pebble.checker;

import android.util.Log;

import com.google.common.collect.Sets;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.responses.AchievementInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NewAchievementsChecker {
    private static final String TAG = NewAchievementsChecker.class.getSimpleName();

    public static void check(final List<ResponseItem> oldAchievements, final List<ResponseItem> newAchievements) {
        final Set<ResponseItem> oldAchievementsSet = new HashSet<ResponseItem>(oldAchievements);
        final Set<ResponseItem> newAchievementsSet = new HashSet<ResponseItem>(newAchievements);
        final Set<ResponseItem> achievementsIn = Sets.difference(newAchievementsSet, oldAchievementsSet).immutableCopy();
        final Set<ResponseItem> achievementsOut = Sets.difference(oldAchievementsSet, newAchievementsSet).immutableCopy();

        notifyAchievementsOut(achievementsOut);
        notifyAchievementsIn(achievementsIn);
    }

    private static void notifyAchievementsIn(final Collection<ResponseItem> achievementsIn) {
        if (!achievementsIn.isEmpty()) {
            Log.i(TAG, "Checker: New achievements in observed page");
            App.getPebbleConnector().sendDataToPebble(achievementsIn);
        }
    }

    private static void notifyAchievementsOut(final Collection<ResponseItem> achievementsOut) {
        if (!achievementsOut.isEmpty()) {
            Log.i(TAG, "Checker: New achievements out of observed page");
            final Collection<ResponseItem> response = getAchievementsOutResponse(achievementsOut);
            App.getPebbleConnector().sendDataToPebble(response);
        }
    }

    private static Collection<ResponseItem> getAchievementsOutResponse(final Iterable<ResponseItem> achievements) {
        final Collection<ResponseItem> achievementsOut = new LinkedList<ResponseItem>();
        for (final ResponseItem responseItem : achievements) {
            final AchievementInResponse achievementInResponse = (AchievementInResponse) responseItem;
            achievementsOut.add(achievementInResponse.toAchievementOutResponse());
        }
        return achievementsOut;
    }
}
