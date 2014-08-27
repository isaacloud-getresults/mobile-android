package com.sointeractive.getresults.app.pebble.responses;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;
import com.sointeractive.getresults.app.pebble.responses.utils.StringTrimmer;

public class LoginResponse implements ResponseItem {
    private static final int RESPONSE_ID = 1;
    private static final int BASE_SIZE = 0;

    private final String roomName;
    private final String name;
    private final int points;
    private final int rank;
    private final int beaconsSize;
    private final int achievementsNumber;
    private final int pagesNumber;

    public LoginResponse(final String name, final int points, final int rank, final String roomName, final int beaconsSize, final int achievementsNumber, final int pagesNumber) {
        this.name = StringTrimmer.getUserName(name);
        this.points = points;
        this.rank = rank;
        this.roomName = StringTrimmer.getBeaconName(roomName);
        this.beaconsSize = beaconsSize;
        this.achievementsNumber = achievementsNumber;
        this.pagesNumber = pagesNumber;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addString(name)
                .addString(roomName)
                .addInt(points)
                .addInt(rank)
                .addInt(beaconsSize)
                .addInt(achievementsNumber)
                .addInt(pagesNumber)
                .build();
    }

    @Override
    public int getSize() {
        return BASE_SIZE;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LoginResponse that = (LoginResponse) o;

        if (achievementsNumber != that.achievementsNumber) return false;
        if (beaconsSize != that.beaconsSize) return false;
        if (pagesNumber != that.pagesNumber) return false;
        if (points != that.points) return false;
        if (rank != that.rank) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (roomName != null ? !roomName.equals(that.roomName) : that.roomName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roomName != null ? roomName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + points;
        result = 31 * result + rank;
        result = 31 * result + beaconsSize;
        result = 31 * result + achievementsNumber;
        result = 31 * result + pagesNumber;
        return result;
    }
}
