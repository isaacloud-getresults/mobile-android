package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;

public class PersonOutResponse implements ResponseItem {
    private static final int RESPONSE_ID = 6;
    private static final int BASE_SIZE = 28;

    private final int id;
    private final String name;
    private final int roomId;

    public PersonOutResponse(final int id, final String name, final int roomId) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
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
        return BASE_SIZE + name.length();
    }
}
