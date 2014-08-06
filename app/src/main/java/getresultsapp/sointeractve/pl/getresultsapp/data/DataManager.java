package getresultsapp.sointeractve.pl.getresultsapp.data;

// Data management class for downloading locations and users.
// @author: Pawel Dylag

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataManager {

    private SparseArray<List<Person>> people = new SparseArray<List<Person>>();
    private List<Location> locations;
    private List<Achievement> achievements;

    public DataManager() {
        people = new SparseArray<List<Person>>();
        locations = new ArrayList<Location>();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Person> getPeopleAtLocation (Location l) {
        if ( l != null) {
            int id = l.getId();
            return people.get(id);
        }
        else return null;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setPeople(SparseArray<List<Person>> people) {
        this.people = people;
    }


    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }
}
