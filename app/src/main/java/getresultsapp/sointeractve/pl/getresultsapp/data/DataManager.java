package getresultsapp.sointeractve.pl.getresultsapp.data;

// Data management class for downloading locations and users.
// @author: Pawel Dylag

import java.util.ArrayList;
import java.util.List;


public class DataManager {

    private List<Person> people;
    private List<Location> locations;

    public DataManager() {
        people = new ArrayList<Person>();
        locations = new ArrayList<Location>();
        // DEBUG MODE, TODO: NEED A SERVICE TO DOWNLOAD PEOPLE
        people.add(new Person("test", "test", 1, 1));
        people.add(new Person("test", "test", 2, 2));
        people.add(new Person("test", "test", 2, 3));
        people.add(new Person("test", "test", 3, 4));

    }

    public List<Person> getPeople() {
        return people;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Person> getPeopleAtLocation (Location l) {
        List<Person> array = new ArrayList<Person>();
        for (Person p: people) {
            if (p.getActualLocation() == l.getId()) {
                array.add(p);
            }
        }
        return array;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }


}
