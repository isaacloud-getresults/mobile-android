package getresultsapp.sointeractve.pl.getresultsapp.fragments;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.activities.MainActivity;
import getresultsapp.sointeractve.pl.getresultsapp.cards.AchievementCard;
import getresultsapp.sointeractve.pl.getresultsapp.cards.ProfileCard;
import getresultsapp.sointeractve.pl.getresultsapp.cards.StatusCard;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import it.gmariotti.cardslib.library.view.CardView;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    ArrayList<Card> achievementCards = new ArrayList<Card>();
    Context context;
    CardGridArrayAdapter cardGridAdapter;
    ProfileCard profileCard;
    CardView cardView;

    private BroadcastReceiver receiverProfile = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive called");
            Toast.makeText(context, "NEW ACHIEVEMENT UNLOCKED!" + "\n" + intent.getStringExtra("label"), Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
            initAchievementCards();

        }
    };

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverProfile,
                new IntentFilter(Settings.broadcastIntentNewAchievement));
        context = this.getActivity();
        initAchievementCards();
        profileCard = new ProfileCard(context,R.layout.profile_card_content);
        cardGridAdapter = new CardGridArrayAdapter(context,achievementCards);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        // PROFILE CARD INIT
        cardView = (CardView) view.findViewById(R.id.cardProfile);
        cardView.setCard(this.profileCard);
        // ACHIEVEMENTS GRID INIT
        CardGridView gridView = (CardGridView) view.findViewById(R.id.achievementsGrid);
        if (gridView!=null){
            gridView.setAdapter(cardGridAdapter);
        }
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void initAchievementCards () {
        for ( Achievement a: App.getDataManager().getAchievements()) {
            //Create a Card
            Card card = new AchievementCard(context, a);
            achievementCards.add(card);
        }
    }







}