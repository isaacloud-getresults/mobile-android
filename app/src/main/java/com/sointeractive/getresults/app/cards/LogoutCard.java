package com.sointeractive.getresults.app.cards;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;

public class LogoutCard extends Card {

    public LogoutCard(Context context) {
        super(context);
        this.setShadow(false);
        this.setTitle("Log out");
    }

}
