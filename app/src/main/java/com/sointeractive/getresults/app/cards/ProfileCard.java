package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.data.App;

import it.gmariotti.cardslib.library.internal.Card;

public class ProfileCard extends Card {

    public ProfileCard(Context context, int layout) {
        super(context, layout);
        this.setShadow(false);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Example on the card

        final TextView name = (TextView) view.findViewById(R.id.profile_card_text_name);
        name.setText(App.loadUserData().getName());


    }

}
