package com.sointeractive.getresults.app.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Location;

import it.gmariotti.cardslib.library.internal.Card;

public class StatusCard extends Card {

    private TextView title;
    private IconTextView stats;

    public StatusCard(final Context context, final int layout) {
        super(context, layout);
        setShadow(false);
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, final View view) {

        title = (TextView) view.findViewById(R.id.status_card_main_text);
        stats = (IconTextView) view.findViewById(R.id.status_card_stats);
    }

    public void initLocation(final Location newLocation) {
        if (title != null) {
//            if(newLocation != null)
            title.setText(newLocation.getLabel());
        }
        if (stats != null) {
            stats.setText("{fa-users}" + " " + App.getPeopleAtLocation(newLocation).size() + "   " + "{fa-trophy}" + " " + App.getDataManager().getAchievements().size());
        }
    }
}
