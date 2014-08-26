package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sointeractive.getresults.app.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

public class StatsCard extends Card {

    public StatsCard(final Context context) {
        super(context, R.layout.stats_card_content);
        setShadow(false);
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, final View view) {
        //Example on the card
        final ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().setupView(getCardView());
        setViewToClickToExpand(viewToClickToExpand);

    }

}
