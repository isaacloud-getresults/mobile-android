package com.sointeractive.getresults.app.fragments;

import android.app.Activity;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.cards.StatusCard;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.DrawableManager;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.data.isaacloud.Person;

import java.util.List;

import it.gmariotti.cardslib.library.view.CardView;


public class LocationsFragment extends Fragment {

    private static final String TAG = LocationsFragment.class.getSimpleName();
    private ExpandableListAdapter listAdapter;
    private final BroadcastReceiver receiverLocations = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "Event: onReceive called");
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    };
    private StatusCard statusCard;
    private final BroadcastReceiver receiverStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "Event: onReceive called");
            Log.d(TAG, "User location is now: " + App.loadUserData().getUserLocation().getLabel());
            statusCard.initLocation(App.loadUserData().getUserLocation());
            statusCard.setOnClickListener(null);
            statusCard.setClickable(false);
            cardView.refreshCard(statusCard);
        }
    };
    private CardView cardView;
    private Context context;
    private DrawableManager dm;

    public static LocationsFragment newInstance() {
        Log.d(TAG, "newInstance");
        final LocationsFragment f = new LocationsFragment();
        final Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<Location> locationsArray = App.getLocations();
        context = getActivity();
        for (final Location l : locationsArray) {
            Log.v(TAG, "Loading: " + l.getLabel());
        }
        statusCard = new StatusCard(context, R.layout.status_card_content);
        statusCard.setBackgroundResourceId(R.drawable.status_card_background);
        listAdapter = new ExpandableListAdapter(context, locationsArray);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverLocations,
                new IntentFilter(Settings.BROADCAST_INTENT_UPDATE_DATA));
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverStatus,
                new IntentFilter(Settings.BROADCAST_INTENT_NEW_LOCATION));
        dm = new DrawableManager();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_locations, container, false);
        context = getActivity();
        cardView = (CardView) view.findViewById(R.id.cardStatus);
        cardView.setCard(statusCard);
        final ImageView currentRoom = (ImageView) view.findViewById(R.id.colorBorder);
        dm.fetchDrawableOnThread("http://cdn.homeidea.pics/images/images.businessweek.com/ss/06/11/1117_home_offices/image/gourmet.jpg", currentRoom, true);
        final ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.listView);
        expandableListView.setGroupIndicator(null);
        expandableListView.setDivider(getResources().getDrawable(R.drawable.divider));
        expandableListView.setChildDivider(getResources().getDrawable(R.drawable.child_divider));
        expandableListView.setAdapter(listAdapter);
        return view;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listAdapter = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiverLocations);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++
    // ======== EXPANDABLE LIST ADAPTER CLASS =========
    // ++++++++++++++++++++++++++++++++++++++++++++++++

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final Context context;
        private final List<Location> locationsList;

        public ExpandableListAdapter(final Context context, final List<Location> listDataHeader) {
            this.context = context;
            this.locationsList = listDataHeader;
        }

        @Override
        public Person getChild(final int groupPosition, final int childPosition) {
            final List<Person> list = App.getPeopleAtLocation(locationsList.get(groupPosition));
            return list.get(childPosition);
        }

        @Override
        public long getChildId(final int groupPosition, final int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 final boolean isLastChild, View convertView, final ViewGroup parent) {

            final String childText = getChild(groupPosition, childPosition).getFullName();

            if (convertView == null) {
                final LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }
            convertView.setPadding(0, 0, 10, 0);
            final TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(final int groupPosition) {
            final List<Person> list = App.getPeopleAtLocation(locationsList.get(groupPosition));
            return list.size();
        }

        @Override
        public Location getGroup(final int groupPosition) {
            return locationsList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return locationsList.size();
        }

        @Override
        public long getGroupId(final int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded,
                                 View convertView, final ViewGroup parent) {
            final String headerTitle = getGroup(groupPosition).getLabel();
            final String headerStats = "{fa-users}" + " " + App.getPeopleAtLocation(locationsList.get(groupPosition)).size();
            if (convertView == null) {
                final LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }
            final IconTextView indicator = (IconTextView) convertView.findViewById(R.id.indicator);
            final TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            final TextView lblListHeaderVisits = (TextView) convertView
                    .findViewById(R.id.locationStatsCounter);
            final String state = isExpanded ? "{fa-chevron-up}" : "{fa-chevron-down}";
            lblListHeader.setText(headerTitle);
            lblListHeaderVisits.setText(headerStats);
            final ImageView locationPic = (ImageView) convertView.findViewById(R.id.locationImage);
            dm.fetchDrawableOnThread("http://cdn.homeidea.pics/images/images.businessweek.com/ss/06/11/1117_home_offices/image/gourmet.jpg", locationPic, true);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(final int groupPosition, final int childPosition) {
            return true;
        }

    }

}