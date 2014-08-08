package getresultsapp.sointeractve.pl.getresultsapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;

/**
 * Data storage class for users in locations
 */
public class Person {

    private String firstName, lastName;
    private int id, actualLocation;

    public Person (String firstName, String lastName, int actualLocation, int id) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setActualLocation(actualLocation);
        this.setId(id);
    }

    public Person (JSONObject json) throws JSONException  {
        this.setFirstName(json.getString("firstName"));
        this.setLastName(json.getString("lastName"));
        this.setId(json.getInt("id"));
        this.setActualLocation(3);
        // getting user location
        JSONArray array = json.getJSONArray("counterValues");
        if (array.length() != 0) {
            for (int j = 0; j < array.length(); j++) {
                JSONObject counter = (JSONObject) array.get(j);
                // get user location counter
                if (counter.getString("counter").equals(Settings.locationCounter)) {
                    this.actualLocation = Integer.parseInt(counter.getString("value"));
                }
            }
        }
    }

    public String print() {
        return this.getFirstName() + " " + this.getLastName() + " " + "currently: " + this.getActualLocation();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() { return firstName + " " + lastName;}

    public int getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(int actualLocation) {
        this.actualLocation = actualLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
