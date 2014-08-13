package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.checker.NewAchievementsNotifier;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.AchievementIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.UserIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.tasks.GetAchievementsTask;

public class AchievementsProvider {
    public final static AchievementsProvider INSTANCE = new AchievementsProvider();


    private Collection<AchievementIC> achievementsIC = new LinkedList<AchievementIC>();

    private AchievementsProvider() {
        // Exists only to defeat instantiation.
    }


    public Collection<AchievementIC> getData() {
        reload();
        return achievementsIC;
    }

    private void reload() {
        final GetAchievementsTask getAchievements = new GetAchievementsTask();
        try {
            final UserIC user = UserProvider.INSTANCE.getData();
            if (user == null) {
                return;
            }

            final Collection<AchievementIC> oldAchievements = achievementsIC;
            final Collection<AchievementIC> newAchievements = getAchievements.execute(user.getId()).get();
            if (newAchievements.size() > oldAchievements.size()) {
                achievementsIC = newAchievements;
                NewAchievementsNotifier.findDifference(oldAchievements, newAchievements);
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        achievementsIC.clear();
    }
}
