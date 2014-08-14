package getresultsapp.sointeractve.pl.getresultsapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.cards.AchievementCard;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;


public class AchievementsFragment extends Fragment {

    Context context;
    CardGridArrayAdapter cardGridAdapter;
    ArrayList<Card> achievementCards = new ArrayList<Card>();

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "AchievementsFragment";
    private BroadcastReceiver receiverAchievements = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");
            Log.d(TAG,"onReceive called");
            Toast.makeText(context, "NEW ACHIEVEMENT UNLOCKED!" + "\n" + intent.getStringExtra("label"), Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
            initAchievementCards();
        }
    };

    public static AchievementsFragment newInstance() {
        AchievementsFragment f = new AchievementsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }
    public AchievementsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverAchievements,
                new IntentFilter(Settings.broadcastIntentNewAchievement));
        context = this.getActivity();
        initAchievementCards();
        cardGridAdapter = new CardGridArrayAdapter(context,achievementCards);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        // ACHIEVEMENTS GRID INIT
        CardGridView gridView = (CardGridView) view.findViewById(R.id.achievementsGrid);
        if (gridView!=null){
            gridView.setAdapter(cardGridAdapter);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public void initAchievementCards () {
        for ( Achievement a: App.getDataManager().getAchievements()) {
            //Create a Card
            Card card = new AchievementCard(context, a);
            achievementCards.add(card);
        }
    }


}
