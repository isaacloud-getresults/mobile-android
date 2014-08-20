package com.sointeractive.getresults.app.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.data.isaacloud.Notification;
import com.sointeractive.getresults.app.data.isaacloud.Person;
import com.sointeractive.getresults.app.data.isaacloud.UserData;
import com.sointeractive.getresults.app.pebble.checker.NewAchievementsNotifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class EventManager {

    private static final String TAG = EventManager.class.getSimpleName();
    static boolean internetConnection;
    private static Context context;
    private static int notificationId = 0;

    public EventManager() {
        context = App.getInstance().getApplicationContext();
    }

    public void postEventLogin() {
        new EventLogin().execute();
    }

    public void postEventNewBeacon(String beaconMajor, String beaconMinor) {
        Log.d(TAG + "SPRAWDAZMY BIKONA: ", beaconMajor + " " + beaconMinor);
        new EventPostNewBeacon().execute(beaconMajor, beaconMinor);
    }

    public void postEventLeftBeacon(String beaconMajor, String beaconMinor) {
        new EventPostLeftBeacon().execute(beaconMajor, beaconMinor);
    }

    public void postEventUpdateData() {
        new EventUpdateData().execute();
    }

    private class EventLogin extends AsyncTask<Object, Object, Object> {

        final UserData userData = App.loadUserData();
        HttpResponse response;
        boolean isError = false;

        @Override
        protected Object doInBackground(Object... params) {
            Log.d(TAG, "Action: sending login event");
            try {
                JSONObject body = new JSONObject();
                body.put("activity", "login");
                response = App.getIsaacloudConnector().event(userData.getUserId(),
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
            if (isError) {
                Log.e(TAG, "Error: Cannot log in");
            }
            if (response != null) {
                Log.v(TAG, "response: " + response.toString());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    // ============ POST EVENT WHEN NEW BEACON IS IN RANGE ============
    ///////////////////////////////////////////////////////////////////

    private class EventPostNewBeacon extends AsyncTask<String, Object, Object> {

        final UserData userData = App.loadUserData();
        private final String TAG = EventPostNewBeacon.class.getSimpleName();
        HttpResponse response;
        boolean isError = false;

        @Override
        protected Object doInBackground(String... data) {
            Log.d(TAG, "Action: sending new beacon event");
            try {
                JSONObject body = new JSONObject();
                body.put("place", data[0] + "." + data[1]);
                response = App.getIsaacloudConnector().event(userData.getUserId(),
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
            // GET ACTUAL LOCATION EVENT
            new EventGetNewLocation().execute();
            if (isError) {
                Log.e(TAG, "Error: Cannot post new beacon");
            }
            if (response != null) {
                Log.v(TAG, "response: " + response.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////
    // ============ GET ACTUAL LOCATION AFTER BEACON EVENT =============
    ////////////////////////////////////////////////////////////////////

    private class EventGetNewLocation extends AsyncTask<Object, Object, Object> {


        final Intent message = new Intent(Settings.BROADCAST_INTENT_UPDATE_DATA);
        final UserData userData = App.loadUserData();
        private final String TAG = EventGetNewLocation.class.getSimpleName();
        HttpResponse response;
        boolean isError = false;

        @Override
        protected Object doInBackground(Object... beaconId) {
            try {
                int id = userData.getUserId();
                HttpResponse response = App.getIsaacloudConnector().path("/cache/users/" + id).get();
                Log.v(TAG, response.toString());
                JSONObject json = response.getJSONObject();
                JSONArray array = json.getJSONArray("counterValues");
                JSONArray gainedAchievements = json.getJSONArray("gainedAchievements");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = (JSONObject) array.get(i);
                    if (o.getString("counter").equals(Settings.LOCATION_COUNTER)) {
                        userData.setUserLocation(Integer.parseInt(o.getString("value")));
                    }
                }
                // set user profile counter values
                String counterLevel = json.getString("level");
                userData.setLevel(counterLevel);
                userData.setGainedAchievements("" + gainedAchievements.length());
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
            // CHECK FOR NEW ACHIEVEMENTS
            new EventCheckAchievements().execute();
            // send broadcast
            LocalBroadcastManager.getInstance(context).sendBroadcast(message);
            if (isError) {
                Log.e(TAG, "Error: Cannot get new location");
            }
            if (response != null) {
                Log.v(TAG, "response: " + response.toString());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    // ============ POST EVENT WHEN BEACON IS OUT OF RANGE ============
    ///////////////////////////////////////////////////////////////////

    private class EventPostLeftBeacon extends AsyncTask<String, Object, Object> {

        final UserData userData = App.loadUserData();
        private final String TAG = EventPostLeftBeacon.class.getSimpleName();
        HttpResponse response;
        boolean isError = false;

        @Override
        protected Object doInBackground(String... data) {
//            generateNotification("Left beacon range", "Outside location", "Meeting room");
            Log.i(TAG, "Action: sending left beacon event");
            try {
                JSONObject body = new JSONObject();
                body.put("place", data[0] + "." + data[1] + ".exit");
                response = App.getIsaacloudConnector().event(userData.getUserId(),
                        "USER", "PRIORITY_HIGH", 1, "NORMAL", body);
                Log.v(TAG, response.toString());
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
            // GET ACTUAL LOCATION EVENT
            new EventGetNewLocation().execute();
            if (isError) {
                Log.e(TAG, "Error: Cannot post left beacon");
            }
            if (response != null) {
                Log.v(TAG, "response: " + response.toString());
            }
        }
    }

    /////////////////////////////////////////////////////
    // ================ UPDATE DATA EVENT ===============
    /////////////////////////////////////////////////////

    private class EventUpdateData extends AsyncTask<String, Object, Object> {

        final boolean isError = false;
        private final String TAG = EventUpdateData.class.getSimpleName();
        HttpResponse response;

        @Override
        protected Object doInBackground(String... data) {
            SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
            List<Location> locationsArray = App.getLocations();
            for (Location loc : locationsArray) {
                entries.put(loc.getId(), new LinkedList<Person>());
            }
            entries.put(0, new LinkedList<Person>());
            try {

                // USERS REQUEST
                HttpResponse usersResponse = App.getIsaacloudConnector().path("/cache/users").withFields("firstName", "lastName", "id", "counterValues").withLimit(0).get();
                Log.v(TAG, usersResponse.toString());

                JSONArray usersArray = usersResponse.getJSONArray();
                // for every user
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = (JSONObject) usersArray.get(i);
                    Person p = new Person(userJson);
                    entries.get(p.getLocation()).add(p);
                    // CHECK ACTUAL USER POSITION:
                    if (p.getId() == App.loadUserData().getUserId()) {
                        if (p.getLocation() != App.loadUserData().getUserLocationId()) {
                            UserData userData = App.loadUserData();
                            userData.setUserLocation(p.getId());
                            App.saveUserData(userData);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Settings.BROADCAST_INTENT_NEW_LOCATION));
                        }
                    }
                }
                App.getDataManager().setPeople(entries);
            } catch (JSONException e) {
                Log.e(TAG, "Error: JSON");
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                Log.e(TAG, "Error: IC connection");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Error: IO, " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Settings.BROADCAST_INTENT_UPDATE_DATA));
            if (isError) {
                Log.e(TAG, "Error: Cannot update data");
            }
            if (response != null) {
                Log.v(TAG, "response: " + response.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////
    // ================ CHECK ACHIEVEMENTS EVENT ===============
    ////////////////////////////////////////////////////////////

    private class EventCheckAchievements extends AsyncTask<Object, Object, Object> {

        final List<Achievement> newAchievements = new ArrayList<Achievement>();
        UserData userData;

        @Override
        protected Object doInBackground(Object... params) {
            userData = App.loadUserData();
            Log.d(TAG, "User data = {name: " + userData.getName() + ", id: " + userData.getUserId() + "}");
            try {
                // ACHIEVEMENTS REQUEST
                SparseIntArray idMap = new SparseIntArray();
                HttpResponse responseUser = App
                        .getIsaacloudConnector()
                        .path("/cache/users/" + userData.getUserId()).withFields("gainedAchievements").withLimit(0).get();
                JSONObject achievementsJson = responseUser.getJSONObject();
                JSONArray arrayUser = achievementsJson.getJSONArray("gainedAchievements");
                for (int i = 0; i < arrayUser.length(); i++) {
                    JSONObject json = (JSONObject) arrayUser.get(i);
                    idMap.put(json.getInt("achievement"), json.getInt("amount"));
                }
                HttpResponse responseGeneral = App.getIsaacloudConnector()
                        .path("/cache/achievements").withLimit(1000).get();
                JSONArray arrayGeneral = responseGeneral.getJSONArray();
                for (int i = 0; i < arrayGeneral.length(); i++) {
                    JSONObject json = (JSONObject) arrayGeneral.get(i);
                    if (idMap.get(json.getInt("id"), -1) != -1) {
                        newAchievements.add(0, new Achievement(json, true, idMap.get(json.getInt("id"))));
                    }
                }
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
            List<Achievement> actualAchievements = App.getDataManager().getAchievements();
            for (Achievement a : actualAchievements) {
                Log.v(TAG, "old Achievements: " + a.getLabel());
            }
            for (Achievement a : newAchievements) {
                Log.v(TAG, "new Achievements: " + a.getLabel());
            }

            if (newAchievements.size() != actualAchievements.size()) {
                // search for new achievement
                List<Achievement> recentAchievements = new LinkedList<Achievement>();
                int i = 0;
                while (i < newAchievements.size()) {
                    if (!actualAchievements.contains(newAchievements.get(i))) {
                        recentAchievements.add(newAchievements.get(i));
                    }
                    i++;
                }
                App.getDataManager().setAchievements(newAchievements);
                NewAchievementsNotifier.notifyAchievements(recentAchievements);
                for (Achievement achievement : recentAchievements) {
                    Intent intent = new Intent(Settings.BROADCAST_INTENT_NEW_ACHIEVEMENT);
                    intent.putExtra("label", achievement.getLabel());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                    generateNotification("NEW ACHIEVEMENT UNLOCKED!", "New achievement", recentAchievement.getLabel());
                }
            } else {
                Log.d(TAG, "No new achievements.");
            }
            new EventCheckNotifications().execute();
        }
    }

    private class EventCheckNotifications extends AsyncTask<Object, Object, Object> {

        List<Notification> entries = new ArrayList<Notification>();

        @Override
        protected Object doInBackground(Object... params) {
            try {
                Map<String, Object> query = new HashMap<String, Object>();
                Map<String, String> order = new HashMap<String, String>();
                order.put("createdAt", "DESC");
                query.put("subjectId", App.loadUserData().getUserId());
                HttpResponse response = App.getIsaacloudConnector()
                        .path("/queues/notifications").withQuery(query).withLimit(1).withOrder(order).get();
                JSONArray array = response.getJSONArray();
                for (int i = 0; i < array.length(); i++) {
                    if (array.length() != 0) {
                        entries.add(new Notification((JSONObject) array.get(i)));
                        Log.d(TAG, "added notification: " + array.get(i).toString());
                    } else {
                        Log.d(TAG, "RECEIVED NULL ARRAY AS NOTIFICATIONS");
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return entries;
        }

        protected void onPostExecute(Object result) {
            if (entries.size() != 0) {
                if (App.getDataManager().isNewNotification(entries.get(0))) {
                    Toast.makeText(context, entries.get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "NOTIFICATION = NEW");
                }
            } else Log.d(TAG, "NOTIFICATION = NONE");
        }
    }


}
