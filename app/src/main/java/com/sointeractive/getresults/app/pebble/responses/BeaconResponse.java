package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class BeaconResponse implements ResponseItem {
    private static final int RESPONSE_ID = 2;
    private static final int BASE_SIZE = 28;

    private final int id;
    private final String name;
    private final int people;
    private int pagesNumber;

    public BeaconResponse(final int id, final String name, final int people, final int pagesNumber) {
        this.id = id;
        this.name = StringTrimmer.getBeaconName(name);
        this.people = people;
        this.pagesNumber = pagesNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(people)
                .addInt(pagesNumber)
                .build();
    }

    @Override
    public int getSize() {
        return BASE_SIZE + name.length();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BeaconResponse that = (BeaconResponse) o;

        if (id != that.id) return false;
        if (pagesNumber != that.pagesNumber) return false;
        if (people != that.people) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + people;
        result = 31 * result + pagesNumber;
        return result;
    }
}
