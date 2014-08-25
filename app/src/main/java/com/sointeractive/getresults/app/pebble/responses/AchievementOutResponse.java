package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;

public class AchievementOutResponse implements ResponseItem {
    private static final int RESPONSE_ID = 7;
    private static final int BASE_SIZE = 28;

    private final int id;
    private final String name;
    private final int pageNumber;

    public AchievementOutResponse(final int id, final String name, final int pageNumber) {
        this.id = id;
        this.name = name;
        this.pageNumber = pageNumber;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(pageNumber)
                .build();
    }

    @Override
    public int getSize() {
        return BASE_SIZE + name.length();
    }
}
