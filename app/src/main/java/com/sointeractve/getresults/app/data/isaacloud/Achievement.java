package com.sointeractve.getresults.app.data.isaacloud;

import com.sointeractve.getresults.app.pebble.responses.AchievementDescriptionResponse;
import com.sointeractve.getresults.app.pebble.responses.AchievementResponse;
import com.sointeractve.getresults.app.pebble.responses.ResponseItem;

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

    public Achievement(JSONObject json, boolean isGained, int amount) throws JSONException {
        this.setId(json.getInt("id"));
        this.setLabel(json.getString("label"));
        this.setDesc(json.getString("description"));
        this.setGained(isGained);
        this.setCounter(amount);
    }

    public String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    public String getDesc() {
        return description;
    }

    void setDesc(String desc) {
        this.description = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isGained() {
        return isGained;
    }

    void setGained(boolean isGained) {
        this.isGained = isGained;
    }

    public String print() {
        return "Achievement: " + label + " " + description + " " + isGained;
    }

    public int getCounter() {
        return counter;
    }

    void setCounter(int counter) {
        this.counter = counter;
    }

    public ResponseItem toAchievementResponse() {
        return new AchievementResponse(id, label, description);
    }

    public Collection<ResponseItem> toAchievementDescriptionResponse() {
        final Collection<ResponseItem> responseItems = new LinkedList<ResponseItem>();
        responseItems.add(new AchievementDescriptionResponse(id, description));
        return responseItems;
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }
}