package getresultsapp.sointeractve.pl.getresultsapp.fragments;


import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.activities.MainActivity;
import getresultsapp.sointeractve.pl.getresultsapp.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class ProfileFragment extends ListFragment {

    MainActivity context;
    static ArrayList<Achievement> array;
    boolean isLoaded = false;
    AchievementAdapter adapter;

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        array = new ArrayList<Achievement>();
        new DataListLoader().execute();
        context = (MainActivity) getActivity();
        adapter = new AchievementAdapter(context);
        adapter.setData(array);
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
        array = new ArrayList<Achievement>();
        new DataListLoader().execute();
        adapter = new AchievementAdapter(context);
        adapter.setData(array);
        setListAdapter(adapter);
    }



    public static class DataListLoader extends AsyncTask<Object,Object,Object> {

        UserData userData;
        List<Achievement> mModels;

        @Override
        protected Object doInBackground(Object... params) {
            userData = App.loadUserData();
            List<Achievement> entries = new ArrayList<Achievement>();
            try {
                HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
                HttpResponse responseUser = App
                        .getConnector()
                        .path("/admin/users/" + userData.getUserId()
                                + "/gainedachievements").withLimit(1000).get();
                JSONArray arrayUser = responseUser.getJSONArray();
                for (int i = 0; i < arrayUser.length(); i++) {
                    JSONObject json = (JSONObject) arrayUser.get(i);
                    idMap.put(json.getInt("achievement"), json.getInt("amount"));
                }
                HttpResponse responseGeneral = App.getConnector()
                        .path("/cache/achievements").withLimit(1000).get();
                JSONArray arrayGeneral = responseGeneral.getJSONArray();
                Log.d("TEST", arrayGeneral.toString(3));
                for (int i = 0; i < arrayGeneral.length(); i++) {
                    JSONObject json = (JSONObject) arrayGeneral.get(i);
                    if (idMap.containsKey(json.getInt("id"))) {
                        entries.add(
                                0,
                                new Achievement(json, true, idMap.get(json
                                        .getInt("id"))));
                    } else {

                        entries.add(new Achievement(json, false));
                        array = (ArrayList<Achievement>) entries;
                        for(Achievement a : array) Log.d("TEST", a.getDesc());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return entries;
        }
    }

    private class AchievementAdapter extends ArrayAdapter<Achievement> {
        private final LayoutInflater mInflater;

        public AchievementAdapter(Context context) {
            super(context, R.layout.fragment_achievement_item);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(ArrayList<Achievement> data) {
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