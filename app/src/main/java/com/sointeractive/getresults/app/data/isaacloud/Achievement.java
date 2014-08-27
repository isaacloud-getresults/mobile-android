package com.sointeractive.getresults.app.data.isaacloud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.sointeractive.getresults.app.pebble.responses.AchievementBadgeResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementDescriptionResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementInResponse;
import com.sointeractive.getresults.app.pebble.responses.EmptyResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Data store class for Achievements.
 *
 * @author Mateusz Renes
 */
public class Achievement {

    private String label, description, imageUrl;
    private int counter = 0;
    private boolean isGained;

    private int id;

    public Achievement(final JSONObject json, final boolean isGained, final int amount) throws JSONException {
        setId(json.getInt("id"));
        setLabel(json.getString("label"));
        setDesc(json.getString("description"));
        setGained(isGained);
        setCounter(amount);
    }

    public String getLabel() {
        return label;
    }

    void setLabel(final String label) {
        this.label = label;
    }

    public String getDesc() {
        return description;
    }

    void setDesc(final String desc) {
        this.description = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isGained() {
        return isGained;
    }

    void setGained(final boolean isGained) {
        this.isGained = isGained;
    }

    public String print() {
        return "Achievement: " + label + " " + description + " " + isGained;
    }

    public int getCounter() {
        return counter;
    }

    void setCounter(final int counter) {
        this.counter = counter;
    }

    public ResponseItem toAchievementResponse() {
        return new AchievementInResponse(id, label, description);
    }

    public Collection<ResponseItem> toAchievementDescriptionResponse() {
        final Collection<ResponseItem> descriptionResponse = AchievementDescriptionResponse.getResponse(id, description);
        final Collection<ResponseItem> badgeResponse = toAchievementBadgeResponse();
        descriptionResponse.addAll(badgeResponse);
        return descriptionResponse;
    }

    private Collection<ResponseItem> toAchievementBadgeResponse() {
        //<DEBUG_ONLY>
        // TODO: Remove this from code
        //Sample image from base64
        try {
            final String input = "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 6ZG6iXkIAAAAAElFTkSuQmCC";
            final byte[] decodedByte = Base64.decode(input, 0);
            final Bitmap icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            return AchievementBadgeResponse.getResponse(id, icon);
        } catch (final Exception e) {
            Log.e("BADGE", "Error");
            e.printStackTrace();
            final Collection<ResponseItem> responseItems = new LinkedList<ResponseItem>();
            responseItems.add(EmptyResponse.INSTANCE);
            return responseItems;
        }
        //</DEBUG_ONLY>
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Achievement that = (Achievement) o;

        if (counter != that.counter) return false;
        if (id != that.id) return false;
        if (isGained != that.isGained) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null)
            return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + counter;
        result = 31 * result + (isGained ? 1 : 0);
        result = 31 * result + id;
        return result;
    }
}