package getresultsapp.sointeractve.pl.getresultsapp.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;

import java.util.List;

import getresultsapp.sointeractve.pl.getresultsapp.utils.DictionaryBuilder;

public class PersonInResponse implements ResponseItem {
    private static final int RESPONSE_ID = 3;

    private final int id;
    private final String name;
    private final int roomId;

    public PersonInResponse(final int id, final String name, final int roomId) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
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
