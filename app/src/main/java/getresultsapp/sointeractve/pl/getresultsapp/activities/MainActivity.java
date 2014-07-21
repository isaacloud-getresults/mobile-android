package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.ActionBar;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.LocationsFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.ProfileFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.StatusFragment;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.TabListener;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class MainActivity extends Activity {

    private static final String TAG = "UserActivity";
    ActionBar.Tab tab1, tab2, tab3;
    Fragment fragmentTab1 = new StatusFragment();
    Fragment fragmentTab2 = new LocationsFragment();
    Fragment fragmentTab3 = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_test);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tab1 = actionBar.newTab().setText("Status");
        tab2 = actionBar.newTab().setText("Locations");
        tab3 = actionBar.newTab().setText("Profile");

        tab1.setTabListener(new TabListener(fragmentTab1));
        tab2.setTabListener(new TabListener(fragmentTab2));
        tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);


        new AlertDialog.Builder(this)
                .setTitle("Hello " + App.loadUserData().getName())
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                }).show();

    }

    // LOGIN EVENT
    private class PostEventTask extends AsyncTask<Object, Object, Object> {

        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(Object... params) {
            Log.d(TAG, "UWAGA!");
            try {
                JSONObject body = new JSONObject();
                body.put("activity", "login");
                response = App.getConnector().event(userData.getUserId(),
                        "USER", "PRIORITY_HIGH", 1, "NORMAL", body);
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                isError = true;
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            Log.d(TAG, "onPostExecute()");
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }

    }

}
