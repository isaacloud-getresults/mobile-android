package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;

import it.gmariotti.cardslib.library.internal.Card;

public class AchievementCard extends Card {

    private final Achievement achievementData;
    private String title;

    public AchievementCard(Context context, Achievement a) {
        super(context, R.layout.achievement_card_content);
        this.achievementData = a;
        this.title = a.getLabel();
        this.setShadow(false);
    }



    public Achievement getAchievementData() {
        return achievementData;
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(title);
    }
}
