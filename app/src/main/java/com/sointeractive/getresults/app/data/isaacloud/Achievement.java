package com.sointeractive.getresults.app.data.isaacloud;

import com.sointeractive.getresults.app.pebble.responses.AchievementDescriptionResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementInResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

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
        return AchievementDescriptionResponse.getResponse(id, description);
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