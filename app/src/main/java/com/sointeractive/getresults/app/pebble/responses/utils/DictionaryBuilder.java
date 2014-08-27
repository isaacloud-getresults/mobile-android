package com.sointeractive.getresults.app.pebble.responses.utils;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.communication.Request;

public class DictionaryBuilder {
    private final PebbleDictionary dictionary = new PebbleDictionary();
    private int currentIndex = Request.RESPONSE_DATA_INDEX;

    public DictionaryBuilder(final int responseType) {
        dictionary.addInt16(Request.RESPONSE_TYPE, (short) responseType);
    }

    public DictionaryBuilder addString(final String value) {
        dictionary.addString(currentIndex, value);
        currentIndex += 1;
        return this;
    }

    public DictionaryBuilder addInt(final int value) {
        dictionary.addInt16(currentIndex, (short) value);
        currentIndex += 1;
        return this;
    }

    public DictionaryBuilder addBytes(final byte[] value) {
        dictionary.addBytes(currentIndex, value);
        currentIndex += 1;
        return this;
    }

    public PebbleDictionary build() {
        return dictionary;
    }
}
