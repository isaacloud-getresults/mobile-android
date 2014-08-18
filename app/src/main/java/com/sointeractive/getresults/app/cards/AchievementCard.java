package com.sointeractive.getresults.app.cards;

import android.content.Context;

import com.sointeractive.getresults.app.data.isaacloud.Achievement;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by mac on 06.08.2014.
 */
public class AchievementCard extends Card {

    private Achievement achievementData;

    public AchievementCard(Context context, Achievement a) {
        super(context);
        this.achievementData = a;
        this.setTitle(a.getLabel());
        this.setShadow(false);
    }

    public Achievement getAchievementData() {
        return achievementData;
    }


}
