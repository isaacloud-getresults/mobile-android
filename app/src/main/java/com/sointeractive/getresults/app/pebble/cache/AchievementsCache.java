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
    private static List<List<ResponseItem>> achievementPages = new LinkedList<List<ResponseItem>>();
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
        final List<List<ResponseItem>> oldAchievementPages = achievementPages;

        final Collection<Achievement> achievements = App.getDataManager().getAchievements();
        achievementsResponse = makeResponse(achievements);
        paginateAchievements();

        findChanges(oldAchievementPages);
    }

    private void findChanges(final List<List<ResponseItem>> oldAchievementPages) {
        try {
            NewAchievementsChecker.check(oldAchievementPages.get(observedPage), achievementPages.get(observedPage));
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
        achievementPages = new LinkedList<List<ResponseItem>>();
        achievementPages.add(new LinkedList<ResponseItem>());
        int pageNumber = 0;
        int items = 0;
        for (ResponseItem generalResponse : achievementsResponse) {
            AchievementInResponse response = (AchievementInResponse) generalResponse;
            response.setIsMore();
            if (items >= Settings.MAX_ACHIEVEMENTS_PER_PAGE) {
                items = 0;
                pageNumber += 1;
                achievementPages.add(new LinkedList<ResponseItem>());
            }
            items += 1;
            response.setPageNumber(pageNumber);
            achievementPages.get(pageNumber).add(response);
        }
    }

    public int getAchievementPagesNumber() {
        return achievementPages.size();
    }

    public List<ResponseItem> getAchievementPage(final int pageNumber) {
        try {
            final List<ResponseItem> achievementsPage = achievementPages.get(pageNumber);
            final ResponseItem lastResponse = achievementsPage.get(achievementsPage.size() - 1);
            final AchievementInResponse lastAchievementResponse = (AchievementInResponse) lastResponse;
            lastAchievementResponse.setLast();
            return achievementsPage;
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: Cannot get page " + pageNumber);
            return new LinkedList<ResponseItem>();
        }
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

    public void clearObservedPage() {
        observedPage = -1;
    }
}
