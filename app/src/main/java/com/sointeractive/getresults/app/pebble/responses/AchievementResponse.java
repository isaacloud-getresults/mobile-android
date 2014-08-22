package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class AchievementResponse implements ResponseItem {
    private static final int RESPONSE_ID = 4;
    private static final int BASE_SIZE = 28;

    private static int totalSize = 0;

    private final int id;
    private final String name;
    private final String description;

    private int isMoreResponsesOnPage = 1;

    public AchievementResponse(final int id, final String name, final String description) {
        this.id = id;
        this.name = StringTrimmer.getAchievementName(name);
        this.description = description;
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
                .addInt(getDescriptionPartsNumber())
                .addInt(isMoreResponsesOnPage)
                .build();
    }

    @Override
    public int getSize() {
        final int size = BASE_SIZE + name.length();
        totalSize += size;
        return size;
    }

    private int getDescriptionPartsNumber() {
        final double stringLength = description.length();
        final double partSize = StringTrimmer.MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN;
        return (int) Math.ceil(stringLength / partSize);
    }

    public void setLast() {
        this.isMoreResponsesOnPage = 0;
    }

    public void setIsMore() {
        this.isMoreResponsesOnPage = 1;
    }
}
