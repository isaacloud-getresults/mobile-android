package getresultsapp.sointeractve.pl.getresultsapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data storage class for locations.
 */

public class Location implements Serializable {

    private String label;
    private int id;


    public Location (String label, int id ) {
        this.setLabel(label);
        this.setId(id);

    }

    public Location (JSONObject json) throws JSONException {
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




}
