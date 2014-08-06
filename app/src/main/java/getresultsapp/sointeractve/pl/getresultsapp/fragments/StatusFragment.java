package getresultsapp.sointeractve.pl.getresultsapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.os.Handler;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;



public class StatusFragment extends Fragment {

    TextView textLocation;
    TextView textVisits;
    Context context;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "StatusFragment";
    private BroadcastReceiver receiverStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");
            int visits = 0;
            if (App.loadUserData().getUserLocationId() == 4) {
                visits = App.loadUserData().getLocationVisits();
            }

            updateStatus(visits);
        }
    };

    public static StatusFragment newInstance() {
        StatusFragment f = new StatusFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }
    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverStatus,
                new IntentFilter(Settings.broadcastIntent));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_status, container, false);
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

    public void updateStatus(int stats) {

    }


}
