package getresultsapp.sointeractve.pl.getresultsapp.data;

// Data management class for downloading locations and users.
// @author: Pawel Dylag

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Location;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Person;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.AchievementsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.BeaconsCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.LoginCache;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.PeopleCache;

public class DataManager {

    private SparseArray<List<Person>> people = new SparseArray<List<Person>>();
    private List<Location> locations;
    private List<Achievement> achievements;

    public DataManager() {
        people = new SparseArray<List<Person>>();
        locations = new ArrayList<Location>();
        achievements = new ArrayList<Achievement>();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        BeaconsCache.INSTANCE.reload();
        LoginCache.INSTANCE.reload();
    }

    public List<Person> getPeopleAtLocation(Location l) {
        int id = l.getId();
        return people.get(id);
    }

    public List<Person> getPeople() {
        List<Person> peopleList = new LinkedList<Person>();
        for (int i = 0; i < people.size(); i++)
            peopleList.addAll(people.valueAt(i));
        return peopleList;
    }

    public void setPeople(SparseArray<List<Person>> people) {
        this.people = people;
        PeopleCache.INSTANCE.reload();
        BeaconsCache.INSTANCE.reload();
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        AchievementsCache.INSTANCE.reload();
        LoginCache.INSTANCE.reload();
    }
}
