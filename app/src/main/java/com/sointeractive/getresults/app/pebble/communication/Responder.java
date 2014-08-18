package com.sointeractive.getresults.app.pebble.communication;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;

public class Responder {
    public static void sendResponseItemsToPebble(final Collection<ResponseItem> data) {
        if (!data.isEmpty()) {
            final Collection<PebbleDictionary> responseData = makeResponseDictionary(data);
            App.getPebbleConnector().sendDataToPebble(responseData);
        }
    }

    private static Collection<PebbleDictionary> makeResponseDictionary(final Iterable<ResponseItem> data) {
        final Collection<PebbleDictionary> list = new LinkedList<PebbleDictionary>();
        for (final ResponseItem responseItem : data) {
            list.addAll(responseItem.getData());
        }
        return list;
    }
}
