package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class PersonInResponse implements ResponseItem {
    private static final int RESPONSE_ID = 3;
    private static final int BASE_SIZE = 28;

    private static int totalSize = 0;

    private final int id;
    private final String name;
    private final int roomId;

    public PersonInResponse(final int id, final String name, final int roomId) {
        this.id = id;
        this.name = StringTrimmer.getCoworkerName(name);
        this.roomId = roomId;
    }

    public static int getMemoryCleared() {
        final int size = totalSize;
        totalSize = 0;
        return size;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(roomId)
                .build();
    }

    @Override
    public int getSize() {
        final int size = BASE_SIZE + name.length();
        totalSize += size;
        return size;
    }

    public ResponseItem toPersonOutResponse() {
        return new PersonOutResponse(id, name, roomId);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PersonInResponse)) {
            return false;
        }

        final PersonInResponse personInResponse = (PersonInResponse) obj;
        return id == personInResponse.id;
    }

    public int hashCode() {
        return id;
    }
}
