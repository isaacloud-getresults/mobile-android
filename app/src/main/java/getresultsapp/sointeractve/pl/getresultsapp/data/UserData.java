package getresultsapp.sointeractve.pl.getresultsapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class UserData implements Serializable {


    private String name, email, firstName;
    private int userId;
    private int userLocation;

    public int getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(int userLocation) {
        this.userLocation = userLocation;
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