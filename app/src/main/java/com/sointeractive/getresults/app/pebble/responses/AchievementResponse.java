package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;

public class AchievementResponse implements ResponseItem {
    private static final int RESPONSE_ID = 4;
    private static final int BASE_SIZE = 28;

    private static int totalSize = 0;

    private final int id;
    private final String name;
    private final String description;

    public AchievementResponse(final int id, final String name, final String description) {
        this.id = id;
        this.name = getSafeLengthName(name, Settings.MAX_ACHIEVEMENT_NAME_STR_LEN);
        this.description = description;
    }

    public static int getMemoryCleared() {
        final int size = totalSize;
        totalSize = 0;
        return size;
    }

    private String getSafeLengthName(String name, int maxLength) {
        if (name.length() > maxLength) {
            return name.substring(0, maxLength);
        } else {
            return name;
        }
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(getDescriptionPartsNumber())
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
        final double partSize = Settings.MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN;
        return (int) Math.ceil(stringLength / partSize);
    }
}
