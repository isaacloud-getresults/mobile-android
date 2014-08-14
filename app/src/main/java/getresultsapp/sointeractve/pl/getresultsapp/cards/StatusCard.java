package getresultsapp.sointeractve.pl.getresultsapp.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.Location;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by mac on 06.08.2014.
 */
public class StatusCard extends Card {

    Location actualLocation;
    TextView title;

    public StatusCard(Context context, int layout) {
        super(context, layout);
        this.setShadow(false);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Example on the card
        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().setupView(getCardView());
        setViewToClickToExpand(viewToClickToExpand);

        title = (TextView) view.findViewById(R.id.status_card_main_text);

    }

    public void initLocation (Location newLocation) {
        this.actualLocation = newLocation;
        if(title != null) {
//            if(actualLocation != null)
            title.setText(actualLocation.getLabel());
        }
    }
}
