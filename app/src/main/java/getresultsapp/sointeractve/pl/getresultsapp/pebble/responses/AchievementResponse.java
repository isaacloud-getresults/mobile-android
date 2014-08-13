package getresultsapp.sointeractve.pl.getresultsapp.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;

import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.config.PebbleSettings;
import getresultsapp.sointeractve.pl.getresultsapp.utils.DictionaryBuilder;

public class AchievementResponse implements ResponseItem {
    private static final int RESPONSE_ID = 4;

    private final int id;
    private final String name;
    private final String description;

    public AchievementResponse(final int id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public List<PebbleDictionary> getData() {
        int descriptionMaxLength = PebbleSettings.MAX_ACHIEVEMENTS_DESCRIPTION_STR_LEN;
        descriptionMaxLength -= name.length();

        final String descriptionToSend;
        if (description.length() > descriptionMaxLength) {
            descriptionToSend = description.substring(0, descriptionMaxLength - 3) + "...";
        } else {
            descriptionToSend = description;
        }

        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addString(descriptionToSend)
                .pack();

    }
}
