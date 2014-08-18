package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.data.App;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by mac on 06.08.2014.
 */
public class ProfileCard extends Card {

    private TextView name;

    public ProfileCard(Context context, int layout) {
        super(context, layout);
        this.setShadow(false);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Example on the card

        this.name = (TextView) view.findViewById(R.id.profile_card_text_name);
        this.name.setText(App.loadUserData().getName());


    }

}
