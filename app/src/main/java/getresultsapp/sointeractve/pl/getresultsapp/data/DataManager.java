package getresultsapp.sointeractve.pl.getresultsapp.data;

// Data management class for downloading locations and users.
// @author: Pawel Dylag

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataManager {

    SparseArray<List<Person>> people = new SparseArray<List<Person>>();
    private List<Location> locations;

    public DataManager() {
        people = new SparseArray<List<Person>>();
        locations = new ArrayList<Location>();
        // DEBUG MODE, TODO: NEED A SERVICE TO DOWNLOAD PEOPLE

    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Person> getPeopleAtLocation (Location l) {
        int id = l.getId();
        return people.get(id);
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setPeople(SparseArray<List<Person>> people) {
        this.people = people;
    }


}
