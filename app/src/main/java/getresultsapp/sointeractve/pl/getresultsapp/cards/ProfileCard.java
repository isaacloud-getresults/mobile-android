package getresultsapp.sointeractve.pl.getresultsapp.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by mac on 06.08.2014.
 */
public class ProfileCard extends Card {

    TextView name;

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
