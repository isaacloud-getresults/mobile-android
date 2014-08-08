package getresultsapp.sointeractve.pl.getresultsapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class UserData implements Serializable {


    private String name, email, firstName;
    private int userId, locationVisits;
    private Location userLocation;

    public Location getUserLocation() {
        return this.userLocation;
    }

    public int getUserLocationId(){
       return this.userLocation.getId();
    }

    public void setUserLocation(int id) {
        for (Location l : App.getLocations()) {
            if (l.getId() == id) {
                this.userLocation = l;
            }
        }
    }

    public void setUserLocation (Location newLocation) {
        this.userLocation = newLocation;
    }

    public int getLocationVisits() {
        return locationVisits;
    }

    public void setLocationVisits(int locationVisits) {
        this.locationVisits = locationVisits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }


}