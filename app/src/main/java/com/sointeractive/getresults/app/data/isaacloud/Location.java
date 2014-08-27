package com.sointeractive.getresults.app.data.isaacloud;

import com.sointeractive.getresults.app.pebble.responses.BeaconResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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

    void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    public ResponseItem toBeaconResponse(final int peopleNumber, final int peoplePages) {
        return new BeaconResponse(id, label, peopleNumber, peoplePages);
    }
}
