package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import android.util.SparseArray;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class AchievementsCache {
    public static final AchievementsCache INSTANCE = new AchievementsCache();
    private static final SparseArray<Collection<ResponseItem>> achievementDescriptionResponses = new SparseArray<Collection<ResponseItem>>();

    private Collection<ResponseItem> achievementsResponse = new LinkedList<ResponseItem>();

    private AchievementsCache() {
        // Exists only to defeat instantiation.
    }

    public static Collection<ResponseItem> makeResponse(final Iterable<Achievement> collection) {
        final Collection<ResponseItem> response = new LinkedList<ResponseItem>();
        for (final Achievement achievement : collection) {
            response.add(achievement.toAchievementResponse());

            final Collection<ResponseItem> achievementDescriptionResponse = achievement.toAchievementDescriptionResponse();
            achievementDescriptionResponses.put(achievement.getId(), achievementDescriptionResponse);
        }
        return response;
    }

    public Collection<ResponseItem> getData() {
        return achievementsResponse;
    }

    public Collection<ResponseItem> getDescriptionData(final int id) {
        return achievementDescriptionResponses.get(id, new LinkedList<ResponseItem>());
    }

    public void reload() {
        final Collection<Achievement> achievements = App.getDataManager().getAchievements();
        achievementsResponse = makeResponse(achievements);
    }

    public int getSize() {
        return achievementsResponse.size();
    }

    public void clear() {
        achievementsResponse.clear();
    }
}
