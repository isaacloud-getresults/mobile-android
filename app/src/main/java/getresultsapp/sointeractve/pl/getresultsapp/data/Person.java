package getresultsapp.sointeractve.pl.getresultsapp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data storage class for users in locations
 */
public class Person {

    private String firstName, lastName, actualLocation;
    private int id;

    public Person (String firstName, String lastName, String actualLocation, int id) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setActualLocation(actualLocation);
        this.setId(id);
    }

    public Person (JSONObject json) throws JSONException  {
        this.setFirstName(json.getString("firstName"));
        this.setLastName(json.getString("lastName"));
        //this.setActualLocation(actualLocation);
        this.setId(json.getInt("id"));
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

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
