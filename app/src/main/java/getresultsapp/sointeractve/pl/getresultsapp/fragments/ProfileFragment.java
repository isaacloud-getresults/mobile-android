package getresultsapp.sointeractve.pl.getresultsapp.fragments;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Vibrator;
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
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class ProfileFragment extends ListFragment {

    private static final String TAG = "ProfileFragment";

    MainActivity context;
    ArrayList<Achievement> array;
    AchievementAdapter adapter;
    private BroadcastReceiver receiverProfile = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive called");
            adapter.setData(App.getDataManager().getAchievements());
            adapter.notifyDataSetChanged();
            Toast.makeText(context, "NEW ACHIEVEMENT UNLOCKED!" + "\n" + intent.getStringExtra("label"), Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
        }
    };

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverProfile,
                new IntentFilter(Settings.broadcastIntentNewAchievement));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
        array = new ArrayList<Achievement>();
        adapter = new AchievementAdapter(context);
        adapter.setData(App.getDataManager().getAchievements());
        setListAdapter(adapter);
    }



    private class AchievementAdapter extends ArrayAdapter<Achievement> {
        private final LayoutInflater mInflater;

        public AchievementAdapter(Context context) {
            super(context, R.layout.fragment_achievement_item);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Achievement> data) {
            clear();
            if (data != null) {
                for (Achievement appEntry : data) {
                    add(appEntry);
                }
            }
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.fragment_achievement_item,
                        parent, false);
            } else {
                view = convertView;
            }
            Achievement achievement = getItem(position);
            TextView textLabel = (TextView) view
                    .findViewById(R.id.fragment_achievement_text_label);
            TextView textDesc = (TextView) view
                    .findViewById(R.id.fragment_achievement_text_desc);
            TextView textCounter = (TextView) view
                    .findViewById(R.id.fragment_achievement_text_counter);
            ImageView image = (ImageView) view
                    .findViewById(R.id.fragment_achievement_image);
            textLabel.setText(achievement.getLabel());
            textDesc.setText(achievement.getDesc());
            if (achievement.getCounter() != 0) {
                textCounter.setText("" + achievement.getCounter());
            }
            image.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_launcher));
            if (!achievement.isGained()) {
                view.setBackgroundColor(Color.GRAY);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }

}