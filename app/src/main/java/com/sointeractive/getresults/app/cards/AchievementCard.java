package com.sointeractive.getresults.app.cards;

import android.content.Context;

import com.sointeractive.getresults.app.data.isaacloud.Achievement;

import it.gmariotti.cardslib.library.internal.Card;

public class AchievementCard extends Card {

    private final Achievement achievementData;

    public AchievementCard(final Context context, final Achievement a) {
        super(context);
        this.achievementData = a;
        setTitle(a.getLabel());
        setShadow(false);
    }

    public Achievement getAchievementData() {
        return achievementData;
    }


}
