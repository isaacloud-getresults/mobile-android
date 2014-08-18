package getresultsapp.sointeractve.pl.getresultsapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class UserData implements Serializable {


    private int userId;
    private Location userLocation;
    private String name, email, firstName;
    private String level;
    private String score;
    private String gainedAchievements;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getGainedAchievements() {
        return gainedAchievements;
    }

    public void setGainedAchievements(String gainedAchievements) {
        this.gainedAchievements = gainedAchievements;
    }

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