package getresultsapp.sointeractve.pl.getresultsapp.pebble.communication;

import java.util.Collection;

import getresultsapp.sointeractve.pl.getresultsapp.pebble.responses.ResponseItem;

interface Sendable {
    public Collection<ResponseItem> getSendable(final int query);
}
