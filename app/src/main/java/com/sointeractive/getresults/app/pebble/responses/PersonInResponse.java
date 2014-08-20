package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.config.Settings;

import java.util.List;

public class PersonInResponse implements ResponseItem {
    public static final int RESPONSE_ID = 3;

    private final int id;
    private final String name;
    private final int roomId;

    public PersonInResponse(final int id, final String name, final int roomId) {
        this.id = id;
        this.name = getSafeLengthName(name, Settings.MAX_COWORKER_FULL_NAME_STR_LEN);
        this.roomId = roomId;
    }

    private String getSafeLengthName(String name, int maxLength) {
        if (name.length() > maxLength) {
            return name.substring(0, maxLength);
        } else {
            return name;
        }
    }

    @Override
    public List<PebbleDictionary> getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addString(name)
                .addInt(roomId)
                .pack();
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
