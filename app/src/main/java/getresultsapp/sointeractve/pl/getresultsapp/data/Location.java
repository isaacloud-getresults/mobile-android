package getresultsapp.sointeractve.pl.getresultsapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Data storage class for locations.
 */

public class Location {

    private String label;
    private int id,visitorsNumber;

    public int getVisitorsNumber() {
        return visitorsNumber;
    }

    public void AddVisitor() {
        this.visitorsNumber++;
    }

    public void RemoveVisitor() {
        if (visitorsNumber != 0) {
            this.visitorsNumber--;
        }
    }

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

    public String print () {
        return this.label;
    }



}
