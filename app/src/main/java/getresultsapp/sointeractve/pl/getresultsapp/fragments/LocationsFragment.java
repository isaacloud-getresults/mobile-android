package getresultsapp.sointeractve.pl.getresultsapp.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.Location;
import getresultsapp.sointeractve.pl.getresultsapp.data.Person;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class LocationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Location>> {

    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    ArrayList<Location> locationsArray;
    HashMap<Location, List<Person>> visitorsArray;
    Context context;
    boolean isLoaded = false;

    private OnFragmentInteractionListener mListener;

    public static StatusFragment newInstance(int page, String title) {
        StatusFragment fragmentFirst = new StatusFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationsArray = new ArrayList<Location>();
        visitorsArray = new HashMap<Location, List<Person>>();
        getLoaderManager().initLoader(0, null, this);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        context = getActivity();
        listAdapter = new ExpandableListAdapter(context, locationsArray, visitorsArray);
        getLoaderManager().restartLoader(0,null,this);
        expandableListView = (ExpandableListView) view.findViewById(R.id.listView);
        expandableListView.setAdapter(listAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        context = activity.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        listAdapter = null;

    }

    @Override
    public Loader<List<Location>> onCreateLoader(int id, Bundle args) {
        Log.d("LocationsFragment",".onCreateLoader");
        return new LocationsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> data) {
        listAdapter.setLocations(data);
        // The list should now be shown.
        if (isResumed()) {
            //setListShown(true);
        } else {
            //setListShownNoAnimation(true);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
        listAdapter.setLocations(null);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // +++++++++++++++++++++++++++++++++++++++++
    // ======== LOCATIONS LOADER CLASS =========
    // +++++++++++++++++++++++++++++++++++++++++

    private static class LocationsLoader extends AsyncTaskLoader<List<Location>> {

        private List<Location> mLocations;
        private HttpResponse response;
        private static final String TAG = "LocationsLoader";

        public LocationsLoader(Context context) {
            super(context);
        }

        @Override
        public List<Location> loadInBackground() {

            List<Location> entries = new ArrayList<Location>();

            try {
                // USERS REQUEST
                HttpResponse userListResponse = App.getConnector()
                        .path("/cache/users")
                        .withFields("firstName", "lastName", "id", "usersGroups")
                        .withLimit(1000).get();
                Log.d(TAG, userListResponse.toString());
                JSONArray usersArray = userListResponse.getJSONArray();

                // LOCATIONS REQUEST
                HttpResponse response = App.getConnector().path("/cache/users/groups").withFields("label", "users", "id").get();
                Log.d(TAG, response.toString());
                JSONArray locationsArray = response.getJSONArray();

                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject locJson = (JSONObject) locationsArray.get(i);
                    Location locToAdd = new Location(locJson);
                    int id = locToAdd.getId();
                    for (int j = 0; j < usersArray.length(); j++) {
                        JSONObject userJson = (JSONObject) usersArray.get(j);
                        JSONArray array = userJson.getJSONArray("usersGroups");
                        if (array.length() != 0  && id == array.getInt(0)) {
                            locToAdd.addVisitor(new Person(userJson));
                        }
                    }
                    entries.add(locToAdd);
                    Log.d(TAG, entries.get(i).print());
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
        public void deliverResult(List<Location> listOfData) {
            if (isReset()) {
                // An async query came in while the loader is stopped. We
                // don't need the result.
                if (listOfData != null) {
                    onReleaseResources(listOfData);
                }
            }
            List<Location> oldApps = listOfData;
            mLocations = listOfData;
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
         * Helper function to take care of releasing resources associated with
         * an actively loaded data set.
         */

        protected void onReleaseResources(List<Location> apps) {
        }

        /**
         * Handles a request to start the Loader.
         */

        @Override
        protected void onStartLoading() {
            if (mLocations != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mLocations);
            }
            if (takeContentChanged() || mLocations == null) {
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
        public void onCanceled(List<Location> apps) {
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
            if (mLocations != null) {
                onReleaseResources(mLocations);
                mLocations = null;
            }


        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++
    // ======== EXPANDABLE LIST ADAPTER CLASS =========
    // ++++++++++++++++++++++++++++++++++++++++++++++++

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<Location> locationsList;
        private HashMap<Location, List<Person>> currentVisitorsList;

        public ExpandableListAdapter(Context context, List<Location> listDataHeader,
                                     HashMap<Location, List<Person>> listChildData) {
            this.context = context;
            this.locationsList = listDataHeader;
            this.currentVisitorsList = listChildData;
        }

        public void setLocations(List<Location> data) {
            if (data != null) {
                for (Location appEntry : data) {
                    locationsList.add(appEntry);
                    currentVisitorsList.put(appEntry, appEntry.getVisitors());
                }
            }
        }

        @Override
        public Person getChild(int groupPosition, int childPosititon) {
            return this.currentVisitorsList.get(this.locationsList.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = getChild(groupPosition, childPosition).getFullName();

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.currentVisitorsList.get(this.locationsList.get(groupPosition))
                    .size();
        }

        @Override
        public Location getGroup(int groupPosition) {
            return this.locationsList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.locationsList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = getGroup(groupPosition).getLabel();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

}