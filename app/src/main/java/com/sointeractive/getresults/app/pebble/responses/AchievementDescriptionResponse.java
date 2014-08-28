package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AchievementDescriptionResponse implements ResponseItem {
    private static final int RESPONSE_ID = 5;

    private final int id;
    private final String description;
    private final int descriptionPartId;

    public AchievementDescriptionResponse(final int id, final String description, final int descriptionPartId) {
        this.id = id;
        this.description = description;
        this.descriptionPartId = descriptionPartId;
    }

    public static Collection<ResponseItem> getResponse(final int id, final String description) {
        final List<ResponseItem> responses = new LinkedList<ResponseItem>();
        final Queue<String> descriptionParts = partitionDescription(description, StringTrimmer.MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN);

        int descriptionPartId = 0;
        while (!descriptionParts.isEmpty()) {
            final ResponseItem item = new AchievementDescriptionResponse(id, descriptionParts.poll(), descriptionPartId);
            responses.add(item);
            descriptionPartId += 1;
        }
        return responses;
    }

    private static Queue<String> partitionDescription(final String text, final int maxLength) {
        final Queue<String> descriptionParts = new LinkedList<String>();
        for (int start = 0; start < text.length(); start += maxLength) {
            descriptionParts.add(text.substring(start, Math.min(text.length(), start + maxLength)));
        }
        return descriptionParts;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(description)
                .addInt(descriptionPartId)
                .build();

    }
}