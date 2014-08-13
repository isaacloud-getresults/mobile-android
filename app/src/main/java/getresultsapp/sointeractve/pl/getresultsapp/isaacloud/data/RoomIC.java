package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data;

import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.BeaconResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

public class RoomIC {
    private final int id;
    private final String name;

    public RoomIC(final JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("label");
    }

    public int getId() {
        return id;
    }

    public ResponseItem toBeaconResponse(final int peopleNumber) {
        return new BeaconResponse(id, name, peopleNumber);
    }
}
