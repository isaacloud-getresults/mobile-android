package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sointeractive.getresults.app.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

public class SettingsCard extends Card {

    public SettingsCard(Context context) {
        super(context, R.layout.settings_card_content);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Example on the card
        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().setupView(getCardView());
        setViewToClickToExpand(viewToClickToExpand);

    }

}
