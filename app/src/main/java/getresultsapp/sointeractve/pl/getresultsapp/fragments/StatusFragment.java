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
    Context context;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "StatusFragment";
    private BroadcastReceiver receiverStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain anydata
            Log.d(TAG, "onReceive called");
            updateStatus();
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
        TextView textHey = (TextView) view.findViewById(R.id.textHey);
        textLocation = (TextView) view.findViewById(R.id.textLocation);
        textHey.setText("Hey " + App.loadUserData().getFirstName() + ", " + "you are at:");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void updateStatus() {

        textLocation.setText(App.loadUserData().getUserLocation());
    }


}
