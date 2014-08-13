package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data;

import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.config.IsaaCloudSettings;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.PersonInResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class PersonIC {
    private final int id;
    private final int beacon;
    private final String firstName;
    private final String lastName;
    private final SparseIntArray counters = new SparseIntArray();

    public PersonIC(final JSONObject json) throws JSONException {
        id = json.getInt("id");
        firstName = json.getString("firstName");
        lastName = json.getString("lastName");
        setCounterValues(json.getJSONArray("counterValues"));
        beacon = counters.get(IsaaCloudSettings.ROOM_COUNTER_ID, -1);
    }

    public int getId() {
        return id;
    }

    public int getBeacon() {
        return beacon;
    }

    void setCounterValues(final JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!jsonArray.isNull(i)) {
                final JSONObject counter = jsonArray.getJSONObject(i);
                counters.put(counter.getInt("counter"), counter.getInt("value"));
            }
        }
    }

    String getFullName() {
        return firstName + " " + lastName;
    }

    public ResponseItem toPersonInResponse(final int roomId) {
        return new PersonInResponse(id, getFullName(), roomId);
    }
}
