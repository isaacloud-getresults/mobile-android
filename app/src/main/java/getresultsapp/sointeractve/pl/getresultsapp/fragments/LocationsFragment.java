package getresultsapp.sointeractve.pl.getresultsapp.fragments;

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
import android.widget.TextView;

import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.cards.StatusCard;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Location;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Person;
import it.gmariotti.cardslib.library.view.CardView;


public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationsFragment";
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    List<Location> locationsArray;
    StatusCard statusCard;
    private BroadcastReceiver receiverLocations = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "Problem " + App.loadLoginData().getPassword());
//            Log.d(TAG, "Problem " + App.loadUserData().getUserLocation().getLabel());
            statusCard.initLocation(App.loadUserData().getUserLocation());
            statusCard.setOnClickListener(null);
            statusCard.setClickable(false);
            cardView.refreshCard(statusCard);
        }
    };
    CardView cardView;
    Context context;

    public static LocationsFragment newInstance() {
        Log.d(TAG, "newInstance");
        LocationsFragment f = new LocationsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationsArray = App.getLocations();
        context = this.getActivity();
        for (Location l : locationsArray) {
            Log.d(TAG, "Loading: " + l.getLabel());
        }
        statusCard = new StatusCard(context, R.layout.status_card_content);
        statusCard.setBackgroundResourceId(R.drawable.status_card_background);
        listAdapter = new ExpandableListAdapter(context, locationsArray);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverLocations,
                new IntentFilter(Settings.broadcastIntentUpdateData));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        context = getActivity();
        cardView = (CardView) view.findViewById(R.id.cardStatus);
        cardView.setCard(this.statusCard);
        expandableListView = (ExpandableListView) view.findViewById(R.id.listView);
        expandableListView.setGroupIndicator(null);
        expandableListView.setDivider(getResources().getDrawable(R.drawable.divider));
        expandableListView.setChildDivider(getResources().getDrawable(R.drawable.child_divider));
        expandableListView.setAdapter(listAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
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

        private Context context;
        private List<Location> locationsList;

        public ExpandableListAdapter(Context context, List<Location> listDataHeader) {
            this.context = context;
            this.locationsList = listDataHeader;
        }

        @Override
        public Person getChild(int groupPosition, int childPosition) {
            List<Person> list = App.getPeopleAtLocation(locationsList.get(groupPosition));
            Person child = list.get(childPosition);
            return child;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView( int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = getChild(groupPosition, childPosition).getFullName();

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }
            convertView.setPadding(0, 0, 10, 0);
            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<Person> list = App.getPeopleAtLocation(locationsList.get(groupPosition));
            return list.size();
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
            String headerStats = "{fa-users}" + " " + App.getPeopleAtLocation(locationsList.get(groupPosition)).size() + "   " + "{fa-trophy}" + " " + App.getDataManager().getAchievements().size();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }
            IconTextView indicator = (IconTextView) convertView.findViewById(R.id.indicator);
            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            TextView lblListHeaderVisits = (TextView) convertView
                    .findViewById(R.id.locationStatsCounter);
            String state = isExpanded ? "{fa-chevron-up}" : "{fa-chevron-down}";
            lblListHeader.setText(headerTitle);
            lblListHeaderVisits.setText(headerStats);
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