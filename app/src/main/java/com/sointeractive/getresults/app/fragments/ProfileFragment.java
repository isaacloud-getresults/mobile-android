package com.sointeractive.getresults.app.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.activities.LoginActivity;
import com.sointeractive.getresults.app.cards.LogoutCard;
import com.sointeractive.getresults.app.cards.ProfileCard;
import com.sointeractive.getresults.app.cards.SettingsCard;
import com.sointeractive.getresults.app.cards.StatsCard;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;

import it.gmariotti.cardslib.library.view.CardView;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private final BroadcastReceiver receiverProfile = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");
            refreshData();

        }
    };
    private Context context;
    private ProfileCard profileCard;
    private SettingsCard settingsCard;
    private StatsCard statsCard;
    private CardView profileCardView;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        profileCard = new ProfileCard(context, R.layout.profile_card_content);
        settingsCard = new SettingsCard(context);
        statsCard = new StatsCard(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverProfile,
                new IntentFilter(Settings.BROADCAST_INTENT_UPDATE_DATA));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        // PROFILE CARD INIT
        profileCardView = (CardView) view.findViewById(R.id.cardProfile);
        CardView settingsCardView = (CardView) view.findViewById(R.id.cardSettings);
        CardView statsCardView = (CardView) view.findViewById(R.id.cardStats);
        CardView logoutCardView = (CardView) view.findViewById(R.id.cardLogout);
        profileCardView.setCard(this.profileCard);
        settingsCardView.setCard(this.settingsCard);
        statsCardView.setCard(this.statsCard);
        logoutCardView.setCard(new LogoutCard(context));

        logoutCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("logout", true);
                startActivity(i);

                getActivity().finish();
            }
        });

        return view;
    }


    void refreshData() {
        TextView counterLevel = (TextView) profileCardView.findViewById(R.id.counterLevel);
        TextView counterScore = (TextView) profileCardView.findViewById(R.id.counterScore);
        TextView counterAchievements = (TextView) profileCardView.findViewById(R.id.counterAchievements);
        counterLevel.setText(App.loadUserData().getLevel());
        counterScore.setText(App.loadUserData().getScore());
        counterAchievements.setText(App.loadUserData().getGainedAchievements());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiverProfile);
    }
}