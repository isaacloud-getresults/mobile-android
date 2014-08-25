package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class PersonInResponse implements ResponseItem {
    private static final int RESPONSE_ID = 3;
    private static final int BASE_SIZE = 28;

    private final int id;
    private final String name;
    private final int roomId;

    private int pageNumber = 0;
    private int isMoreResponsesOnPage = 1;

    public PersonInResponse(final int id, final String name, final int roomId) {
        this.id = id;
        this.name = StringTrimmer.getCoworkerName(name);
        this.roomId = roomId;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(roomId)
                .addInt(pageNumber)
                .addInt(isMoreResponsesOnPage)
                .build();
    }

    @Override
    public int getSize() {
        return BASE_SIZE + name.length();
    }

    public ResponseItem toPersonOutResponse() {
        return new PersonOutResponse(id, name, roomId, pageNumber);
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

        final PersonInResponse that = (PersonInResponse) o;

        if (id != that.id) return false;
        if (roomId != that.roomId) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + roomId;
        return result;
    }
}
