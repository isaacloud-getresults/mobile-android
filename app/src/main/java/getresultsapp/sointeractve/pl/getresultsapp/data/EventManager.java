package getresultsapp.sointeractve.pl.getresultsapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.fragments.StatusFragment;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

//
// @author: Pawel Dylag
//
public class EventManager {

    public static final String TAG = "EventManager";
    Context context;

    public EventManager(){
        this.context = App.getInstance().getApplicationContext();
    }


    public void postEventLogin () {
        new EventLogin().execute();
    }

    public void postEventNewBeacon (String beaconMajor, String beaconMinor) {
        Log.d(TAG + "SPRAWDAZMY BIKONA: ",beaconMajor + " " + beaconMinor);
        new EventPostNewBeacon().execute(beaconMajor, beaconMinor);
    }

    public void postEventLeftBeacon (String beaconMajor, String beaconMinor) {
        new EventPostLeftBeacon().execute(beaconMajor, beaconMinor);
    }

    public void postEventUpdateData (){
        new EventUpdateData().execute();
    }


    ////////////////////////////////////////////////
    // ================  LOGIN EVENT ===============
    ////////////////////////////////////////////////

    private class EventLogin extends AsyncTask<Object, Object, Object> {

        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(Object... params) {
            Log.d(TAG, "EventLogin:");
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

    ///////////////////////////////////////////////////////////////////
    // ============ POST EVENT WHEN NEW BEACON IS IN RANGE ============
    ///////////////////////////////////////////////////////////////////

    private class EventPostNewBeacon extends AsyncTask<String, Object, Object> {

        String TAG = "EventPostNewBeacon";
        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(String... data) {
            Log.d(TAG, "EventLogin:");
            try {
                JSONObject body = new JSONObject();
                body.put("place", data[0] + "." + data[1]);
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
            // GET ACTUAL LOCATION EVENT
            new EventGetNewLocation().execute();
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////
    // ============ GET ACTUAL LOCATION AFTER BEACON EVENT =============
    ////////////////////////////////////////////////////////////////////

    private class EventGetNewLocation extends AsyncTask<Object, Object, Object> {

        String TAG = "EventGetNewLocation";
        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();


        @Override
        protected Object doInBackground(Object... beaconId) {
            try {
                int id = userData.getUserId();
                HttpResponse response = App.getConnector().path("/cache/users/"+id).get();
                Log.d(TAG, response.toString());
                JSONObject json = response.getJSONObject();
                JSONArray array = json.getJSONArray("counterValues");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = (JSONObject) array.get(i);
                    if (o.getString("counter").equals("6")) {
                        userData.setUserLocation(Integer.parseInt(o.getString("value")));
                    }
                }
                App.saveUserData(userData);
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
            Intent intent = new Intent(Settings.broadcastIntent);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }
    }


    ///////////////////////////////////////////////////////////////////
    // ============ POST EVENT WHEN NEW BEACON IS IN RANGE ============
    ///////////////////////////////////////////////////////////////////

    private class EventPostLeftBeacon extends AsyncTask<String, Object, Object> {

        String TAG = "EventPostLeftBeacon";
        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(String... data) {

            Log.d(TAG, "EventPostLeftBeacon");
            try {
                JSONObject body = new JSONObject();
                body.put("place", data[0] + "." + data[1] + ".exit");
                response = App.getConnector().event(userData.getUserId(),
                        "USER", "PRIORITY_HIGH", 1, "NORMAL", body);
                Log.d(TAG, response.toString());
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
            // GET ACTUAL LOCATION EVENT
            new EventGetNewLocation().execute();
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }
    }


    /////////////////////////////////////////////////////
    // ================ UPDATE DATA EVENT ===============
    /////////////////////////////////////////////////////

    private class EventUpdateData extends AsyncTask<String, Object, Object> {

        String TAG = "EventUpdateData";
        HttpResponse response;
        boolean isError = false;
        UserData userData = App.loadUserData();

        @Override
        protected Object doInBackground(String... data) {
            SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
            List<Location> locationsArray = App.getLocations();
            for(Location loc : locationsArray) {
                entries.put(loc.getId() , new LinkedList<Person>());
            }
            entries.put(0, new LinkedList<Person>());
            try {

                 // USERS REQUEST
                HttpResponse usersResponse = App.getConnector().path("/cache/users").withFields("firstName", "lastName","id","counterValues").get();
                Log.d(TAG, usersResponse.toString());
                JSONArray usersArray = usersResponse.getJSONArray();
                // for every user
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = (JSONObject) usersArray.get(i);
                    Person p = new Person(userJson);
                    entries.get(p.getActualLocation()).add(p);
                }
                App.getDataManager().setPeople(entries);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            Log.d(TAG, "onPostExecute()");
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent (Settings.broadcastIntent));
            if (isError) {
                Log.d(TAG, "onPostExecute() - error detected");
            }
            if (response != null) {
                Log.d(TAG, "onPostExecute() - response: " + response.toString());
            }
        }
    }
}
