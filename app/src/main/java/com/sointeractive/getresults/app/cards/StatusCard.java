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

    public StatusCard(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        title = (TextView) view.findViewById(R.id.status_card_main_text);
        stats = (IconTextView) view.findViewById(R.id.status_card_stats);
    }

    public void initLocation(Location newLocation) {
        if (title != null) {
//            if(newLocation != null)
            title.setText(newLocation.getLabel());
        }
        if (stats != null) {
            stats.setText("{fa-users}" + " " + App.getPeopleAtLocation(newLocation).size() + "   " + "{fa-trophy}" + " " + App.getDataManager().getAchievements().size());
        }
    }
}
