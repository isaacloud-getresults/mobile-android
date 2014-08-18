package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker;

import java.util.Collection;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.AchievementsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.communication.Responder;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class NewAchievementsNotifier {
    public static void notifyAchievements(final Collection<Achievement> changedAchievements) {
        sendListItems(changedAchievements);
        sendNotification(changedAchievements);
    }

    private static void sendListItems(final Iterable<Achievement> changedAchievements) {
        final Collection<ResponseItem> responseItems = AchievementsCache.makeResponse(changedAchievements);
        Responder.sendResponseItemsToPebble(responseItems);
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
