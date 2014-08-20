package com.sointeractive.getresults.app.data.isaacloud;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.pebble.responses.PersonInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data storage class for users in locations
 */
public class Person {

    private String firstName, lastName;
    private int id, location;

    public Person(String firstName, String lastName, int location, int id) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setLocation(location);
        this.setId(id);
    }

    public Person(JSONObject json) throws JSONException {
        this.setFirstName(json.getString("firstName"));
        this.setLastName(json.getString("lastName"));
        this.setId(json.getInt("id"));
        this.setLocation(3);
        // getting user location
        JSONArray array = json.getJSONArray("counterValues");
        if (array.length() != 0) {
            for (int j = 0; j < array.length(); j++) {
                JSONObject counter = (JSONObject) array.get(j);
                // get user location counter
                if (counter.getString("counter").equals(Settings.LOCATION_COUNTER)) {
                    this.location = Integer.parseInt(counter.getString("value"));
                }
            }
        }
    }

    public String print() {
        return this.getFirstName() + " " + this.getLastName() + " " + "currently: " + this.getLocation();
    }

    String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getLocation() {
        return location;
    }

    void setLocation(int location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public ResponseItem toPersonInResponse() {
        return new PersonInResponse(id, getFullName(), location);
    }
}
