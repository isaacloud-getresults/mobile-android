package com.sointeractive.getresults.app.pebble.cache;

import android.util.Log;
import android.util.SparseArray;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;
import com.sointeractive.getresults.app.pebble.checker.NewAchievementsChecker;
import com.sointeractive.getresults.app.pebble.responses.AchievementInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AchievementsCache {
    public static final AchievementsCache INSTANCE = new AchievementsCache();

    private static final String TAG = AchievementsCache.class.getSimpleName();

    private static final SparseArray<Collection<ResponseItem>> achievementDescriptionResponses = new SparseArray<Collection<ResponseItem>>();
    private static List<List<ResponseItem>> pages = new LinkedList<List<ResponseItem>>();
    private static Collection<ResponseItem> achievementsResponse = new LinkedList<ResponseItem>();

    private int observedPage = -1;

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

    public void reload() {
        final List<List<ResponseItem>> oldAchievementPages = pages;

        final Collection<Achievement> achievements = App.getDataManager().getAchievements();
        achievementsResponse = makeResponse(achievements);
        paginateAchievements();

        findChanges(oldAchievementPages);
    }

    private void findChanges(final List<List<ResponseItem>> oldAchievementPages) {
        try {
            NewAchievementsChecker.check(oldAchievementPages.get(observedPage), pages.get(observedPage));
        } catch (IndexOutOfBoundsException e) {
            if (observedPage == -1) {
                Log.d(TAG, "No achievement page is observed");
            } else {
                Log.e(TAG, "Cannot check achievements on observed page: " + observedPage);
            }
        }
    }

    public void clear() {
        achievementsResponse.clear();
        paginateAchievements();
        clearObservedPage();
    }

    private void paginateAchievements() {
        pages = new LinkedList<List<ResponseItem>>();
        int totalMemory = App.getPebbleConnector().getMemory();
        int currentMemory = 0;
        int pageNumber = -1;
        int items = 0;
        for (ResponseItem generalResponse : achievementsResponse) {
            AchievementInResponse response = (AchievementInResponse) generalResponse;
            final int responseSize = response.getSize();
            if (responseSize > totalMemory) {
                continue;
            }
            response.setIsMore();
            if (responseSize > currentMemory || items >= Settings.MAX_ACHIEVEMENTS_PER_PAGE) {
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

    public void clearObservedPage() {
        observedPage = -1;
    }

    public int getAchievementPages() {
        return pages.size();
    }

    public List<ResponseItem> getAchievementsPage(final int pageNumber) {
        if (pageNumber >= getAchievementPages()) {
            return new LinkedList<ResponseItem>();
        }
        final List<ResponseItem> achievementsPage = pages.get(pageNumber);
        final ResponseItem lastResponse = achievementsPage.get(achievementsPage.size() - 1);
        final AchievementInResponse lastAchievementResponse = (AchievementInResponse) lastResponse;
        lastAchievementResponse.setLast();
        return achievementsPage;
    }

    public int getSize() {
        return achievementsResponse.size();
    }

    public Collection<ResponseItem> getDescriptionData(final int id) {
        return achievementDescriptionResponses.get(id, new LinkedList<ResponseItem>());
    }

    public void setObservedPage(final int observedPage) {
        Log.i(TAG, "Action: Set observed page to: " + observedPage);
        this.observedPage = observedPage;
    }
}
