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
    private int id;
    private ArrayList<Person> currentVisitors;

    public Location (String name, int id ) {
        this.setLabel(label);
        this.setId(id);
        currentVisitors = new ArrayList<Person>();
    }

    public void addVisitor(Person v) {
        currentVisitors.add(v);
    }


    public ArrayList<Person> getVisitors() {
        return this.currentVisitors;
    }


    public void removeVisitor(int id) {
        for ( Person x: currentVisitors) {
            if (x.getId() == id) {
              currentVisitors.remove(x);
            }
        }
    }

    public Location (JSONObject json) throws JSONException {
        currentVisitors = new ArrayList<Person>();
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
