package com.sointeractive.getresults.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.cards.AchievementCard;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;


public class AchievementsFragment extends Fragment {

    private static final String TAG = AchievementsFragment.class.getSimpleName();
    private final BroadcastReceiver receiverAchievements = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "Event: onReceive called");
            Toast.makeText(context, "NEW ACHIEVEMENT UNLOCKED!" + "\n" + intent.getStringExtra("label"), Toast.LENGTH_LONG).show();
//            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(250);
            initAchievementCards();
        }
    };
    private final ArrayList<Card> achievementCards = new ArrayList<Card>();
    private Context context;
    private CardGridArrayAdapter cardGridAdapter;
    private OnFragmentInteractionListener mListener;

    public static AchievementsFragment newInstance() {
        final AchievementsFragment f = new AchievementsFragment();
        final Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverAchievements,
                new IntentFilter(Settings.BROADCAST_INTENT_NEW_ACHIEVEMENT));
        context = getActivity();
        cardGridAdapter = new CardGridArrayAdapter(context, achievementCards);
        initAchievementCards();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        // ACHIEVEMENTS GRID INIT
        final CardGridView gridView = (CardGridView) view.findViewById(R.id.achievementsGrid);
        if (gridView != null) {
            gridView.setAdapter(cardGridAdapter);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void initAchievementCards() {
        if (!achievementCards.isEmpty()) {
            achievementCards.clear();
        }
        for (final Achievement a : App.getDataManager().getAchievements()) {
            //Create a Card
            final Card card = new AchievementCard(context, a);
            achievementCards.add(card);
        }
        cardGridAdapter.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
