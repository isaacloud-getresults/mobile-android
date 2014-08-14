package getresultsapp.sointeractve.pl.getresultsapp.utils;

import android.util.Log;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.AchievementsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.BeaconsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.LoginCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.PeopleCache;

public class CacheManager {
    public static final CacheManager INSTANCE = new CacheManager();

    private static final String TAG = CacheManager.class.getSimpleName();

    private CacheManager() {
        // Exists only to defeat instantiation.
    }

    public void reload() {
        Log.d(TAG, "Action: Reload cache");
        clear();
        update();
    }

    public void update() {
        AchievementsCache.INSTANCE.reload();
        PeopleCache.INSTANCE.reload();
        BeaconsCache.INSTANCE.reload();
        LoginCache.INSTANCE.reload();
        Log.i(TAG, "Event: Cache reloaded");
    }

    private void clear() {
        Log.d(TAG, "Action: Clear cache");
        AchievementsCache.INSTANCE.clear();
        PeopleCache.INSTANCE.clear();
        BeaconsCache.INSTANCE.clear();
        LoginCache.INSTANCE.clear();
    }
}
