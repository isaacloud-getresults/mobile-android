package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker;

import android.util.Log;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.AchievementIC;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.AchievementsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.communication.Responder;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class NewAchievementsNotifier {
    private static final String TAG = NewAchievementsNotifier.class.getSimpleName();

    public static void findDifference(final Collection<AchievementIC> oldAchievements, final Collection<AchievementIC> newAchievements) {
        Log.i(TAG, "Checker: New achievements found");

        final Set<AchievementIC> gainedAchievements = getGainedAchievements(oldAchievements, newAchievements);
        notifyAchievements(gainedAchievements);
    }

    private static Set<AchievementIC> getGainedAchievements(final Collection<AchievementIC> oldAchievements, final Collection<AchievementIC> newAchievements) {
        final Set<AchievementIC> oldSet = new HashSet<AchievementIC>(oldAchievements);
        final Set<AchievementIC> newSet = new HashSet<AchievementIC>(newAchievements);
        return Sets.difference(newSet, oldSet).immutableCopy();
    }

    private static void notifyAchievements(final Collection<AchievementIC> changedAchievements) {
        sendListItems(changedAchievements);
        sendNotification(changedAchievements);
    }

    // TODO: Refactor this
    private static void sendListItems(final Iterable<AchievementIC> changedAchievements) {
        final Collection<ResponseItem> responseItems = AchievementsCache.makeResponse(changedAchievements);
        Responder.sendResponseItemsToPebble(responseItems);
    }

    private static void sendNotification(final Collection<AchievementIC> changedAchievements) {
        final String title = getTitle(changedAchievements);
        final String body = getBody(changedAchievements);
        App.getPebbleConnector().sendNotification(title, body);
    }

    private static String getBody(final Iterable<AchievementIC> changedAchievements) {
        final StringBuilder bodyBuilder = new StringBuilder();

        for (final AchievementIC newAchievement : changedAchievements) {
            bodyBuilder.append("-- ");
            bodyBuilder.append(newAchievement.getName());
            bodyBuilder.append("\n");
        }

        return bodyBuilder.toString();
    }

    private static String getTitle(final Collection<AchievementIC> changedAchievements) {
        final StringBuilder titleBuilder = new StringBuilder();

        titleBuilder.append("New Achievement");
        if (changedAchievements.size() > 1) {
            titleBuilder.append("s");
        }

        return titleBuilder.toString();
    }
}
