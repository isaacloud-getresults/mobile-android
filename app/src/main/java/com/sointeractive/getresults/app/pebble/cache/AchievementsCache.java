package com.sointeractive.getresults.app.pebble.cache;

import android.util.SparseArray;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;
import com.sointeractive.getresults.app.pebble.responses.AchievementResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AchievementsCache {
    public static final AchievementsCache INSTANCE = new AchievementsCache();
    private static final SparseArray<Collection<ResponseItem>> achievementDescriptionResponses = new SparseArray<Collection<ResponseItem>>();
    private static List<List<ResponseItem>> pages = new LinkedList<List<ResponseItem>>();
    private static Collection<ResponseItem> achievementsResponse = new LinkedList<ResponseItem>();

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

    private static void paginateAchievements() {
        pages = new LinkedList<List<ResponseItem>>();
        int totalMemory = App.getPebbleConnector().getMemory();
        int currentMemory = 0;
        int pageNumber = -1;
        int items = 0;
        for (ResponseItem generalResponse : achievementsResponse) {
            AchievementResponse response = (AchievementResponse) generalResponse;
            final int responseSize = response.getSize();
            if (responseSize > totalMemory) {
                continue;
            }
            response.setIsMore();
            if (responseSize > currentMemory || items >= Settings.MAX_ITEMS_PER_PAGE) {
                items = 0;
                pageNumber += 1;
                pages.add(new LinkedList<ResponseItem>());
                currentMemory = totalMemory;
            }
            items += 1;
            currentMemory -= responseSize;
            response.setPageNumber(pageNumber);
            pages.get(pageNumber).add(response);
        }
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
        paginateAchievements();
    }

    public int getSize() {
        return achievementsResponse.size();
    }

    public void clear() {
        achievementsResponse.clear();
        paginateAchievements();
    }

    public int getAchievementPages() {
        return pages.size();
    }

    public List<ResponseItem> getAchievementsPage(final int pageNumber) {
        if (pageNumber > getAchievementPages()) {
            return new LinkedList<ResponseItem>();
        }
        final List<ResponseItem> achievementsPage = pages.get(pageNumber);
        final ResponseItem lastResponse = achievementsPage.get(achievementsPage.size() - 1);
        final AchievementResponse lastAchievementResponse = (AchievementResponse) lastResponse;
        lastAchievementResponse.setLast();
        return achievementsPage;
    }
}
