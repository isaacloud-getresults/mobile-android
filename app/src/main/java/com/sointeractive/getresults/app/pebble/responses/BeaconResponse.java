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

    public BeaconResponse(final int id, final String name, final int people) {
        this.id = id;
        this.name = StringTrimmer.getBeaconName(name);
        this.people = people;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSizeToReserve() {
        return BASE_SIZE + name.length();
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(people)
                .build();
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BeaconResponse that = (BeaconResponse) o;
        return id == that.id &&
                people == that.people &&
                name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + people;
        return result;
    }
}
