package getresultsapp.sointeractve.pl.getresultsapp.pebble.cache;

import android.util.SparseArray;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.AchievementIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers.AchievementsProvider;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class AchievementsCache {
    public static final AchievementsCache INSTANCE = new AchievementsCache();
    private static final SparseArray<Collection<ResponseItem>> achievementDescriptionResponses = new SparseArray<Collection<ResponseItem>>();

    private Collection<ResponseItem> achievementsResponse = new LinkedList<ResponseItem>();

    private AchievementsCache() {
        // Exists only to defeat instantiation.
    }

    public static Collection<ResponseItem> makeResponse(final Iterable<AchievementIC> collection) {
        final Collection<ResponseItem> response = new LinkedList<ResponseItem>();
        for (final AchievementIC achievement : collection) {
            response.add(achievement.toAchievementResponse());

            final Collection<ResponseItem> achievementDescriptionResponse = achievement.toAchievementDescriptionResponse();
            achievementDescriptionResponses.put(achievement.getId(), achievementDescriptionResponse);
        }
        return response;
    }

    public Collection<ResponseItem> getData() {
        reloadIfNeeded();
        return achievementsResponse;
    }

    public Collection<ResponseItem> getDescriptionData(final int id) {
        reloadIfNeeded();
        return achievementDescriptionResponses.get(id, new LinkedList<ResponseItem>());
    }

    private void reloadIfNeeded() {
        if (achievementsResponse.isEmpty()) {
            final Collection<AchievementIC> achievements = AchievementsProvider.INSTANCE.getData();
            achievementsResponse = makeResponse(achievements);
        }
    }

    public void reload() {
        final Collection<AchievementIC> achievements = AchievementsProvider.INSTANCE.getData();
        achievementsResponse = makeResponse(achievements);
    }

    public int getSize() {
        return achievementsResponse.size();
    }

    public void clear() {
        AchievementsProvider.INSTANCE.clear();
        achievementsResponse.clear();
    }
}
