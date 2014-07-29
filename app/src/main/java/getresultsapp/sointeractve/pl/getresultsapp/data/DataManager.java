package getresultsapp.sointeractve.pl.getresultsapp.data;

// Data management class for downloading locations and users.
// @author: Pawel Dylag

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;


public class DataManager {

    private List<Person> people;
    private List<Location> locations;

    public DataManager() {
        people = new ArrayList<Person>();
        // DEBUG MODE, TODO: NEED A SERVICE TO DOWNLOAD PEOPLE
        people.add(new Person("test", "test", 1, 1));
        people.add(new Person("test", "test", 2, 2));
        people.add(new Person("test", "test", 2, 3));
        people.add(new Person("test", "test", 3, 4));
        locations = new ArrayList<Location>();
        new EventGetLocations().execute();
    }

    public List<Person> getPeople() {
        return people;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Person> getPeopleAtLocation (Location l) {
        List<Person> array = new ArrayList<Person>();
        for (Person p: people) {
            if (p.getActualLocation() == l.getId()) {
                array.add(p);
            }
        }
        return array;
    }

    // GET LOCATIONS EVENT
    private class EventGetLocations extends AsyncTask<Object, Object, Object> {

        private static final String TAG = "EventGetLocations";
        public boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");

        }


        @Override
        public Object doInBackground(Object... params) {
            List<Location> entries = new ArrayList<Location>();
            try {
                // LOCATIONS REQUEST
                HttpResponse response = App.getConnector().path("/cache/users/groups").withFields("label", "id").get();
                Log.d(TAG, response.toString());
                JSONArray locationsArray = response.getJSONArray();
                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject json = (JSONObject) locationsArray.get(i);
                    Log.d(TAG, json.getString("label"));
                    entries.add(new Location(json));
                }
                success = true;
                locations = entries;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Log.d(TAG, "onPostExecute()");
            if (success) {
                for (Location l: locations) {
                    Log.d(TAG, l.getLabel());
                }
            } else {
                Log.d(TAG, "NOT SUCCES");
            }
        }

    }

}
