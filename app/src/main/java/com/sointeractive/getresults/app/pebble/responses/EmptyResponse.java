package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;

public class EmptyResponse implements ResponseItem {
    public static final ResponseItem INSTANCE = new EmptyResponse();

    private EmptyResponse() {
        // Exists only to defeat instantiation.
    }

    public PebbleDictionary getData() {
        return new PebbleDictionary();
    }

    @Override
    public int getSize() {
        return 0;
    }
}
