package getresultsapp.sointeractve.pl.getresultsapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import getresultsapp.sointeractve.pl.getresultsapp.data.Notification;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class ProfileFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<Achievement>> {

    MainActivity context;
    ArrayList<Achievement> array;
    boolean isLoaded = false;
    AchievementAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
        array = new ArrayList<Achievement>();
        adapter = new AchievementAdapter(context);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Achievement>> onCreateLoader(int arg0, Bundle arg1) {
        System.out.println("DataListFragment.onCreateLoader");
        return new DataListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Achievement>> arg0,
                               List<Achievement> data) {
        adapter.setData(data);
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Achievement>> arg0) {
        adapter.setData(null);
    }

    public static class DataListLoader extends
            AsyncTaskLoader<List<Achievement>> {

        UserData userData;
        List<Achievement> mModels;

        public DataListLoader(Context context) {
            super(context);
        }

        @Override    public List<Achievement> loadInBackground() {
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

        /**
         * Called when there is new data to deliver to the client. The super
         * class will take care of delivering it; the implementation here just
         * adds a little more logic.
         */
        @Override
        public void deliverResult(List<Achievement> listOfData) {
            if (isReset()) {
                // An async query came in while the loader is stopped. We
                // don't need the result.
                if (listOfData != null) {
                    onReleaseResources(listOfData);
                }
            }
            List<Achievement> oldApps = listOfData;
            mModels = listOfData;
            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(listOfData);
            }
            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (mModels != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mModels);
            }
            if (takeContentChanged() || mModels == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<Achievement> apps) {
            super.onCanceled(apps);
            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();
            // Ensure the loader is stopped
            onStopLoading();
            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mModels != null) {
                onReleaseResources(mModels);
                mModels = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated with
         * an actively loaded data set.
         */
        protected void onReleaseResources(List<Achievement> apps) {
        }

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