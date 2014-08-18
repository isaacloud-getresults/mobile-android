package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.BeaconResponse;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

/**
 * Data storage class for locations.
 */

public class Location implements Serializable {

    private String label;
    private int id;


    public Location(String label, int id) {
        this.setLabel(label);
        this.setId(id);

    }

    public Location(JSONObject json) throws JSONException {
        this.setLabel(json.getString("label"));
        this.setId(json.getInt("id"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ResponseItem toBeaconResponse(final int peopleNumber) {
        return new BeaconResponse(id, label, peopleNumber);
    }
}
