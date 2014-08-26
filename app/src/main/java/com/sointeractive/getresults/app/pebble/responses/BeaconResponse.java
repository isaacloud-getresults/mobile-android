package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class BeaconResponse implements ResponseItem {
    private static final int RESPONSE_ID = 2;

    private final int id;
    private final String name;
    private final int people;
    private final int peoplePagesNumber;

    private int pageNumber = 0;
    private int isMoreResponsesOnPage = 1;

    public BeaconResponse(final int id, final String name, final int people, final int peoplePagesNumber) {
        this.id = id;
        this.name = StringTrimmer.getBeaconName(name);
        this.people = people;
        this.peoplePagesNumber = peoplePagesNumber;
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
                .addInt(peoplePagesNumber)
                .addInt(pageNumber)
                .addInt(isMoreResponsesOnPage)
                .build();
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setLast() {
        this.isMoreResponsesOnPage = 0;
    }

    public void setIsMore() {
        this.isMoreResponsesOnPage = 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BeaconResponse that = (BeaconResponse) o;

        if (id != that.id) return false;
        if (peoplePagesNumber != that.peoplePagesNumber) return false;
        if (people != that.people) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + people;
        result = 31 * result + peoplePagesNumber;
        return result;
    }
}
