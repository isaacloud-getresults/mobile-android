package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class AchievementResponse implements ResponseItem {
    private static final int RESPONSE_ID = 4;
    private static final int BASE_SIZE = 28;

    private final int id;
    private final String name;
    private final String description;

    private int pageNumber = 0;

    private int isMoreResponsesOnPage = 1;
    public AchievementResponse(final int id, final String name, final String description) {
        this.id = id;
        this.name = StringTrimmer.getAchievementName(name);
        this.description = description;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(getDescriptionPartsNumber())
                .addInt(pageNumber)
                .addInt(isMoreResponsesOnPage)
                .build();
    }

    @Override
    public int getSize() {
        return BASE_SIZE + name.length();
    }

    private int getDescriptionPartsNumber() {
        final double stringLength = description.length();
        final double partSize = StringTrimmer.MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN;
        return (int) Math.ceil(stringLength / partSize);
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
}
