package com.sointeractive.getresults.app.pebble.checker;

import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;

import java.util.Collection;

public class NewAchievementsNotifier {
    public static void notifyAchievements(final Collection<Achievement> changedAchievements) {
        sendNotification(changedAchievements);
    }

    private static void sendListItems(final Iterable<Achievement> changedAchievements) {
        // TODO: Check only observed achievements page
//        final Collection<ResponseItem> responseItems = AchievementsCache.makeResponse(changedAchievements);
//        App.getPebbleConnector().sendDataToPebble(responseItems);
    }

    private static void sendNotification(final Collection<Achievement> changedAchievements) {
        final String title = getTitle(changedAchievements);
        final String body = getBody(changedAchievements);
        App.getPebbleConnector().sendNotification(title, body);
    }

    private static String getTitle(final Collection<Achievement> changedAchievements) {
        final StringBuilder titleBuilder = new StringBuilder();

        titleBuilder.append("New Achievement");
        if (changedAchievements.size() > 1) {
            titleBuilder.append("s");
        }

        return titleBuilder.toString();
    }

    private static String getBody(final Iterable<Achievement> changedAchievements) {
        final StringBuilder bodyBuilder = new StringBuilder();

        for (final Achievement newAchievement : changedAchievements) {
            bodyBuilder.append("-- ");
            bodyBuilder.append(newAchievement.getLabel());
            bodyBuilder.append("\n");
        }

        return bodyBuilder.toString();
    }
}
