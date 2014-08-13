package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.PersonIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.tasks.GetPeopleTask;

public class PeopleProvider {
    public static final PeopleProvider INSTANCE = new PeopleProvider();

    private Collection<PersonIC> peopleIC = new LinkedList<PersonIC>();

    private PeopleProvider() {
        // Exists only to defeat instantiation.
    }


    public Collection<PersonIC> getData() {
        reload();
        return peopleIC;
    }

    private void reload() {
        final GetPeopleTask getPeople = new GetPeopleTask();
        try {
            final Collection<PersonIC> newPeopleIC = getPeople.execute().get();
            if (!newPeopleIC.isEmpty()) {
                peopleIC = newPeopleIC;
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        peopleIC.clear();
    }
}
