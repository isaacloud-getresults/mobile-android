package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.config.Settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AchievementDescriptionResponse implements ResponseItem {
    private static final int RESPONSE_ID = 5;

    private final int id;
    private final String description;

    public AchievementDescriptionResponse(final int id, final String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public List<PebbleDictionary> getData() {
        final List<PebbleDictionary> data = new LinkedList<PebbleDictionary>();
        final Queue<String> descriptionParts = partitionDescription(description);

        int descriptionPartId = 0;
        while (!descriptionParts.isEmpty()) {
            final PebbleDictionary item = new DictionaryBuilder(RESPONSE_ID)
                    .addInt(id)
                    .addString(descriptionParts.poll())
                    .addInt(descriptionPartId)
                    .build();
            data.add(item);
            descriptionPartId += 1;
        }
        return data;
    }

    private Queue<String> partitionDescription(final String text) {
        final Queue<String> descriptionParts = new LinkedList<String>();
        for (int start = 0; start < text.length(); start += Settings.MAX_ACHIEVEMENTS_DESCRIPTION_STR_LEN) {
            descriptionParts.add(text.substring(start, Math.min(text.length(), start + Settings.MAX_ACHIEVEMENTS_DESCRIPTION_STR_LEN)));
        }
        return descriptionParts;
    }
}