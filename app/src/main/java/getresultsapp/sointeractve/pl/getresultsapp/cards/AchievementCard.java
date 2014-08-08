package getresultsapp.sointeractve.pl.getresultsapp.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.Achievement;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by mac on 06.08.2014.
 */
public class AchievementCard extends Card {

    Achievement achievementData;

    public AchievementCard(Context context, Achievement a) {
        super(context);
        this.achievementData = a;
        this.setTitle(a.getLabel());
    }

    public Achievement getAchievementData() {
        return achievementData;
    }



}
