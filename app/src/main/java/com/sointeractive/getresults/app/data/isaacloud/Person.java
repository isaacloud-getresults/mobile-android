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

    public Person(final String firstName, final String lastName, final int location, final int id) {
        setFirstName(firstName);
        setLastName(lastName);
        setLocation(location);
        setId(id);
    }

    public Person(final JSONObject json) throws JSONException {
        setFirstName(json.getString("firstName"));
        setLastName(json.getString("lastName"));
        setId(json.getInt("id"));
        setLocation(3);
        // getting user location
        final JSONArray array = json.getJSONArray("counterValues");
        if (array.length() != 0) {
            for (int j = 0; j < array.length(); j++) {
                final JSONObject counter = (JSONObject) array.get(j);
                // get user location counter
                if (counter.getString("counter").equals(Settings.LOCATION_COUNTER)) {
                    this.location = Integer.parseInt(counter.getString("value"));
                }
            }
        }
    }

    public String print() {
        return getFirstName() + " " + getLastName() + " " + "currently: " + getLocation();
    }

    String getFirstName() {
        return firstName;
    }

    void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getLocation() {
        return location;
    }

    void setLocation(final int location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    public ResponseItem toPersonInResponse() {
        return new PersonInResponse(id, getFullName(), location);
    }
}
