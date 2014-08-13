package getresultsapp.sointeractve.pl.getresultsapp.fragments;


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

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.cards.LogoutCard;
import getresultsapp.sointeractve.pl.getresultsapp.cards.ProfileCard;
import getresultsapp.sointeractve.pl.getresultsapp.cards.SettingsCard;
import getresultsapp.sointeractve.pl.getresultsapp.cards.StatsCard;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import it.gmariotti.cardslib.library.view.CardView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private BroadcastReceiver receiverProfile = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");

        }
    };
    Context context;
    ProfileCard profileCard;
    SettingsCard settingsCard;
    StatsCard statsCard;
    CardView profileCardView;

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        profileCard = new ProfileCard(context, R.layout.profile_card_content);
        settingsCard = new SettingsCard(context);
        statsCard = new StatsCard(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverProfile,
                new IntentFilter(Settings.broadcastIntent));

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

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


}