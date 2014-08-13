package getresultsapp.sointeractve.pl.getresultsapp.pebble.communication;

import com.sointeractive.android.kit.util.PebbleDictionary;

import java.util.Collection;
import java.util.LinkedList;

import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

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
