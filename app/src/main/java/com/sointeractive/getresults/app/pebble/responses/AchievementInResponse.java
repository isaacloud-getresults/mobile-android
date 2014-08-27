package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class AchievementInResponse implements ResponseItem {
    private static final int RESPONSE_ID = 4;

    private final int id;
    private final String name;
    private final String description;

    private int pageNumber = 0;
    private int isMoreResponsesOnPage = 1;

    public AchievementInResponse(final int id, final String name, final String description) {
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

    private int getDescriptionPartsNumber() {
        final double stringLength = description.length();
        final double partSize = StringTrimmer.MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN;
        return (int) Math.ceil(stringLength / partSize);
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setIsLast() {
        this.isMoreResponsesOnPage = 0;
    }

    public void setIsMore() {
        this.isMoreResponsesOnPage = 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AchievementInResponse that = (AchievementInResponse) o;

        if (id != that.id) return false;
        if (!description.equals(that.description)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    public ResponseItem toAchievementOutResponse() {
        return new AchievementOutResponse(id, name, pageNumber);
    }
}
